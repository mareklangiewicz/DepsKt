@file:Suppress("UnstableApiUsage", "unused")

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import okio.Path.Companion.toOkioPath
import pl.mareklangiewicz.kgroundx.maintenance.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.bad.*
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.ure.UReplacement.Companion.Group
import pl.mareklangiewicz.ure.UReplacement.Companion.Literal
import kotlin.text.Regex.Companion.escapeReplacement
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.sourcefun.*
import org.jetbrains.kotlin.gradle.dsl.*

plugins {
  plugAll(plugs.KotlinJvm, plugs.NexusPublish, plugs.GradlePublish, plugs.Signing)
  id("pl.mareklangiewicz.sourcefun") version "0.4.18"
  // FIXME_later: add to plugAll after updating deps
  // https://plugins.gradle.org/search?term=pl.mareklangiewicz
}

buildscript {
  // Important: It's temporarily needed workaround block because changed api in kotlinx-maintenance
  //   BTW this issue fails on sync, but try running tasks before removing this workaround,
  //   because similar issue before (when api of related kground/kommand/etc changed) was compiling fine
  //   and only failing when I was executing specific custom task.
  dependencies {
    classpath("pl.mareklangiewicz:kommandline:0.0.82")
    classpath("pl.mareklangiewicz:kgroundx-maintenance:0.0.76")
    // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kommandline/
    // https://s01.oss.sonatype.org/content/repositories/releases/pl/mareklangiewicz/kground/
  }
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
}

tasks.defaultKotlinCompileOptions(jvmTargetVer = null) // see jvmToolchain below

tasks.defaultTestsOptions()

ext.addDefaultStuffFromSystemEnvs()

defaultGroupAndVerAndDescription(
  myLibDetails(
    name = "DepsKt",
    group = "pl.mareklangiewicz.deps", // important non default ...deps group (as accepted on gradle portal)
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support).",
    githubUrl = "https://github.com/mareklangiewicz/DepsKt",
    version = Ver(0, 3, 44),
    // TODO use some SourceFun task to make sure it's synced with Vers.DepsPlug
    // (we println it when applying plugin so have to be synced not to confuse users)
    // https://plugins.gradle.org/search?term=pl.mareklangiewicz
    settings = LibSettings(
      withJs = false,
      compose = null,
    ),
  ),
)

kotlin {
  jvmToolchain(22)
}

defaultSigning()

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

/** Publishing to Sonatype OSSRH has to be explicitly allowed here, by setting withSonatypeOssPublishing to true. */
fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  ext.addDefaultStuffFromSystemEnvs()
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
    if (it.settings.withSonatypeOssPublishing) defaultSonatypeOssNexusPublishing()
  }

  // kinda workaround for kinda issue with kotlin native
  // https://youtrack.jetbrains.com/issue/KT-48410/Sync-failed.-Could-not-determine-the-dependencies-of-task-commonizeNativeDistribution.#focus=Comments-27-5144160.0-0
  repositories { mavenCentral() }
}

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile (or MYKOTLIBS_signing_key with whole signing key)
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See KGround/template-full/template-full-lib/build.gradle.kts
 */
fun ExtraPropertiesExtension.addDefaultStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") =
  addAllFromSystemEnvs(envKeyMatchPrefix)

fun Project.defaultSonatypeOssNexusPublishing(
  sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
  ossrhUsername: String = rootExtString["ossrhUsername"],
  ossrhPassword: String = rootExtString["ossrhPassword"],
) {
  nexusPublishing {
    this.repositories {
      sonatype {  // only for users registered in Sonatype after 24 Feb 2021
        stagingProfileId put sonatypeStagingProfileId
        username put ossrhUsername
        password put ossrhPassword
        nexusUrl put repos.sonatypeOssNexus
        snapshotRepositoryUrl put repos.sonatypeOssSnapshots
      }
    }
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
  jvmTargetVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
  renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0) // FIXME_later: add param.
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
fun MavenPublication.defaultPOM(lib: LibDetails) = pom {
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

/** See also: root project template-full: addDefaultStuffFromSystemEnvs */
fun Project.defaultSigning(
  keyId: String = rootExtString["signing.keyId"],
  key: String = rootExtReadFileUtf8TryOrNull("signing.keyFile") ?: rootExtString["signing.key"],
  password: String = rootExtString["signing.password"],
) = extensions.configure<SigningExtension> {
  useInMemoryPgpKeys(keyId, key, password)
  sign(extensions.getByType<PublishingExtension>().publications)
}

fun Project.defaultPublishing(
  lib: LibDetails,
  readmeFile: File = File(rootDir, "README.md"),
  withSignErrorWorkaround: Boolean = true,
  withPublishingPrintln: Boolean = false, // FIXME_later: enabling brakes gradle android publications
) {

  val readmeJavadocJar by tasks.registering(Jar::class) {
    from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
    archiveClassifier put "javadoc"
  }

  extensions.configure<PublishingExtension> {

    // We have at least two cases:
    // 1. With plug.KotlinMulti it creates publications automatically (so no need to create here)
    // 2. With plug.KotlinJvm it does not create publications (so we have to create it manually)
    if (plugins.hasPlugin("org.jetbrains.kotlin.jvm")) {
      publications.create<MavenPublication>("jvm") {
        from(components["kotlin"])
      }
    }

    publications.withType<MavenPublication> {
      artifact(readmeJavadocJar)
      // Adding javadoc artifact generates warnings like:
      // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
      // (UPDATE: now it's errors - see workaround below)
      // It looks like a bug in kotlin multiplatform plugin:
      // https://youtrack.jetbrains.com/issue/KT-46466
      // FIXME_someday: Watch the issue.
      // If it's a bug in kotlin multiplatform then remove this comment when it's fixed.
      // Some related bug reports:
      // https://youtrack.jetbrains.com/issue/KT-47936
      // https://github.com/gradle/gradle/issues/17043

      defaultPOM(lib)
    }
  }
  if (withSignErrorWorkaround) tasks.withSignErrorWorkaround() // very much related to comments above too
  if (withPublishingPrintln) tasks.withPublishingPrintln()
}

/*
Hacky workaround for gradle error with signing+publishing on gradle 8.1-rc-1:

FAILURE: Build failed with an exception.

* What went wrong:
A problem was found with the configuration of task ':template-full-lib:signJvmPublication' (type 'Sign').
  - Gradle detected a problem with the following location: '/home/marek/code/kotlin/KGround/template-full/template-full-lib/build/libs/template-full-lib-0.0.02-javadoc.jar.asc'.

    Reason: Task ':template-full-lib:publishJsPublicationToMavenLocal' uses this output of task ':template-full-lib:signJvmPublication' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.

    Possible solutions:
      1. Declare task ':template-full-lib:signJvmPublication' as an input of ':template-full-lib:publishJsPublicationToMavenLocal'.
      2. Declare an explicit dependency on ':template-full-lib:signJvmPublication' from ':template-full-lib:publishJsPublicationToMavenLocal' using Task#dependsOn.
      3. Declare an explicit dependency on ':template-full-lib:signJvmPublication' from ':template-full-lib:publishJsPublicationToMavenLocal' using Task#mustRunAfter.

    Please refer to https://docs.gradle.org/8.1-rc-1/userguide/validation_problems.html#implicit_dependency for more details about this problem.

 */
fun TaskContainer.withSignErrorWorkaround() =
  withType<AbstractPublishToMaven>().configureEach { dependsOn(withType<Sign>()) }

fun TaskContainer.withPublishingPrintln() = withType<AbstractPublishToMaven>().configureEach {
  val coordinates = publication.run { "$groupId:$artifactId:$version" }
  when (this) {
    is PublishToMavenRepository -> doFirst {
      println("Publishing $coordinates to ${repository.url}")
    }
    is PublishToMavenLocal -> doFirst {
      val localRepo = System.getenv("HOME")!! + "/.m2/repository"
      val localPath = localRepo + publication.run { "/$groupId/$artifactId".replace('.', '/') }
      println("Publishing $coordinates to $localPath")
    }
  }
}

// endregion [[Kotlin Module Build Template]]
