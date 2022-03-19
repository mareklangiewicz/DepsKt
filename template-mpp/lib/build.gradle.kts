import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(deps.uspekx)
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(deps.junit5engine)
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions { jvmTarget = vers.defaultJvm }
}

tasks.withType<AbstractTestTask> {
    testLogging {
        showStandardStreams = true
        showStackTraces = true
    }
}

defaultPublishing(libs.TemplateMPP)

defaultSigning()

// region Kotlin Multi Template

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

// endregion Kotlin Multi Template