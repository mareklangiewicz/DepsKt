import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "samplefun"

// Note: Not using special region: 'My Settings Stuff', because pluginManagement has to differ: includeBuild("..")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }

  // ".." is the DepsKt root, since samplefun sits directly under it, as a sibling of sourcefun.
  // So this composite supplies BOTH the sourcefun plugin under test and the
  // deps/deps.settings/templatefun plugins, all from source -- which is exactly what this is for.
  // Needed also as a workaround for TestKit issue with classloader
  // (see comments in sourcefun/src/test/kotlin/SourceFunTests.kt)
  includeBuild("..")
}

plugins {
  // Version is irrelevant while includeBuild above substitutes it; kept at the latest published one
  // so the sample still resolves if the composite is ever switched off.
  id("pl.mareklangiewicz.deps.settings") version "0.4.67" // https://plugins.gradle.org/search?term=mareklangiewicz
}

// This standalone build defines its own Lib, exactly like every consumer repo does. It is NOT the
// DepsKt one: samplefun is not a DepsKt artifact and never goes to Maven Central.
//
// `withCentralPublish = false` is gone from the flags because the flag itself is gone -- publish
// intent is per-MODULE now and is passed to the template, not inherited through LibFlags. See
// ../docs/design/publish-intent-per-module.md. :sample-lib opts in with LibPublish() (local
// publications only, toCentral = false), which makes this build the one place that actually RUNS
// the new publishing path against the local DepsKt via includeBuild("..").
//
// A playground, deliberately outside the root build: samples, TestKit/GradleRunner experiments and
// anything else that is allowed to be SLOW, so it stays opt-in instead of taxing every `./gradlew
// build`. sourcefun's own tests against it are disabled by default -- see SourceFunTests.kt.
gradle.extLib = lib(
  info = myLibInfo(
    name = "SampleFun",
    description = "DepsKt playground: samples and slow, opt-in gradle TestKit experiments.",
    githubUrl = "https://github.com/mareklangiewicz/DepsKt/tree/master/samplefun",
    version = Ver(0, 1, 9),
  ),
  flags = LibFlags(withJs = false, withLinuxX64 = false),
  withCompose = false,
)

include(":sample-lib")
