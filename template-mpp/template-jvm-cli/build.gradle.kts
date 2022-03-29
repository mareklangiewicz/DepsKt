import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm") version vers.kotlin
    application
}

defaultGroupAndVerAndDescription(libs.TemplateMPP)

repositories { defaultRepos() }

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":template-mpp-lib"))
            }
        }
    }
}

application { mainClass put "pl.mareklangiewicz.hello.cli.MainCliKt" }

tasks.defaultKotlinCompileOptions()


// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true,
) {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = jvmTargetVer
            if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

// endregion [Kotlin Module Build Template]
