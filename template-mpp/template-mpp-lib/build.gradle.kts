import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.defaults.*

plugins {
    kotlin("multiplatform") version vers.kotlin
    id("maven-publish")
    id("signing")
}

repositories { defaultRepos() }

defaultGroupAndVerAndDescription(libs.TemplateMPP)

kotlin {
    jvm()
    jsDefault()
    linuxX64()
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(deps.uspekx)
            }
        }
    }
}

tasks.defaultKotlinCompileOptions()

tasks.defaultTestsOptions()

defaultPublishing(libs.TemplateMPP)

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

// region MPP Module Build Template

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

// endregion MPP Module Build Template