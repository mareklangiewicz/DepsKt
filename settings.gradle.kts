import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "DepsKt"


// region [[My Settings Stuff]]

// https://docs.gradle.org/current/userguide/upgrading_version_9.html#opt_into_gradle_10_behavior_by_disabling_implicit_lookup_in_parent_projects
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  // Opt-in through the environment, so this region is identical in every project and no flag has
  // to live above it and be kept in sync. Unset means off. To enable for one run:
  //   ENABLE_LOCAL_DEPSKT_IN_DIR=/home/marek/code/kotlin/DepsKt ./gradlew build
  val enableLocalDepsKtInDir = System.getenv("ENABLE_LOCAL_DEPSKT_IN_DIR")?.let { File(it).normalize() }
  // The env var reaches nested builds too, so DepsKt's own copy of this region sees it: skip self.
  // Pass a String: this scope's includeBuild takes only String, and a File silently resolves to the
  // outer Settings.includeBuild, a plain composite that never offers DepsKt's PLUGINS.
  if (enableLocalDepsKtInDir != null && enableLocalDepsKtInDir != rootDir.normalize()) {
    logger.warn("Including local build $enableLocalDepsKtInDir")
    includeBuild(enableLocalDepsKtInDir.path)
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.4.66" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.6.0" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    // Opt-in through the environment; unset means no scan is ever published, which is what keeps
    // private repos safe without anyone remembering to switch them off. A public repo turns it on
    // in its own CI workflow:  ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE=true
    // Read into a local at configuration time: `onlyIf` runs at the END of the build, and reading
    // a settings-script top-level `val` from there would capture the script OBJECT, which the
    // configuration cache rejects. A local is captured by value.
    val enabled = System.getenv("ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE") == "true"
    publishing.onlyIf { enabled && it.buildResult.failures.isNotEmpty() }
  }
}

// endregion [[My Settings Stuff]]

gradle.extLib = lib(
  info = myLibInfo(
    name = "DepsKt",
    group = "pl.mareklangiewicz.deps", // important non default ...deps group (as accepted on gradle portal)
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support).",
    githubUrl = "https://github.com/mareklangiewicz/DepsKt",
    version = Ver(0, 4, 66), // also sync it in ./deps/src/main/kotlin/deps/Vers.kt
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
