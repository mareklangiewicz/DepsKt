plugins {
    kotlin("jvm") version "1.7.20"
    id("com.gradle.plugin-publish") version "1.0.0" // https://plugins.gradle.org/docs/publish-plugin
    id("signing")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("com.squareup.okio:okio:3.2.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.android.library:com.android.library.gradle.plugin:8.0.0-alpha02")
    // Warning: andro gradle plugin is needed here, so kotlin plugin can access BaseExtension class etc.
    // It is needed in android projects using deps.kt (which does: plugins { kotlin("android") }
    // It doesn't work if users just add this andro gradle plugin dependency in their projects.
    testImplementation("pl.mareklangiewicz:uspekx-junit5:0.0.25") // TODO: try to use deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1") // TODO: try to use deps... (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1") // TODO: try to use deps... (see comment in settings)
    // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc.)
}

tasks.defaultKotlinCompileOptions("17")

tasks.defaultTestsOptions()

group = "pl.mareklangiewicz.deps"
version = "0.2.19"


gradlePlugin {
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
            displayName = "Deps.kt plugin"
        }
        create("depsSettingsPlugin") {
            id = "pl.mareklangiewicz.deps.settings"
            implementationClass = "pl.mareklangiewicz.deps.DepsSettingsPlugin"
            displayName = "Deps.kt settings plugin"
        }
        create("sourceFunPlugin") {
            id = "pl.mareklangiewicz.sourcefun"
            implementationClass = "pl.mareklangiewicz.sourcefun.SourceFunPlugin"
            displayName = "SourceFun plugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/langara/deps.kt"
    vcsUrl = "https://github.com/langara/deps.kt"
    tags = listOf("bom", "dependencies")
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."
}

// region Copy&Paste Code for deps building special case

// Note: Can not import pl.mareklangiewicz.defaults.* here

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String,
    requiresOptIn: Boolean = true,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
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

// endregion Copy&Paste Code for deps building special case