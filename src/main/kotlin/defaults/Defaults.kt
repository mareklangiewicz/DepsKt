@file:Suppress("unused", "PackageDirectoryMismatch")

package pl.mareklangiewicz.defaults

import pl.mareklangiewicz.deps.*
import org.gradle.api.*
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.*
import pl.mareklangiewicz.utils.*
import java.io.*
import deps
import repos

fun v(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLength: Int = 2, suffix: String = "") =
    "$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix"


fun RepositoryHandler.defaultRepos(
    withMavenLocal: Boolean = false,
    withMavenCentral: Boolean = true,
    withGradle: Boolean = false,
    withGoogle: Boolean = true,
    withKotlinX: Boolean = true,
    withJitpack: Boolean = true,
) {
    if (withMavenLocal) mavenLocal()
    if (withMavenCentral) mavenCentral()
    if (withGradle) gradlePluginPortal()
    if (withGoogle) google()
    if (withKotlinX) maven(repos.kotlinx)
    if (withJitpack) maven(repos.jitpack)
}

@Deprecated("Use defaultGroupAndVerAndDescription", replaceWith = ReplaceWith("defaultGroupAndVerAndDescription(libs.name)"))
fun Project.defaultGroupAndVer(dep: String) {
    val (g, _, v) = dep.split(":")
    group = g
    version = v
}

fun Project.defaultGroupAndVerAndDescription(lib: LibDetails) {
    group = lib.group
    version = lib.version
    description = lib.description
}

/** usually not needed - see template-android */
fun ScriptHandlerScope.defaultAndroBuildScript() {
    repositories {
        defaultRepos(withGradle = true)
    }
    dependencies {
        defaultAndroBuildScriptDeps()
    }
}


/** usually not needed - see template-android */
fun DependencyHandler.defaultAndroBuildScriptDeps(
) {
    add("classpath", deps.kotlinGradlePlugin)
    add("classpath", deps.androidGradlePlugin)
}



fun DependencyHandler.defaultAndroDeps(
    configuration: String = "implementation",
    withCompose: Boolean = false,
) = deps.run {
    addAll(configuration,
        androidxCoreKtx,
        androidxAppcompat,
        androidMaterial,
        androidxLifecycleCompiler,
        androidxLifecycleRuntimeKtx,
    )
    if (withCompose) addAll(configuration,
        composeAndroidUi,
        composeAndroidUiTooling,
        composeAndroidMaterial3,
        composeAndroidMaterial,
        androidxActivityCompose,
    )
}

fun DependencyHandler.defaultAndroTestDeps(
    configuration: String = "testImplementation",
    withCompose: Boolean = false,
) = deps.run {
    addAll(configuration,
//        uspekx,
        junit4,
        androidxEspressoCore,
        googleTruth,
        androidxTestRules,
        androidxTestRunner,
        androidxTestExtTruth,
        androidxTestExtJUnit,
        "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0",
//        mockitoKotlin2,
        mockitoAndroid
    )
    if (withCompose) addAll(configuration,
        composeAndroidUiTest,
        composeAndroidUiTestJUnit4,
        composeAndroidUiTestManifest,
    )
}

fun MutableSet<String>.defaultAndroExcludedResources() = addAll(listOf(
    "**/*.md",
    "**/attach_hotspot_windows.dll",
    "META-INF/licenses/**",
    "META-INF/AL2.0",
    "META-INF/LGPL2.1",
))


fun Project.defaultSigning() {
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            rootExt("signing.keyId"),
            rootExt("signing.key"),
            rootExt("signing.password")
        )
        sign(extensions.getByType<PublishingExtension>().publications)
    }
}

fun Project.defaultPublishing(lib: LibDetails, readmeFile: File = File(rootDir, "README.md")) {

    val readmeJavadocJar by tasks.registering(Jar::class) {
        from(readmeFile) // TODO_maybe: use dokka to create real docs? (but it's not even java..)
        archiveClassifier put "javadoc"
    }

    extensions.configure<PublishingExtension> {
        publications.withType<MavenPublication> { defaultPublication(lib, readmeJavadocJar) }
    }
}

private fun MavenPublication.defaultPublication(lib: LibDetails, javaDocProvider: TaskProvider<Jar>) {

    artifact(javaDocProvider)
    // Adding javadoc artifact generates warnings like:
    // Execution optimizations have been disabled for task ':uspek:signJvmPublication'
    // It looks like a bug in kotlin multiplatform plugin:
    // https://youtrack.jetbrains.com/issue/KT-46466
    // FIXME_someday: Watch the issue.
    // If it's a bug in kotlin multiplatform then remove this comment when it's fixed.
    // Some related bug reports:
    // https://youtrack.jetbrains.com/issue/KT-47936
    // https://github.com/gradle/gradle/issues/17043

    // Provide artifacts information requited by Maven Central
    pom {
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
        scm {
            url put lib.githubUrl
        }
    }
}