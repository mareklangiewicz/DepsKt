import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm") version vers.kotlin
    application
}

defaultBuildTemplateForJvmApp(
    appMainPackage = "pl.mareklangiewicz.hello.cli",
    appMainClass = "MainCliKt",
    details = libs.TemplateMPP,
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

// region [Jvm App Build Template]

@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForJvmApp(
    appMainPackage: String,
    appMainClass: String = "MainKt",
    details: LibDetails = libs.Unknown,
    addMainDependencies: KotlinDependencyHandler.() -> Unit = {}
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
        }
    }

    application { mainClass put "$appMainPackage.$appMainClass" }

    tasks.defaultKotlinCompileOptions()
}

// endregion [Jvm App Build Template]