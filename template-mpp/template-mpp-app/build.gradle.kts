import org.jetbrains.compose.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version vers.composeJb
}

defaultBuildTemplateForComposeMppApp(
    appMainPackage = "pl.mareklangiewicz.hello",
    details = libs.TemplateMPP,
    withNativeLinux64 = true,
    withKotlinxHtml = true,
) {
    implementation(project(":template-mpp-lib"))
}

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


// region [MPP Module Build Template]

/** Only for very standard small libs. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForMppLib(
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    repositories { defaultRepos(withKotlinxHtml = withKotlinxHtml, withComposeJbDev = withComposeJbDevRepo) }
    defaultGroupAndVerAndDescription(details)
    kotlin { allDefault(
        withJvm,
        withJs,
        withNativeLinux64,
        withKotlinxHtml,
        withTestJUnit5,
        withTestUSpekX,
        addCommonMainDependencies
    ) }
    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
    if (plugins.hasPlugin("maven-publish")) {
        defaultPublishing(details)
        if (plugins.hasPlugin("signing")) defaultSigning()
        else println("MPP Module ${name}: signing disabled")
    }
    else println("MPP Module ${name}: publishing (and signing) disabled")
}

/** Only for very standard small libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun KotlinMultiplatformExtension.allDefault(
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    if (withJvm) jvm()
    if (withJs) jsDefault()
    if (withNativeLinux64) linuxX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                if (withKotlinxHtml) implementation(deps.kotlinxHtml)
                addCommonMainDependencies()
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                if (withTestUSpekX) implementation(deps.uspekx)
            }
        }
        if (withJvm) {
            val jvmTest by getting {
                dependencies {
                    if (withTestJUnit5) implementation(deps.junit5engine)
                }
            }
        }
        if (withNativeLinux64) {
            val linuxX64Main by getting
            val linuxX64Test by getting
        }
    }
}


fun KotlinMultiplatformExtension.jsDefault(
    withBrowser: Boolean = true,
    withNode: Boolean = false,
    testWithChrome: Boolean = true,
    testHeadless: Boolean = true,
) {
    js(IR) {
        if (withBrowser) browser {
            testTask {
                useKarma {
                    when (testWithChrome to testHeadless) {
                        true to true -> useChromeHeadless()
                        true to false -> useChrome()
                    }
                }
            }
        }
        if (withNode) nodejs()
    }
}

// endregion [MPP Module Build Template]

// region [MPP App Build Template]

fun Project.defaultBuildTemplateForMppApp(
    appMainPackage: String,
    appMainFun: String = "main",
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = withComposeJbDevRepo,
        withTestJUnit5 = withTestJUnit5,
        withTestUSpekX = withTestUSpekX,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        if (withJvm) jvm {
            println("MPP App ${project.name}: Generating general jvm executables with kotlin multiplatform plugin is not supported (without compose).")
            // TODO_someday: Will they support multiplatform way of declaring jvm app?
            //binaries.executable()
        }
        if (withJs) js(IR) {
            binaries.executable()
        }
        if (withNativeLinux64) linuxX64 {
            binaries {
                executable {
                    entryPoint = "$appMainPackage.$appMainFun"
                }
            }
        }
    }
}

// endregion [MPP App Build Template]

// region [Compose MPP Module Build Template]

/** Only for very standard compose mpp libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
@OptIn(ExperimentalComposeLibrary::class)
fun Project.defaultBuildTemplateForComposeMppLib(
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeUi: Boolean = withJvm,
    withComposeFoundation: Boolean = withJvm,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = withJvm,
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = withJvm,
    withComposeWebCore: Boolean = withJs,
    withComposeWebWidgets: Boolean = false,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = true,
        withTestJUnit5 = false, // Unfortunately Compose UI steel uses JUnit4 instead of 5
        withTestUSpekX = true,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        sourceSets {
            val jvmMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    if (withComposeUi) {
                        implementation(compose.ui)
                        implementation(compose.uiTooling)
                        implementation(compose.preview)
                    }
                    if (withComposeFoundation) implementation(compose.foundation)
                    if (withComposeMaterial2) implementation(compose.material)
                    if (withComposeMaterial3) implementation(compose.material3)
                    if (withComposeMaterialIconsExtended) implementation(compose.materialIconsExtended)
                    if (withComposeFullAnimation) {
                        implementation(compose.animation)
                        implementation(compose.animationGraphics)
                    }
                    if (withComposeDesktop) {
                        implementation(compose.desktop.common)
                        implementation(compose.desktop.currentOs)
                    }
                    if (withComposeDesktopComponents) {
                        implementation(compose.desktop.components.splitPane)
                    }
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    if (withComposeWebCore) implementation(compose.web.core)
                    @Suppress("DEPRECATION")
                    if (withComposeWebWidgets) implementation(compose.web.widgets)
                    if (withComposeWebSvg) implementation(compose.web.svg)
                }
            }
            val jvmTest by getting {
                dependencies {
                    if (withComposeTestUiJUnit4) implementation(compose.uiTestJUnit4)
                }
            }
            val jsTest by getting {
                dependencies {
                    if (withComposeTestWebUtils) implementation(compose.web.testUtils)
                }
            }
        }
    }
}

// endregion [Compose MPP Module Build Template]

// region [Compose MPP App Build Template]

/** Only for very standard compose mpp apps. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForComposeMppApp(
    appMainPackage: String,
    appMainClass: String = "App_jvmKt", // for compose jvm
    appMainFun: String = "main", // for native
    details: LibDetails = libs.Unknown,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeUi: Boolean = withJvm,
    withComposeFoundation: Boolean = withJvm,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = withJvm,
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = withJvm,
    withComposeWebCore: Boolean = withJs,
    withComposeWebWidgets: Boolean = false,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    defaultBuildTemplateForComposeMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeUi = withComposeUi,
        withComposeFoundation = withComposeFoundation,
        withComposeMaterial2 = withComposeMaterial2,
        withComposeMaterial3 = withComposeMaterial3,
        withComposeMaterialIconsExtended = withComposeMaterialIconsExtended,
        withComposeFullAnimation = withComposeFullAnimation,
        withComposeDesktop = withComposeDesktop,
        withComposeDesktopComponents = withComposeDesktopComponents,
        withComposeWebCore = withComposeWebCore,
        withComposeWebWidgets = withComposeWebWidgets,
        withComposeWebSvg = withComposeWebSvg,
        withComposeTestUiJUnit4 = withComposeTestUiJUnit4,
        withComposeTestWebUtils = withComposeTestWebUtils,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        if (withJs) js(IR) {
            binaries.executable()
        }
        if (withNativeLinux64) linuxX64 {
            binaries {
                executable {
                    entryPoint = "$appMainPackage.$appMainFun"
                }
            }
        }
    }
    if (withJvm) {
        compose.desktop {
            application {
                mainClass = "$appMainPackage.$appMainClass"
                nativeDistributions {
                    targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
                    packageName = details.name
                    packageVersion = details.version
                    description = details.description
                }
            }
        }
    }
}

// endregion [Compose MPP App Build Template]