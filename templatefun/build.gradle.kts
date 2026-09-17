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
  // Both carry vers.Kotlin, like KotlinJvm does. They need no root `apply false`: that exists to
  // stop the KOTLIN GRADLE PLUGIN being loaded by two subprojects, and these two are applied only
  // here. So the Kotlin version is stated in exactly one place, Vers.kt -- these used to be two
  // "2.4.20" literals (a `plugins {}` block cannot read vers.Kotlin) guarded by a check that failed
  // the build when they drifted. The check is gone with the literals it guarded.
  plugAll(plugs.KotlinSamWithReceiver, plugs.KotlinAssignment)
}

repositories {
  mavenCentral()
  google()
  gradlePluginPortal()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.4.20-Beta1")
  implementation("com.android.tools.build:gradle:9.4.0")
  implementation("org.jetbrains.compose:compose-gradle-plugin:1.12.0-beta01")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.37.0")
  // The sibling Lib model lives in the :deps sibling of THIS repo now, so depend on it directly
  // instead of on a published version. One less pin to drift: template-logic's own pin was stale
  // at 0.4.26 while the repo was on 0.4.27.
  implementation(project(":deps"))
  // `kotlin-dsl` used to add this implicitly. The Gradle API itself is always on the classpath;
  // this is the kotlin-dsl EXTENSIONS (org.gradle.kotlin.dsl.*), which these templates import.
  implementation(gradleKotlinDsl())

  // Dependabot alerts on this repo are all AGP transitives landing on THIS project's runtime
  // classpath (:deps and :sourcefun resolve clean). AGP 9.4.0 already carries the bouncycastle bump
  // that closed the critical GOST one; these constraints cover the rest, which AGP still ships at
  // vulnerable versions. Constraints -- not `force` -- so a later AGP that fixes these upstream
  // simply wins on its own, and they publish in templatefun's module metadata for consumers too.
  // Drop each line once AGP ships at or above it.
  constraints {
    implementation("org.bouncycastle:bcprov-jdk18on:1.84") { because("LDAP injection; GHSA alert #27") }
    implementation("org.bouncycastle:bcpkix-jdk18on:1.84") { because("risky crypto algorithm; alert #26") }
    implementation("org.bouncycastle:bcutil-jdk18on:1.84") { because("kept in lockstep with bcprov/bcpkix") }
    implementation("org.bitbucket.b_c:jose4j:0.9.6") { because("DoS via compressed JWE content; alert #32") }
    implementation("org.jdom:jdom2:2.0.6.1") { because("XXE injection, via jetifier-processor; alert #31") }
    implementation("org.apache.commons:commons-lang3:3.18.0") { because("uncontrolled recursion; alert #30") }
  }
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
