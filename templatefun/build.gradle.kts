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

plugins {
  `kotlin-dsl`
  plugAll(plugs.GradlePublish, plugs.VannikPublish)
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
}

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
// plugin. The id is pl.mareklangiewicz.templatefun, from the precompiled script plugin file name
// (src/main/kotlin/pl.mareklangiewicz.templatefun.gradle.kts) -- kotlin-dsl registers it.
val myLib = gradle.extLib

// Set here, not inherited: the root project deliberately has no group. Besides the plugin marker,
// this is what makes composite-build substitution find this project by pl.mareklangiewicz.deps:
// templatefun -- publication coordinates alone did not (measured: probe-logic could not resolve it).
defaultGroupAndVerAndDescription(myLib)

gradlePlugin {
  website = myLib.info.githubUrl
  vcsUrl = myLib.info.githubUrl
  plugins.configureEach {
    displayName = "DepsKt templatefun plugin"
    description = "Reusable gradle build templates for typical kotlin/android/compose projects."
    tags = listOf("template", "convention", "build-logic")
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
