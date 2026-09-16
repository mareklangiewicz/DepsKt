import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import com.vanniktech.maven.publish.*

// templatefun: the reusable build templates, moved here from KGround's `template-logic` so other
// repos can depend on them instead of copying the same regions into every build script.
//
// It is a SEPARATE subproject, not part of the DepsKt jar, on purpose. Both of DepsKt's plugin
// ids (pl.mareklangiewicz.deps and pl.mareklangiewicz.deps.settings) ship from the :deps artifact,
// and the settings one is applied in settings.gradle.kts -- evaluated before anything else in every
// consuming build. The AGP / Compose / KMP dependencies below must not land on that classpath.

// Kotlin comes from the root (`plug(plugs.KotlinJvm) apply false`), exactly like :deps and
// :sourcefun, so all three siblings are built by ONE compiler at vers.Kotlin. This project used to
// apply `kotlin-dsl` instead, which pins whatever applies it to the Kotlin EMBEDDED in Gradle
// (2.4.0 for Gradle 9.7.1) -- silent skew against its two siblings, plus a warning on every
// configure. See docs/design/templatefun-off-kotlin-dsl.md for the measurements.
//
// What `kotlin-dsl` was actually providing here was NOT a dependency (diffing the compile classpath
// with and without it differs by one jar, kotlin-reflect). It was two Kotlin COMPILER plugins,
// applied below. Without them the Gradle DSL does not typecheck: `Action<T>` is annotated
// @HasImplicitReceiver and only sam-with-receiver turns those lambdas into lambdas WITH receiver
// (removing kotlin-dsl gives 62 errors; sam-with-receiver alone takes it to 4), and
// `property = value` on a Gradle `Property<T>` comes from the assignment plugin via
// @SupportsKotlinAssignmentOverloading (those last 4).
plugins {
  plugAll(plugs.KotlinJvmNoVer, plugs.GradlePublish, plugs.VannikPublish) // version comes from the root
  // These two cannot say `vers.Kotlin`: a `plugins {}` block cannot see it. The literals are
  // asserted against it below instead, so they cannot drift silently.
  //
  // TODO after publishing 0.4.53: `plugs.KotlinSamWithReceiver` / `plugs.KotlinAssignment` now exist
  // (deps/src/main/kotlin/deps/Plugs.kt), both `.withVer(vers.Kotlin)`, which removes the literals
  // AND the check below -- the version stops being repeated at all. This script reads `plugs` from
  // the PUBLISHED deps.settings plugin, though, so the entries are invisible here until they ship.
  // Verified against 0.4.52, not assumed: `Unresolved reference 'KotlinSamWithReceiver'`. Once
  // published, replace the two lines below (and delete the check) with:
  //
  //   plugAll(plugs.KotlinSamWithReceiver, plugs.KotlinAssignment)
  id("org.jetbrains.kotlin.plugin.sam.with.receiver") version "2.4.20"
  id("org.jetbrains.kotlin.plugin.assignment") version "2.4.20"
}

// The gate for the two literals above. A `plugins {}` block is resolved before this script body
// runs, so this cannot pick the version -- but it CAN refuse to build when the two disagree, which
// is the whole risk of hardcoding them. Bumping vers.Kotlin without bumping them fails here, loudly.
val kotlinPluginVer = "2.4.20"
check(kotlinPluginVer == vers.Kotlin.str) {
  "The sam-with-receiver/assignment plugin versions in plugins {} say $kotlinPluginVer, " +
    "but vers.Kotlin is ${vers.Kotlin.str}. templatefun would be compiled by a different Kotlin " +
    "than :deps and :sourcefun -- the exact skew that dropping kotlin-dsl removed. Update both."
}

repositories {
  mavenCentral()
  google()
  gradlePluginPortal()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.4.20-Beta1")
  implementation("com.android.tools.build:gradle:9.3.0-rc02")
  implementation("org.jetbrains.compose:compose-gradle-plugin:1.12.0-beta01")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.37.0")
  // The sibling Lib model lives in the :deps sibling of THIS repo now, so depend on it directly
  // instead of on a published version. One less pin to drift: template-logic's own pin was stale
  // at 0.4.26 while the repo was on 0.4.27.
  implementation(project(":deps"))
  // `kotlin-dsl` used to add this implicitly. The Gradle API itself is always on the classpath;
  // this is the kotlin-dsl EXTENSIONS (org.gradle.kotlin.dsl.*), which these templates import.
  implementation(gradleKotlinDsl())
}

// Match :deps (jvmToolchain(23)). Without this, the toolchain is whatever JDK ran the publish, and
// the published Gradle module metadata says so: templatefun 0.4.28-0.4.30 all shipped
// org.gradle.jvm.version = 25 because they were published from a JDK 25 machine, which made them
// unresolvable for any consumer on an older JVM. Marek's CI runs Java 23, so it could not apply the
// plugin at all. A published plugin's minimum JVM must be a decision, not a property of the
// publisher's laptop.
kotlin {
  jvmToolchain(23)
}

// What `kotlin-dsl` was really providing -- see the note above plugins {}.
samWithReceiver { annotation("org.gradle.api.HasImplicitReceiver") }
assignment { annotation("org.gradle.api.SupportsKotlinAssignmentOverloading") }

// Only these sources get the flag. Consuming build scripts are always compiled WITHOUT it, which is
// why every entry point takes `lib: Lib` as an ordinary parameter and opens the context scopes
// itself -- see the probes in KGround's kgroundx-experiments.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
    freeCompilerArgs.add("-Xexplicit-context-arguments")
  }
}

// Published so repos other than KGround can stop copying build-script regions and just apply the
// plugin. The id is pl.mareklangiewicz.templatefun, registered explicitly in gradlePlugin {} below:
// `kotlin-dsl` used to derive it from a precompiled script plugin's FILE NAME, and that script is
// gone with it.
val myLib = gradle.extLib

// Set here, not inherited: the root project deliberately has no group. Besides the plugin marker,
// this is what makes composite-build substitution find this project by pl.mareklangiewicz.deps:
// templatefun -- publication coordinates alone did not (measured: probe-logic could not resolve it).
defaultGroupAndVerAndDescription(myLib)

gradlePlugin {
  website = myLib.info.githubUrl
  vcsUrl = myLib.info.githubUrl
  plugins {
    create("templateFunPlugin") {
      id = "pl.mareklangiewicz.templatefun"
      implementationClass = "pl.mareklangiewicz.templatefun.TemplateFunPlugin"
      displayName = "DepsKt templatefun plugin"
      description = "Reusable gradle build templates for typical kotlin/android/compose projects."
      tags = listOf("template", "convention", "build-logic")
    }
  }
}

// Publishing is spelled out here instead of calling the [[Kotlin Module Build Template]] region's
// defaultPublishing: that region is a per-script COPY, not published DepsKt API, and it lives in
// deps/build.gradle.kts. Sharing it would mean this build script depending on that one.
// artifactId is the project name (templatefun); group is shared -> pl.mareklangiewicz.deps:templatefun.
mavenPublishing {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (myLib.flags.withCentralPublish) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  coordinates(groupId = myLib.info.group, artifactId = name, version = myLib.info.version.str)
  pom {
    name = myLib.info.name + " templatefun"
    description = "Reusable gradle build templates for typical kotlin/android/compose projects."
    url = myLib.info.githubUrl
    licenses { license { name = myLib.info.licenceName; url = myLib.info.licenceUrl } }
    developers {
      developer {
        id = myLib.info.authorId; name = myLib.info.authorName; email = myLib.info.authorEmail
      }
    }
    scm { url = myLib.info.githubUrl }
  }
}
