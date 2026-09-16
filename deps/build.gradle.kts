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
import pl.mareklangiewicz.templatefun.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  // plugAll(plugs.KotlinJvm, plugs.GradlePublish, plugs.VannikPublish, plugs.SourceFun)
  plugAll(plugs.KotlinJvmNoVer, plugs.GradlePublish, plugs.VannikPublish) // version comes from the root
  plug(plugs.TemplateFun) // the PUBLISHED one -- see the note above defaultPublishing below
  // The PUBLISHED sourcefun, even though :sourcefun is now a sibling in this very build. A
  // subproject cannot supply a plugin to another subproject's build script, and this version is
  // deliberately a LITERAL, not plugs.SourceFun: it must name something already on the portal, so
  // it lags between a bump and a publish -- exactly like the settings plugin pinned in
  // ../settings.gradle.kts. Bump it by hand, after the release it names is out.
  id("pl.mareklangiewicz.sourcefun") version "0.4.49" // https://plugins.gradle.org/search?term=mareklangiewicz
}

repositories {
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

// The lib is defined once, in settings.gradle.kts, and read here. Named myLib, not lib, so the
// local does not shadow the lib(..) factory it is built with.
val myLib = gradle.extLib

// Set here, not inherited from the root: the root deliberately has no group (see build.gradle.kts).
// The gradlePlugin marker publications read project.group/version, so this is what puts
// pl.mareklangiewicz.deps on them.
defaultGroupAndVerAndDescription(myLib)

kotlin {
  jvmToolchain(23)
}

// templatefun's defaultPublishing is `context(info: LibInfo, flags: LibFlags) fun Project...`, and
// build scripts are compiled WITHOUT -Xcontext-parameters (Gradle pins the script language version),
// so it is reached through the flattened coercion: context parameters first, then the extension
// receiver. This call IS the claim KGround's probes 7/14/15 assert -- exercised by a real build now,
// rather than by branch-local evidence.
val tfDefaultPublishing: (LibInfo, LibFlags, Project) -> Unit = Project::defaultPublishing
tfDefaultPublishing(myLib.info, myLib.flags, project)

// artifactId is pinned, NOT derived from the project name: this project lives in ./deps, and
// defaultPublishing defaults artifactId to project.name. Pinning it here -- rather than renaming
// the project to "DepsKt" -- keeps the path, the directory and the name agreeing with each other.
// This must come AFTER defaultPublishing: both call coordinates(..) and the last call wins.
mavenPublishing { coordinates(myLib.info.group, "DepsKt", myLib.info.version.str) }

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


val pathToSrcKotlin = projectPath / "src/main/kotlin"
val urlToRefreshDeps = "https://raw.githubusercontent.com/mareklangiewicz/refreshDeps"
val urlToObjectsFile = "$urlToRefreshDeps/main/plugins/dependencies/src/test/resources/objects-for-deps.txt"

val downloadGeneratedDeps = tasks.register<DownloadFileTask>("downloadGeneratedDeps") {
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
tasks.register("updateGeneratedDepsAlternative") {
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
tasks.register("updateSomeRegexes") {

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
    path.processSingleFile(path) {
      it.replaceSingle(ureWithTheOldThing, Group("beforeTheThing") + Literal(theNewThing) + Group("afterTheThing"))
    }
  }
}



// The [[Kotlin Module Build Template]] region used to be copied in here (addRepos,
// defaultKotlinCompileOptions, defaultTestsOptions, defaultPOM, defaultPublishing).
// It is gone: this script applies the published pl.mareklangiewicz.templatefun instead, which is
// what every other repo now does. DepsKt consuming its own published templatefun is not circular:
// the whole chain is the PREVIOUS release, a finished artifact, not this build. Currently:
// deps.settings 0.4.30 -> DepsKt 0.4.30 -> (Vers.DepsPlug) templatefun 0.4.30 -> DepsKt 0.4.30.
