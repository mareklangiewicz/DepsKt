@file:Suppress("unused", "PackageDirectoryMismatch")

package pl.mareklangiewicz.defaults

import deps
import org.gradle.api.artifacts.dsl.*
import org.gradle.kotlin.dsl.*


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
