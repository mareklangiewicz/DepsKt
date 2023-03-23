@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.1.0" // https://plugins.gradle.org/docs/publish-plugin
    id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin
    signing
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("com.squareup.okio:okio:3.3.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    implementation("com.android.library:com.android.library.gradle.plugin:8.1.0-alpha09")
    // Warning: andro gradle plugin is needed here, so kotlin plugin can access BaseExtension class etc.
    // It is needed in android projects using deps.kt (which does: plugins { kotlin("android") }
    // It doesn't work if users just add this andro gradle plugin dependency in their projects.
    testImplementation(deps.uspekxJUnit5)
    testImplementation("pl.mareklangiewicz:uspekx-junit5:0.0.25") // TODO: try to use deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1") // TODO: try to use deps... (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1") // TODO: try to use deps... (see comment in settings)
    // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc.)
}

tasks.defaultKotlinCompileOptions("17")

tasks.defaultTestsOptions()

defaultSonatypeOssStuffFromSystemEnvs()

defaultSigning()

group = "pl.mareklangiewicz.deps"
version = "0.2.23"


gradlePlugin {
    website.set("https://github.com/langara/deps.kt")
    vcsUrl.set("https://github.com/langara/deps.kt")
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
            displayName = "Deps.kt plugin"
            description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."
            tags.set(listOf("bom", "dependencies"))
        }
        create("depsSettingsPlugin") {
            id = "pl.mareklangiewicz.deps.settings"
            implementationClass = "pl.mareklangiewicz.deps.DepsSettingsPlugin"
            displayName = "Deps.kt settings plugin"
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

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See deps.kt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun Project.defaultSonatypeOssStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") {
    ext.addAllFromSystemEnvs(envKeyMatchPrefix)
//    defaultSonatypeOssNexusPublishing() // FIXME
}

fun Project.defaultSonatypeOssNexusPublishing(
    sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
    ossrhUsername: String = rootExtString["ossrhUsername"],
    ossrhPassword: String = rootExtString["ossrhPassword"],
) = nexusPublishing {
    TODO() // FIXME
    repositories {
//        sonatype {  // only for users registered in Sonatype after 24 Feb 2021
//            stagingProfileId put sonatypeStagingProfileId
//            username put ossrhUsername
//            password put ossrhPassword
//            nexusUrl put uri(repos.sonatypeOssNexus)
//            snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
//        }
    }
}

// endregion [Root Build Template]

// region [Kotlin Module Build Template]

fun RepositoryHandler.defaultRepos(
    withMavenLocal: Boolean = false,
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
    jvmTargetVer: String = vers.defaultJvm,
    renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (renderInternalDiagnosticNames) freeCompilerArgs = freeCompilerArgs + "-Xrender-internal-diagnostic-names"
        // useful for example to suppress some errors when accessing internal code from some library, like:
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

/** See also: root project template-mpp: fun Project.defaultSonatypeOssStuffFromSystemEnvs */
fun Project.defaultSigning(
    keyId: String = rootExtString["signing.keyId"],
    key: String = rootExtReadFileUtf8("signing.keyFile"),
    password: String = rootExtString["signing.password"],
) = extensions.configure<SigningExtension> {
    useInMemoryPgpKeys(keyId, key, password)
    sign(extensions.getByType<PublishingExtension>().publications)
}

fun Project.defaultPublishing(lib: LibDetails, readmeFile: File = File(rootDir, "README.md")) {

    val readmeJavadocJar by tasks.registering(Jar::class) {
        from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
        archiveClassifier put "javadoc"
    }

    extensions.configure<PublishingExtension> {
        publications.withType<MavenPublication> {
            artifact(readmeJavadocJar)
            // Adding javadoc artifact generates warnings like:
            // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
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
}


// endregion [Kotlin Module Build Template]
