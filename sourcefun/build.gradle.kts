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

// Hand the tests the sample project's location instead of letting them guess it from $HOME or
// $GITHUB_WORKSPACE. Both guesses survived the move from the standalone SourceFun repo while
// pointing at the wrong tree -- see the kdoc on sampleSourceFunProjectPath in SourceFunTests.kt.
tasks.withType<Test>().configureEach {
  val samplePath = layout.projectDirectory.dir("sample-sourcefun").asFile
  systemProperty("sourcefun.sampleProjectPath", samplePath.absolutePath)
  // The tests run gradle in there and rewrite its sources, so it is an input in every sense.
  inputs.dir(samplePath).withPropertyName("sampleSourceFunProject")
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

// Publishing is spelled out here rather than calling templatefun's defaultPublishing, for the same
// reason :deps pins its artifactId: this project's name is "sourcefun" (it follows its DIRECTORY)
// while the published artifact has always been pl.mareklangiewicz.deps:SourceFun. Nothing would
// error if that drifted -- it would just publish a new, empty-history coordinate. So the artifactId
// is pinned, explicitly.
//
// The artifactId alone is no longer a reason to hand-roll this: defaultPublishing takes it as a
// parameter from 0.4.52. The POM still is -- this module needs its own `name` and `description`,
// while defaultPOM puts the per-REPO info.name/description on every module. That is the same
// repo/module split, one level up, and the note on defaultPublishing sketches where it would go
// (a small per-module value, rather than patching one field at a time). Worth revisiting if a
// third module ever wants a module-specific POM; until then this copy is the cheaper answer.
//
// The POM identity is SourceFun's own (name/description), but url and scm now point at DepsKt: this
// is where the sources live.
mavenPublishing {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (myLib.flags.withCentralPublish) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  coordinates(groupId = myLib.info.group, artifactId = "SourceFun", version = myLib.info.version.str)
  pom {
    name = "SourceFun"
    description = "Maintain typical java/kotlin/android projects sources with fun."
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
