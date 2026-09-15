// templatefun: the reusable build templates, moved here from KGround's `template-logic` so other
// repos can depend on them instead of copying the same regions into every build script.
//
// It is a SEPARATE subproject, not part of the root DepsKt jar, on purpose. Both of DepsKt's plugin
// ids (pl.mareklangiewicz.deps and pl.mareklangiewicz.deps.settings) ship from the root artifact,
// and the settings one is applied in settings.gradle.kts -- evaluated before anything else in every
// consuming build. The AGP / Compose / KMP dependencies below must not land on that classpath.

plugins {
  `kotlin-dsl`
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
  // The sibling Lib model lives in the root project of THIS repo now, so depend on it directly
  // instead of on a published version. One less pin to drift: template-logic's own pin was stale
  // at 0.4.26 while the repo was on 0.4.27.
  implementation(project(":"))
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
