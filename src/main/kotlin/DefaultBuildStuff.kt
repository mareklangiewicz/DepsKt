@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*

fun ScriptHandlerScope.defaultAndroBuildScript() {
    repositories {
        defaultRepositories(withGradle = true)
    }
    dependencies {
        classpath(Deps.kotlinGradlePlugin)
        classpath(Deps.androidGradlePlugin)
    }
}

fun RepositoryHandler.defaultRepositories(
    withGradle: Boolean = false,
    withGoogle: Boolean = true,
    withMavenCentral: Boolean = true,
    withKotlinX: Boolean = true,
    withJitpack: Boolean = true,
) {
    if (withGradle) gradlePluginPortal()
    if (withGoogle) google()
    if (withMavenCentral) mavenCentral()
    if (withKotlinX) maven(url = "https://kotlin.bintray.com/kotlinx")
    if (withJitpack) maven(Repos.jitpack)
}

//fun Project.configureKotlinCompileTasks() {
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//        kotlinOptions {
//            jvmTarget = "16"
//            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
//        }
//    }
//}

fun ApplicationExtension.defaultAndroid(
    appId: String,
    appVerCode: Int = 1,
    appVerName: String = defaultVerName(patch = appVerCode),
    javaVersion: JavaVersion = JavaVersion.VERSION_16,
    withCompose: Boolean = false,
) {
    compileSdk = Vers.androidCompileSdk
    defaultCompileOptions(javaVersion)
    defaultDefaultConfig(appId, appVerCode, appVerName)
    defaultBuildTypes()
    if (withCompose) defaultComposeStuff()
    defaultPackagingOptions()
}

fun LibraryExtension.defaultAndroid(
    javaVersion: JavaVersion = JavaVersion.VERSION_16,
    withCompose: Boolean = false,
) {
    compileSdk = Vers.androidCompileSdk
    defaultCompileOptions(javaVersion)
    defaultDefaultConfig()
    defaultBuildTypes()
    if (withCompose) defaultComposeStuff()
    defaultPackagingOptions()
}

fun ApplicationExtension.defaultDefaultConfig(
    appId: String,
    appVerCode: Int = 1,
    appVerName: String = defaultVerName(patch = appVerCode)
) = defaultConfig {
    applicationId = appId
    minSdk = Vers.androidMinSdk
    targetSdk = Vers.androidTargetSdk
    versionCode = appVerCode
    versionName = appVerName
    testInstrumentationRunner = Vers.androidTestRunnerClass
}

private fun defaultVerName(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLen: Int = 2) =
    "$major.$minor." + patch.toString().padStart(patchLen, '0')

fun LibraryExtension.defaultDefaultConfig() = defaultConfig {
    minSdk = Vers.androidMinSdk
    targetSdk = Vers.androidTargetSdk
    testInstrumentationRunner = Vers.androidTestRunnerClass
}

fun CommonExtension<*,*,*,*>.defaultCompileOptions(
    javaVersion: JavaVersion = JavaVersion.VERSION_16
) = compileOptions {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

fun ApplicationExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }
fun LibraryExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }

fun CommonExtension<*,*,*,*>.defaultComposeStuff() {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Vers.composeAndroidCompiler
    }
}

fun CommonExtension<*,*,*,*>.defaultPackagingOptions() = packagingOptions {
    resources.excludes.add("**/*.md")
    resources.excludes.add("**/attach_hotspot_windows.dll")
    resources.excludes.add("META-INF/licenses/**")
    resources.excludes.add("META-INF/AL2.0")
    resources.excludes.add("META-INF/LGPL2.1")
}