import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "DepsKt"


// Careful with auto publishing fails/stack traces
val buildScanPublishingAllowed =
  System.getenv("GITHUB_ACTIONS") == "true"
  // true
  // false

// region [[My Settings Stuff <~~]]
// ~~>".*/Deps\.kt"~~>"../DepsKt"<~~
// endregion [[My Settings Stuff <~~]]
// region [[My Settings Stuff]]

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  val depsDir = File(rootDir, "../DepsKt").normalize()
  val depsInclude =
    // depsDir.exists()
    false
  if (depsInclude) {
    logger.warn("Including local build $depsDir")
    includeBuild(depsDir)
  }
}

plugins {
  // Self-hosting on the PUBLISHED settings plugin, so this can only ever name a version that is
  // already on the portal. It lags the version below between a bump and a publish.
  id("pl.mareklangiewicz.deps.settings") version "0.4.62" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.5.1" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { buildScanPublishingAllowed && it.buildResult.failures.isNotEmpty() }
  }
}

// endregion [[My Settings Stuff]]

gradle.extLib = lib(
  info = myLibInfo(
    name = "DepsKt",
    group = "pl.mareklangiewicz.deps", // important non default ...deps group (as accepted on gradle portal)
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support).",
    githubUrl = "https://github.com/mareklangiewicz/DepsKt",
    version = Ver(0, 4, 63), // also sync it in ./deps/src/main/kotlin/deps/Vers.kt
    // TODO use some SourceFun task to make sure it's synced with Vers.DepsPlug
    // (we println it when applying plugin so have to be synced not to confuse users)
    // https://plugins.gradle.org/search?term=pl.mareklangiewicz
  ),
  flags = LibFlags(withJs = false),
  withCompose = false, // presence, stated as presence
)

// Three siblings under an empty root, not a library root with satellites. See
// docs/design/lib-details-denesting.md, "DepsKt as a multi-project build".
//
// :deps is the published DepsKt artifact (artifactId is pinned in deps/build.gradle.kts).
// :sourcefun is the SourceFun gradle plugin, moved in from its own repo (artifactId pinned the same
// way, to SourceFun). :deps applies its PUBLISHED build, which is not circular -- see the note at
// the top of sourcefun/build.gradle.kts.
// :templatefun is the reusable build templates, moved here from KGround/template-logic. It is kept
// out of the :deps artifact on purpose: both deps plugin ids -- including the settings one, applied
// before anything else in every consuming build -- ship from :deps, and templatefun's AGP / Compose
// / KMP classpath must not land there.
//
// The project is named after its DIRECTORY, and publishes under an artifactId override. It was
// briefly renamed to "DepsKt" so that a consumer's includeBuild("../DepsKt") -- which substitutes
// by project.group:project.name -- would bind to it. That is dropped on purpose: the local
// composite is a convenience nobody keeps switched on (depsInclude is `false` in every repo), and
// it is not worth a project whose path and directory disagree, two projects named DepsKt in one
// build, and a root that must never be given a group. The escape hatch still exists; with the name
// back to "deps" it simply no longer substitutes the deps artifact (templatefun still matches),
// and Gradle degrades to the published jar silently, as it always did when a rule stopped matching.
include(":deps")
include(":templatefun")
include(":sourcefun")
