@file:Suppress("UnstableApiUsage", "unused")

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import com.vanniktech.maven.publish.*
import okio.Path.Companion.toOkioPath
import pl.mareklangiewicz.kgroundx.maintenance.*
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.ure.UReplacement.Companion.Group
import pl.mareklangiewicz.ure.UReplacement.Companion.Literal
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.sourcefun.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  plugAll(plugs.KotlinJvm, plugs.GradlePublish, plugs.VannikPublish, plugs.SourceFun)
}

repositories {
  mavenLocal()
  google()
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  api(Com.SquareUp.Okio.okio) // FIXME_later: remove and use new SourceFun? (DepsKt utils should not depend on okio)
  testImplementation(kotlin("test"))
  testImplementation(Org.JUnit.Jupiter.junit_jupiter_engine)
  testRuntimeOnly(Org.JUnit.Platform.junit_platform_launcher)
}

tasks.defaultKotlinCompileOptions()

tasks.defaultTestsOptions()

val details = myLibDetails(
  name = "DepsKt",
  group = "pl.mareklangiewicz.deps", // important non default ...deps group (as accepted on gradle portal)
  description = "Updated dependencies for typical java/kotlin/android projects (with IDE support).",
  githubUrl = "https://github.com/mareklangiewicz/DepsKt",
  version = Ver(0, 3, 88), // also sync it in ./src/main/kotlin/deps/Vers.kt
  // TODO use some SourceFun task to make sure it's synced with Vers.DepsPlug
  // (we println it when applying plugin so have to be synced not to confuse users)
  // https://plugins.gradle.org/search?term=pl.mareklangiewicz
  settings = LibSettings(
    withJs = false,
    compose = null,
  ),
)

defaultBuildTemplateForRootProject(details)

kotlin {
  jvmToolchain(23)
}

defaultPublishing(details)

gradlePlugin {
  website.set("https://github.com/mareklangiewicz/DepsKt")
  vcsUrl.set("https://github.com/mareklangiewicz/DepsKt")
  plugins {
    create("depsPlugin") {
      id = "pl.mareklangiewicz.deps"
      implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
      displayName = "DepsKt plugin"
      description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."
      tags.set(listOf("bom", "dependencies"))
    }
    create("depsSettingsPlugin") {
      id = "pl.mareklangiewicz.deps.settings"
      implementationClass = "pl.mareklangiewicz.deps.DepsSettingsPlugin"
      displayName = "DepsKt settings plugin"
      description =
        "Updated dependencies for typical java/kotlin/android projects (with IDE support) (settings plugin)."
      tags.set(listOf("bom", "dependencies"))
    }
  }
}


val pathToSrcKotlin = rootProjectPath / "src/main/kotlin"
val urlToRefreshDeps = "https://raw.githubusercontent.com/mareklangiewicz/refreshDeps"
val urlToObjectsFile = "$urlToRefreshDeps/main/plugins/dependencies/src/test/resources/objects-for-deps.txt"

val downloadGeneratedDeps by tasks.registering(DownloadFileTask::class) {
  group = "maintenance"
  inputUrl.set(urlToObjectsFile)
  outputFile.set(layout.buildDirectory.file("objects-for-deps.txt"))
}


@OptIn(DelicateApi::class)
sourceFun {
  grp = "maintenance"
  val updateGeneratedDeps by reg {
    doNotTrackState("Injecting to Deps.kt file which belong to other (compilation) task(s).")
    setSource(downloadGeneratedDeps)
    setOutput(pathToSrcKotlin / "deps")
    setTaskAction { srcTree, outDir ->
      val inPath = srcTree.files.single().toOkioPath()
      val outPath = outDir.file("Deps.kt").asFile.toOkioPath()
      runWithUCtxForTask { outPath.injectSpecialRegionContentFromFile("Deps Generated", inPath) }
    }
  }
}

// Note: Leaving here older version to document and experiment with different approaches more
// (this one uses temp file in home dir not managed by gradle - see downloadAndInjectfileToSpecialRegion)
val updateGeneratedDepsAlternative by tasks.registering {
  group = "maintenance"
  doLastWithUCtxForTask {
    downloadAndInjectFileToSpecialRegion(
      inFileUrl = urlToObjectsFile,
      outFilePath = pathToSrcKotlin / "deps/Deps.kt",
      outFileRegionLabel = "Deps Generated",
    )
  }
}

@OptIn(ExperimentalApi::class)
val updateSomeRegexes by tasks.registering {

  group = "maintenance"

  val ureNewVersionPart = ure {
    0..MAX of ch('0') // have to ignore leading zeros because these confuse parser later (potentially octal)
    1 of ure {
      1..4 of chDigit
    }.withName("VersionPart")
    0..MAX of chWordOrDash
  }
  val theNewThing = "Regex(\"\"\"${ureNewVersionPart.compile()}\"\"\")"
  val ureWithTheOldThing = ure {
    +ureText("fun String.toVersionPartIntCode(): Int = ").withName("beforeTheThing")
    +ure("theOldThing") {
      +ureText("Regex")
      +ureWhatevaInLine()
    }
    +ureText(".matchEntire(this)").withName("afterTheThing")
  }

  doLastWithUCtxForTask {
    val path = pathToSrcKotlin / "utils/Utils.kt"
    processFile(path, path) {
      it.replaceSingle(ureWithTheOldThing, Group("beforeTheThing") + Literal(theNewThing) + Group("afterTheThing"))
    }
  }
}



// region [[Root Build Template]]

fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
  }
}

// endregion [[Root Build Template]]

// region [[Kotlin Module Build Template]]

// Kind of experimental/temporary.. not sure how it will evolve yet,
// but currently I need these kind of substitutions/locals often enough
// especially when updating kground <-> kommandline (trans deps issues)
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

fun RepositoryHandler.addRepos(settings: LibReposSettings) = with(settings) {
  @Suppress("DEPRECATION")
  if (withMavenLocal) mavenLocal()
  if (withMavenCentral) mavenCentral()
  if (withGradle) gradlePluginPortal()
  if (withGoogle) google()
  if (withKotlinx) maven(repos.kotlinx)
  if (withKotlinxHtml) maven(repos.kotlinxHtml)
  if (withComposeJbDev) maven(repos.composeJbDev)
  if (withKtorEap) maven(repos.ktorEap)
  if (withJitpack) maven(repos.jitpack)
}

// TODO_maybe: doc says it could be now also applied globally instead for each task (and it works for andro too)
//   But it's only for jvm+andro, so probably this is better:
//   https://kotlinlang.org/docs/gradle-compiler-options.html#for-all-kotlin-compilation-tasks
fun TaskCollection<Task>.defaultKotlinCompileOptions(
  apiVer: KotlinVersion = KotlinVersion.KOTLIN_2_1,
  jvmTargetVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
  renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    apiVersion.set(apiVer)
    jvmTargetVer?.let { jvmTarget = JvmTarget.fromTarget(it) }
    if (renderInternalDiagnosticNames) freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    // useful, for example, to suppress some errors when accessing internal code from some library, like:
    // @file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
  }
}

fun TaskCollection<Task>.defaultTestsOptions(
  printStandardStreams: Boolean = true,
  printStackTraces: Boolean = true,
  onJvmUseJUnitPlatform: Boolean = true,
) = withType<AbstractTestTask>().configureEach {
  testLogging {
    showStandardStreams = printStandardStreams
    showStackTraces = printStackTraces
  }
  if (onJvmUseJUnitPlatform) (this as? Test)?.useJUnitPlatform()
}

// Provide artifacts information requited by Maven Central
fun MavenPom.defaultPOM(lib: LibDetails) {
  name put lib.name
  description put lib.description
  url put lib.githubUrl

  licenses {
    license {
      name put lib.licenceName
      url put lib.licenceUrl
    }
  }
  developers {
    developer {
      id put lib.authorId
      name put lib.authorName
      email put lib.authorEmail
    }
  }
  scm { url put lib.githubUrl }
}

fun Project.defaultPublishing(lib: LibDetails) = extensions.configure<MavenPublishBaseExtension> {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (lib.settings.withCentralPublish) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  // Note: artifactId is not lib.name but current project.name (module name)
  coordinates(groupId = lib.group, artifactId = name, version = lib.version.str)
  pom { defaultPOM(lib) }
}

// endregion [[Kotlin Module Build Template]]
