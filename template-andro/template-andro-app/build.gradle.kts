import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import pl.mareklangiewicz.defaults.*

plugins {
    id("com.android.application") version vers.androidGradlePlugin
    kotlin("android") version vers.kotlin
    id("maven-publish")
    id("signing")
}

repositories { defaultRepos() }

android {
    defaultAndroApp("pl.mareklangiewicz.templateandro", withCompose = true)
    defaultAndroAppPublishVariant("debug")
}

dependencies {
    implementation(project(":template-andro-lib"))
    defaultAndroDeps(withCompose = true)
    defaultAndroTestDeps(withCompose = true)
}

tasks.defaultKotlinCompileOptions()

defaultGroupAndVerAndDescription(libs.TemplateAndro)

defaultPublishingOfAndroApp(libs.TemplateAndro, "debug")

defaultSigning()




// region Kotlin Module Build Template

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion Kotlin Module Build Template

// region Andro Module Build Template

fun ApplicationExtension.defaultAndroApp(
    appId: String,
    appNamespace: String = appId,
    appVerCode: Int = 1,
    appVerName: String = v(patch = appVerCode),
    jvmVersion: String = vers.defaultJvm,
    sdkCompile: Int = vers.androidSdkCompile,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
    withCompose: Boolean = false,
) {
    compileSdk = sdkCompile
    defaultCompileOptions(jvmVersion)
    defaultDefaultConfig(appId, appNamespace, appVerCode, appVerName, sdkTarget, sdkMin)
    defaultBuildTypes()
    if (withCompose) defaultComposeStuff()
    defaultPackagingOptions()
}

fun LibraryExtension.defaultAndroLib(
    libNamespace: String,
    jvmVersion: String = vers.defaultJvm,
    sdkCompile: Int = vers.androidSdkCompile,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
    withCompose: Boolean = false,
) {
    compileSdk = sdkCompile
    defaultCompileOptions(jvmVersion)
    defaultDefaultConfig(libNamespace, sdkTarget, sdkMin)
    defaultBuildTypes()
    if (withCompose) defaultComposeStuff()
    defaultPackagingOptions()
}

fun ApplicationExtension.defaultDefaultConfig(
    appId: String,
    appNamespace: String = appId,
    appVerCode: Int = 1,
    appVerName: String = v(patch = appVerCode),
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
) = defaultConfig {
    applicationId = appId
    namespace = appNamespace
    targetSdk = sdkTarget
    minSdk = sdkMin
    versionCode = appVerCode
    versionName = appVerName
    testInstrumentationRunner = vers.androidTestRunnerClass
}

fun LibraryExtension.defaultDefaultConfig(
    libNamespace: String,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
) = defaultConfig {
    namespace = libNamespace
    targetSdk = sdkTarget
    minSdk = sdkMin
    testInstrumentationRunner = vers.androidTestRunnerClass
}

fun CommonExtension<*,*,*,*>.defaultCompileOptions(
    jvmVersion: String = vers.defaultJvm
) = compileOptions {
    sourceCompatibility(jvmVersion)
    targetCompatibility(jvmVersion)
}

fun ApplicationExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }
fun LibraryExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }

fun CommonExtension<*,*,*,*>.defaultComposeStuff() {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = vers.composeAndroidCompiler
    }
}

fun CommonExtension<*,*,*,*>.defaultPackagingOptions() = packagingOptions {
    resources.excludes.defaultAndroExcludedResources()
}

fun LibraryExtension.defaultAndroLibPublishVariant(
    variant: String = "release",
    withSources: Boolean = true,
    withJavadoc: Boolean = false,
) {
    publishing {
        singleVariant(variant) {
            if (withSources) withSourcesJar()
            if (withJavadoc) withJavadocJar()
        }
    }
}

fun LibraryExtension.defaultAndroLibPublishAllVariants(
    withSources: Boolean = true,
    withJavadoc: Boolean = false,
) {
    publishing {
        multipleVariants {
            allVariants()
            if (withSources) withSourcesJar()
            if (withJavadoc) withJavadocJar()
        }
    }
}

fun ApplicationExtension.defaultAndroAppPublishVariant(
    variant: String = "release",
    publishAPK: Boolean = true,
    publishAAB: Boolean = false,
) {
    require(!publishAAB || !publishAPK) { "Either APK or AAB can be published, but not both." }
    publishing { singleVariant(variant) { if (publishAPK) publishApk() } }
}

// endregion Andro Module Build Template