import org.jetbrains.compose.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    plugAll(plugs.KotlinMulti, plugs.Compose)
}

// workaround for crazy gradle bugs like this one or simillar:
// https://youtrack.jetbrains.com/issue/KT-43500/KJS-IR-Failed-to-resolve-Kotlin-library-on-attempting-to-resolve-compileOnly-transitive-dependency-from-direct-dependency
repositories { maven(repos.composeJbDev) }

defaultBuildTemplateForComposeMppApp(
    appMainPackage = "pl.mareklangiewicz.hello",
    withJs = true,
    withNativeLinux64 = false,
    withKotlinxHtml = true,
    withComposeCompilerVer = VersNew.ComposeCompiler,
    // FIXME: stop using custom andro cimpiler when compose multiplatform supports kotlin 1.9.0
    // https://github.com/JetBrains/compose-multiplatform/issues/3323
) {
    implementation(project(":template-mpp-lib"))
}


// // Fixes webpack-cli incompatibility by pinning the newest version.
// // https://youtrack.jetbrains.com/issue/KT-52776/KJS-Gradle-Webpack-version-update-despite-yarnlock-breaks-KotlinJS-build
// // also see: https://github.com/renovatebot/renovate/issues/13573#issuecomment-1013864638
// // and: https://www.npmjs.com/package/webpack
// rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
//     versions.webpack.version = "5.74.0"
//     versions.webpackCli.version = "4.10.0"
// }

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

// region [MPP Module Build Template]

/** Only for very standard small libs. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForMppLib(
    details: LibDetails = rootExtLibDetails,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withComposeCompilerAndroidxDevRepo: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    repositories {
        defaultRepos(
            withKotlinxHtml = withKotlinxHtml,
            withComposeJbDev = withComposeJbDevRepo,
            withComposeCompilerAndroidxDev = withComposeCompilerAndroidxDevRepo,
        )
    }
    defaultGroupAndVerAndDescription(details)
    kotlin {
        allDefault(
            withJvm,
            withJs,
            withNativeLinux64,
            withKotlinxHtml,
            withTestJUnit4,
            withTestJUnit5,
            withTestUSpekX,
            addCommonMainDependencies
        )
    }
    configurations.checkVerSync()
    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
    if (plugins.hasPlugin("maven-publish")) {
        defaultPublishing(details)
        if (plugins.hasPlugin("signing")) defaultSigning()
        else println("MPP Module ${name}: signing disabled")
    } else println("MPP Module ${name}: publishing (and signing) disabled")
}

/** Only for very standard small libs. In most cases it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
fun KotlinMultiplatformExtension.allDefault(
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    if (withJvm) jvm()
    if (withJs) jsDefault()
    if (withNativeLinux64) linuxX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                if (withKotlinxHtml) implementation(KotlinX.html)
                addCommonMainDependencies()
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                if (withTestUSpekX) implementation(Langiewicz.uspekx)
            }
        }
        if (withJvm) {
            val jvmTest by getting {
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
    details: LibDetails = rootExtLibDetails,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeJbDevRepo: Boolean = false,
    withTestJUnit4: Boolean = false,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = withComposeJbDevRepo,
        withTestJUnit4 = withTestJUnit4,
        withTestJUnit5 = withTestJUnit5,
        withTestUSpekX = withTestUSpekX,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        if (withJvm) jvm {
            println("MPP App ${project.name}: Generating general jvm executables with kotlin multiplatform plugin is not supported (without compose).")
            // TODO_someday: Will they support multiplatform way of declaring jvm app?
            // binaries.executable()
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

/** Only for very standard compose mpp libs. In most cases, it's better to not use this function. */
@Suppress("UNUSED_VARIABLE")
@OptIn(ExperimentalComposeLibrary::class)
fun Project.defaultBuildTemplateForComposeMppLib(
    details: LibDetails = rootExtLibDetails,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeCompilerVer: Ver? = null,
    withComposeUi: Boolean = true,
    withComposeFoundation: Boolean = true,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = false,
        // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended?repo=space-public-compose-dev
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = false,
        // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane?repo=space-public-compose-dev
    withComposeWebCore: Boolean = withJs,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    if (withComposeCompilerVer != null) compose {
        val cc = AndroidX.Compose.Compiler.compiler.withVer(withComposeCompilerVer)
        kotlinCompilerPlugin.set(cc.mvn)
    }
    defaultBuildTemplateForMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDevRepo = true,
        withComposeCompilerAndroidxDevRepo = withComposeCompilerVer != null,
        withTestJUnit4 = withComposeTestUiJUnit4, // Unfortunately Compose UI still uses JUnit4 instead of 5
        withTestJUnit5 = false,
        withTestUSpekX = true,
        addCommonMainDependencies = addCommonMainDependencies
    )
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(compose.runtime)
                    if (withComposeUi) {
                        implementation(compose.ui)
                    }
                    if (withComposeFoundation) implementation(compose.foundation)
                    if (withComposeFullAnimation) {
                        implementation(compose.animation)
                        implementation(compose.animationGraphics)
                    }
                    if (withComposeMaterial2) implementation(compose.material)
                    if (withComposeMaterial3) implementation(compose.material3)
                }
            }
            if (withJvm) {
                val jvmMain by getting {
                    dependencies {
                        if (withComposeUi) {
                            implementation(compose.uiTooling)
                            implementation(compose.preview)
                        }
                        if (withComposeMaterialIconsExtended) implementation(compose.materialIconsExtended)
                        if (withComposeDesktop) {
                            implementation(compose.desktop.common)
                            implementation(compose.desktop.currentOs)
                        }
                        if (withComposeDesktopComponents) {
                            implementation(compose.desktop.components.splitPane)
                        }
                    }
                }
                val jvmTest by getting {
                    dependencies {
                        if (withComposeTestUiJUnit4) implementation(compose.uiTestJUnit4)
                    }
                }
            }
            if (withJs) {
                val jsMain by getting {
                    dependencies {
                        implementation(compose.runtime)
                        if (withComposeWebCore) implementation(compose.html.core)
                        if (withComposeWebSvg) implementation(compose.html.svg)
                    }
                }
                val jsTest by getting {
                    dependencies {
                        if (withComposeTestWebUtils) implementation(compose.html.testUtils)
                    }
                }
            }
        }
    }
}

// endregion [Compose MPP Module Build Template]

// region [Compose MPP App Build Template]

/** Only for very standard compose mpp apps. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForComposeMppApp(
    appMainPackage: String,
    appMainClass: String = "App_jvmKt", // for compose jvm
    appMainFun: String = "main", // for native
    details: LibDetails = rootExtLibDetails,
    withJvm: Boolean = true,
    withJs: Boolean = true,
    withNativeLinux64: Boolean = false,
    withKotlinxHtml: Boolean = false,
    withComposeCompilerVer: Ver? = null,
    withComposeUi: Boolean = true,
    withComposeFoundation: Boolean = true,
    withComposeMaterial2: Boolean = withJvm,
    withComposeMaterial3: Boolean = withJvm,
    withComposeMaterialIconsExtended: Boolean = false,
        // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended?repo=space-public-compose-dev
    withComposeFullAnimation: Boolean = withJvm,
    withComposeDesktop: Boolean = withJvm,
    withComposeDesktopComponents: Boolean = false,
        // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane?repo=space-public-compose-dev
    withComposeWebCore: Boolean = withJs,
    withComposeWebSvg: Boolean = withJs,
    withComposeTestUiJUnit4: Boolean = withJvm,
    withComposeTestWebUtils: Boolean = withJs,
    addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
    defaultBuildTemplateForComposeMppLib(
        details = details,
        withJvm = withJvm,
        withJs = withJs,
        withNativeLinux64 = withNativeLinux64,
        withKotlinxHtml = withKotlinxHtml,
        withComposeCompilerVer = withComposeCompilerVer,
        withComposeUi = withComposeUi,
        withComposeFoundation = withComposeFoundation,
        withComposeMaterial2 = withComposeMaterial2,
        withComposeMaterial3 = withComposeMaterial3,
        withComposeMaterialIconsExtended = withComposeMaterialIconsExtended,
        withComposeFullAnimation = withComposeFullAnimation,
        withComposeDesktop = withComposeDesktop,
        withComposeDesktopComponents = withComposeDesktopComponents,
        withComposeWebCore = withComposeWebCore,
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
                    packageVersion = details.version?.ver
                    description = details.description
                }
            }
        }
    }
}

// endregion [Compose MPP App Build Template]