@file:Suppress("UnstableApiUsage")

import okio.*
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import pl.mareklangiewicz.defaults.defaultGroupAndVerAndDescription
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.kommand.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*
import kotlin.math.*
import kotlin.random.*

plugins {
    plugAll(plugs.KotlinJvm, plugs.NexusPublish, plugs.GradlePublish, plugs.Signing)
}

tasks.register("updateGeneratedDeps") {
    group = "maintenance"
    doLast {
        val pathToDepsNew = rootProjectPath / "src/main/kotlin/deps/DepsNew.kt"
        val urlToObjs = "https://raw.githubusercontent.com/langara/refreshDeps/main/plugins/dependencies/src/test/resources/objects-for-deps.txt"
        downloadAndInjectFileToSpecialRegion(
            inFileUrl = urlToObjs,
            outFilePath = pathToDepsNew,
            outFileRegionLabel = "Deps Generated"
        )
    }
}


// FIXME NOW: remove experiment1

tasks.register("experiment1") {
    group = "maintenance"
    doLast {
        val pathToDepsNew = rootProjectPath / "src/main/kotlin/deps/DepsNew.kt"
        val inFileUrl = "https://raw.githubusercontent.com/langara/refreshDeps/main/plugins/dependencies/src/test/resources/objects-for-deps.txt"
        val inFilePath = downloadTmpFileVerbose(inFileUrl)
        val regionContent = FileSystem.SYSTEM.readUtf8(inFilePath)
        println(regionContent)
    }
}


fun downloadTmpFileVerbose(
    url: String,
    name: String = "tmp${Random.nextLong().absoluteValue}.txt",
    dir: Path = (CliPlatform.SYS.pathToUserTmp ?: CliPlatform.SYS.pathToSystemTmp ?: "/tmp").toPath()
): Path {
    val path = dir / name
    CliPlatform.SYS.downloadVerbose(url, path)
    return path
}

fun CliPlatform.downloadVerbose(url: String, to: Path) {
    println("downloading $url -> $path")
    // TODO: Add curl to KommandLine library, then use it here
    // -s so no progress bars on error stream; -S to report actual errors on error stream
//    val k = kommand("curl", "-s", "-S", "-o", to.toString(), url)
    val k = kommand("curl", "-o", to.toString(), url)
    val result = start(k).waitForResult()
    result.unwrap { err ->
        if (err.isNotEmpty()) {
            println("FAIL: Error stream was not empty:")
            err.logEach()
            false
        }
        else true
    }
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("pl.mareklangiewicz:kgroundx-maintenance:0.0.07") // FIXME https://repo1.maven.org/maven2/pl/mareklangiewicz/kground/
    testImplementation(Langiewicz.uspekx_junit5)
    testImplementation(Org.JUnit.Jupiter.junit_jupiter)
    testImplementation(Org.JUnit.Jupiter.junit_jupiter_engine)
    // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc.)
}

tasks.defaultKotlinCompileOptions()

tasks.defaultTestsOptions()

ext.addDefaultStuffFromSystemEnvs()

defaultGroupAndVerAndDescription(
    langaraLibDetails(
        name = "DepsKt",
        group = "pl.mareklangiewicz.deps", // important non default ...deps group (as accepted on gradle portal)
        description = "Updated dependencies for typical java/kotlin/android projects (with IDE support).",
        githubUrl = "https://github.com/langara/DepsKt",
        version = Ver(0, 2, 51),
        // https://plugins.gradle.org/search?term=pl.mareklangiewicz
    )
)

defaultSigning()

gradlePlugin {
    website.set("https://github.com/langara/DepsKt")
    vcsUrl.set("https://github.com/langara/DepsKt")
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
            description = "Updated dependencies for typical java/kotlin/android projects (with IDE support) (settings plugin)."
            tags.set(listOf("bom", "dependencies"))
        }
        create("sourceFunPlugin") {
            id = "pl.mareklangiewicz.sourcefun"
            implementationClass = "pl.mareklangiewicz.sourcefun.SourceFunPlugin"
            displayName = "SourceFun plugin"
            description = "Updated dependencies for typical java/kotlin/android projects (with IDE support) (source fun)."
            tags.set(listOf("SourceTask", "DSL"))
        }
    }
}


// region [Root Build Template]

/** Publishing to Sonatype OSSRH has to be explicitly allowed here, by setting withSonatypeOssPublishing to true. */
fun Project.defaultBuildTemplateForRootProject(
    libDetails: LibDetails? = null,
    withSonatypeOssPublishing: Boolean = false
) {
    check(libDetails != null || !withSonatypeOssPublishing)
    ext.addDefaultStuffFromSystemEnvs()
    libDetails?.let {
        rootExtLibDetails = it
        defaultGroupAndVerAndDescription(it)
        if (withSonatypeOssPublishing) defaultSonatypeOssNexusPublishing()
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
 * * See DepsKt/template-mpp/template-mpp-lib/build.gradle.kts
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

// endregion [Root Build Template]

// region [Kotlin Module Build Template]

fun RepositoryHandler.defaultRepos(
    withMavenLocal: Boolean = true,
    withMavenCentral: Boolean = true,
    withGradle: Boolean = false,
    withGoogle: Boolean = true,
    withKotlinx: Boolean = true,
    withKotlinxHtml: Boolean = false,
    withComposeJbDev: Boolean = false,
    withComposeCompilerAndroidxDev: Boolean = false,
    withKtorEap: Boolean = false,
    withJitpack: Boolean = false,
) {
    if (withMavenLocal) mavenLocal()
    if (withMavenCentral) mavenCentral()
    if (withGradle) gradlePluginPortal()
    if (withGoogle) google()
    if (withKotlinx) maven(repos.kotlinx)
    if (withKotlinxHtml) maven(repos.kotlinxHtml)
    if (withComposeJbDev) maven(repos.composeJbDev)
    if (withComposeCompilerAndroidxDev) maven(repos.composeCompilerAndroidxDev)
    if (withKtorEap) maven(repos.ktorEap)
    if (withJitpack) maven(repos.jitpack)
}

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = versNew.JvmDefaultVer,
    renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (renderInternalDiagnosticNames) freeCompilerArgs = freeCompilerArgs + "-Xrender-internal-diagnostic-names"
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

/** See also: root project template-mpp: addDefaultStuffFromSystemEnvs */
fun Project.defaultSigning(
    keyId: String = rootExtString["signing.keyId"],
    key: String = rootExtReadFileUtf8TryOrNull("signing.keyFile") ?: rootExtString["signing.key"],
    password: String = rootExtString["signing.password"],
) = extensions.configure<SigningExtension> {
    useInMemoryPgpKeys(keyId, key, password)
    sign(extensions.getByType<PublishingExtension>().publications)
}

fun Project.defaultPublishing(lib: LibDetails, readmeFile: File = File(rootDir, "README.md"), withSignErrorWorkaround: Boolean = true) {

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
    if (withSignErrorWorkaround) tasks.withSignErrorWorkaround() //very much related to comments above too
}

/*
Hacky workaround for gradle error with signing+publishing on gradle 8.1-rc-1:

FAILURE: Build failed with an exception.

* What went wrong:
A problem was found with the configuration of task ':template-mpp-lib:signJvmPublication' (type 'Sign').
  - Gradle detected a problem with the following location: '/home/marek/code/kotlin/DepsKt/template-mpp/template-mpp-lib/build/libs/template-mpp-lib-0.0.02-javadoc.jar.asc'.

    Reason: Task ':template-mpp-lib:publishJsPublicationToMavenLocal' uses this output of task ':template-mpp-lib:signJvmPublication' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.

    Possible solutions:
      1. Declare task ':template-mpp-lib:signJvmPublication' as an input of ':template-mpp-lib:publishJsPublicationToMavenLocal'.
      2. Declare an explicit dependency on ':template-mpp-lib:signJvmPublication' from ':template-mpp-lib:publishJsPublicationToMavenLocal' using Task#dependsOn.
      3. Declare an explicit dependency on ':template-mpp-lib:signJvmPublication' from ':template-mpp-lib:publishJsPublicationToMavenLocal' using Task#mustRunAfter.

    Please refer to https://docs.gradle.org/8.1-rc-1/userguide/validation_problems.html#implicit_dependency for more details about this problem.

 */
fun TaskContainer.withSignErrorWorkaround() =
    withType<AbstractPublishToMaven>().configureEach { dependsOn(withType<Sign>()) }


@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForJvmLib(
    details: LibDetails = rootExtLibDetails,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    repositories { defaultRepos() }
    defaultGroupAndVerAndDescription(details)

    kotlin {
        sourceSets {
            val main by getting {
                dependencies {
                    addMainDependencies()
                }
            }
            val test by getting {
                dependencies {
                    if (withTestJUnit4) implementation(JUnit.junit)
                    if (withTestJUnit5) implementation(Org.JUnit.Jupiter.junit_jupiter_engine)
                    if (withTestUSpekX) {
                        implementation(Langiewicz.uspekx)
                        if (withTestJUnit4) implementation(Langiewicz.uspekx_junit4)
                        if (withTestJUnit5) implementation(Langiewicz.uspekx_junit5)
                    }
                }
            }
        }
    }

    configurations.checkVerSync()
    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
    if (plugins.hasPlugin("maven-publish")) {
        defaultPublishing(details)
        if (plugins.hasPlugin("signing")) defaultSigning()
        else println("JVM Module ${name}: signing disabled")
    } else println("JVM Module ${name}: publishing (and signing) disabled")
}

// endregion [Kotlin Module Build Template]