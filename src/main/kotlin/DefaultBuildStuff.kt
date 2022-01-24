import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import kotlin.reflect.KCallable


fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: KCallable<Unit>) {
    val pairs: List<Pair<String, () -> Unit>> = afun.map { it.name to { it.call() } }
    registerAllThatGroupFun(group, *pairs.toTypedArray())
}

fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: Pair<String, () -> Unit>) {
    for ((name, code) in afun) register(name) { this.group = group; doLast { code() } }
}

fun RepositoryHandler.defaultRepos(
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
    add("classpath", Deps.kotlinGradlePlugin)
    add("classpath", Deps.androidGradlePlugin)
}

fun defaultVerName(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLen: Int = 2) =
    "$major.$minor." + patch.toString().padStart(patchLen, '0')



fun DependencyHandler.defaultAndroDeps(
    configuration: String = "implementation",
    withCompose: Boolean = false,
) = Deps.run {
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
) = Deps.run {
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
