import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*

plugins {
    id("com.android.library") version vers.androidGradlePlugin
    kotlin("android") version vers.kotlin
    id("maven-publish")
    id("signing")
}

defaultBuildTemplateForAndroidLib(
    libNamespace = "pl.mareklangiewicz.templateandrolib",
    withCompose = true,
    details = libs.TemplateAndro,
    publishVariant = "debug",
)

// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion [Kotlin Module Build Template]

// region [Andro Common Build Template]

fun CommonExtension<*,*,*,*>.defaultCompileOptions(
    jvmVersion: String = vers.defaultJvm
) = compileOptions {
    sourceCompatibility(jvmVersion)
    targetCompatibility(jvmVersion)
}

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

// endregion [Andro Common Build Template]

// region [Andro Lib Build Template]

fun Project.defaultBuildTemplateForAndroidLib(
    libNamespace: String,
    jvmVersion: String = vers.defaultJvm,
    sdkCompile: Int = vers.androidSdkCompile,
    sdkTarget: Int = vers.androidSdkTarget,
    sdkMin: Int = vers.androidSdkMin,
    withCompose: Boolean = false,
    details: LibDetails = libs.Unknown,
    publishVariant: String? = null, // null means disable publishing to maven repo
) {
    repositories { defaultRepos() }
    android {
        defaultAndroLib(libNamespace, jvmVersion, sdkCompile, sdkTarget, sdkMin, withCompose)
        publishVariant?.let { defaultAndroLibPublishVariant(it) }
    }
    dependencies {
        defaultAndroDeps(withCompose = withCompose)
        defaultAndroTestDeps(withCompose = withCompose)
    }
    tasks.defaultKotlinCompileOptions()
    defaultGroupAndVerAndDescription(details)
    publishVariant?.let {
        defaultPublishingOfAndroLib(details, it)
        defaultSigning()
    }
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

fun LibraryExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }

fun LibraryExtension.defaultAndroLibPublishVariant(
    variant: String = "debug",
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

// endregion [Andro Lib Build Template]