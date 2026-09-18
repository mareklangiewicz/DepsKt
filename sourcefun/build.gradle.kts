@file:Suppress("UnstableApiUsage", "unused")

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*
import com.vanniktech.maven.publish.*

// sourcefun: the SourceFun gradle plugin (id pl.mareklangiewicz.sourcefun), moved here from its own
// repo (~/code/kotlin/SourceFun) as the third sibling. See docs/design/lib-details-denesting.md.
//
// It is NOT circular that :deps applies the sourcefun plugin in its own build script: it applies the
// PUBLISHED one, a finished artifact from a previous release, exactly like it applies published
// templatefun and published deps.settings. A sibling subproject could not supply a plugin to another
// subproject's build script anyway -- build-script plugins resolve through pluginManagement.

plugins {
  // Note: I could probably easily include all deps in fat jar by just adding: plug(plugs.GradleShadow),
  // but let's not do it yet; someday maybe (so I can publish all my new kground stuff fast from maven local)
  // https://docs.gradle.org/current/userguide/publishing_gradle_plugins.html#shadow_dependencies
  plugAll(plugs.KotlinJvmNoVer, plugs.GradlePublish, plugs.VannikPublish) // version comes from the root
  plug(plugs.TemplateFun) // the PUBLISHED one -- see the note above
}

repositories {
  // No mavenLocal() here on purpose (it is deprecated in this codebase: LibRepos.withMavenLocal).
  google()
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  api(Com.SquareUp.Okio.okio)
  api(Langiewicz.kground)
  api(Langiewicz.kgroundx)
  api(Langiewicz.kground_io)
  api(Langiewicz.kgroundx_io)
  api(Langiewicz.kgroundx_maintenance)
  api(Langiewicz.kommand_line)
  api(Langiewicz.kommand_samples)
  testImplementation(Langiewicz.uspekx_junit5)
  testImplementation(Org.JUnit.Jupiter.junit_jupiter)
  testImplementation(Org.JUnit.Jupiter.junit_jupiter_engine)
  testRuntimeOnly(Org.JUnit.Platform.junit_platform_launcher)
  // Explicit platform launcher required in new gradle:
  // https://docs.gradle.org/8.13/userguide/upgrading_version_8.html#test_framework_implementation_dependencies

  // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc.)
}

val kgVer = "0.1.32" // https://central.sonatype.com/artifact/pl.mareklangiewicz/kground/versions

setMyWeirdSubstitutions(
  "kground" to kgVer,
  "kgroundx" to kgVer,
  "kground-io" to kgVer,
  "kgroundx-io" to kgVer,
  "kgroundx-maintenance" to kgVer,
  "kommand-line" to kgVer,
  "kommand-samples" to kgVer,
)

// From the applied templatefun plugin now; the copied [[Kotlin Module Build Template]] region is gone.
tasks.defaultKotlinCompileOptions()

tasks.defaultTestsOptions()

// Hand the tests the samplefun project's location instead of letting them guess it from $HOME or
// $GITHUB_WORKSPACE. Both guesses survived the move from the standalone SourceFun repo while
// pointing at the wrong tree -- see the kdoc on sampleFunProjectPath in SourceFunTests.kt.
//
// samplefun is a SIBLING of sourcefun now, not a child, so this reaches through rootProject rather
// than through this project's own directory. The system property was renamed along with it, so a
// stale `sourcefun.sampleProjectPath` reader fails at the chkNN instead of silently seeing null.
tasks.withType<Test>().configureEach {
  val samplePath = rootProject.layout.projectDirectory.dir("samplefun").asFile
  systemProperty("sourcefun.samplefunProjectPath", samplePath.absolutePath)
  // The tests run gradle in there and rewrite its sources, so it is an input in every sense.
  inputs.dir(samplePath).withPropertyName("sampleFunProject")
}

// The lib is defined once, in settings.gradle.kts, and read here -- same as :deps and :templatefun.
// Named myLib, not lib, so the local does not shadow the lib(..) factory it is built with.
val myLib = gradle.extLib

// Set here, not inherited from the root: the root deliberately has no group (see ../build.gradle.kts).
// The gradlePlugin marker publications read project.group/version, so this is what puts
// pl.mareklangiewicz.deps on them.
defaultGroupAndVerAndDescription(myLib)

// Match :deps and :templatefun. Without this the toolchain is whatever JDK ran the publish, and the
// published module metadata says so -- that is how templatefun 0.4.28-0.4.30 shipped
// org.gradle.jvm.version = 25 and became unresolvable on Marek's Java 23 CI. A published plugin's
// minimum JVM must be a decision, not a property of the publisher's laptop.
kotlin {
  jvmToolchain(23)
}

// Publishing is NO LONGER spelled out here. It used to be, for two reasons, and 0.4.63 removed
// both: the artifactId (this project's name follows its DIRECTORY, "sourcefun", while the published
// artifact has always been pl.mareklangiewicz.deps:SourceFun) became a defaultPublishing parameter
// in 0.4.52, and the module-specific POM name/description -- the last reason to keep the copy --
// became LibPublish.pomName/pomDescription. See docs/design/publish-intent-per-module.md.
//
// url and scm come from defaultPOM as info.githubUrl, which is DepsKt: this is where the sources
// live now. That matches what the hand-rolled block already said.
//
// Flattened coercion, same as in deps/build.gradle.kts: defaultPublishing is
// `context(info: LibInfo, publish: LibPublish) fun Project.defaultPublishing()` and build scripts
// are compiled WITHOUT -Xcontext-parameters, so the context parameters come first and the extension
// receiver last. A function reference cannot use default arguments, hence the explicit LibPublish.
val tfDefaultPublishing: (LibInfo, LibPublish, Project) -> Unit = Project::defaultPublishing
tfDefaultPublishing(
  myLib.info,
  LibPublish(
    artifactId = "SourceFun",
    pomName = "SourceFun",
    pomDescription = "Maintain typical java/kotlin/android projects sources with fun.",
    // toCentral stays false: these artifacts ship to the Gradle Plugin Portal only. See
    // docs/design/releasing.md, "The Maven Central path, and why it is inert".
  ),
  project,
)

gradlePlugin {
  website = myLib.info.githubUrl
  vcsUrl = myLib.info.githubUrl
  plugins {
    create("sourceFunPlugin") {
      id = "pl.mareklangiewicz.sourcefun"
      implementationClass = "pl.mareklangiewicz.sourcefun.SourceFunPlugin"
      displayName = "SourceFun plugin"
      description = "Maintain typical java/kotlin/android projects sources with fun."
      tags = listOf("SourceTask", "DSL")
    }
  }
}

// Kind of experimental/temporary.. not sure how it will evolve yet,
// but currently I need these kind of substitutions/locals often enough
// especially when updating kground <-> kommandline (trans deps issues).
// Kept local on purpose: templatefun does not export it, and it is project-specific.
fun Project.setMyWeirdSubstitutions(
  vararg rules: Pair<String, String>,
  myProjectsGroup: String = "pl.mareklangiewicz",
  tryToUseLocalProjects: Boolean = true,
) {
  val foundLocalProjects: Map<String, Project?> =
    if (tryToUseLocalProjects) rules.associate { it.first to findProject(":${it.first}") }
    else emptyMap()
  configurations.all {
    resolutionStrategy.dependencySubstitution {
      for ((projName, projVer) in rules)
        substitute(module("$myProjectsGroup:$projName"))
          .using(
            // Note: there are different fun in gradle: Project.project; DependencySubstitution.project
            if (foundLocalProjects[projName] != null) project(":$projName")
            else module("$myProjectsGroup:$projName:$projVer")
          )
    }
  }
}
