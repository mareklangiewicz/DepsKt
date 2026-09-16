import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "sample-sourcefun"

// Note: Not using special region: 'My Settings Stuff', because pluginManagement has to differ: includeBuild("..")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }

  // ".." is the DepsKt root now that SourceFun lives in DepsKt/sourcefun. So this composite supplies
  // BOTH the sourcefun plugin under test and the deps/deps.settings/templatefun plugins, all from
  // source -- which is exactly what this sample is for.
  // Needed also as a workaround for TestKit issue with classloader
  // (see comments in sourcefun/src/test/kotlin/SourceFunTests.kt)
  includeBuild("../..")
}

plugins {
  // Version is irrelevant while includeBuild above substitutes it; kept at the latest published one
  // so the sample still resolves if the composite is ever switched off.
  id("pl.mareklangiewicz.deps.settings") version "0.4.54" // https://plugins.gradle.org/search?term=mareklangiewicz
}

// This standalone build defines its own Lib, exactly like every consumer repo does. It is NOT the
// DepsKt one: the sample publishes nothing and is not a DepsKt artifact.
gradle.extLib = lib(
  info = myLibInfo(
    name = "Sample-SourceFun",
    description = "Sample-SourceFun",
    githubUrl = "https://github.com/mareklangiewicz/DepsKt/tree/master/sourcefun/sample-sourcefun",
    version = Ver(0, 1, 9),
  ),
  flags = LibFlags(withJs = false, withLinuxX64 = false, withCentralPublish = false),
  withCompose = false,
)

include(":sample-lib")
