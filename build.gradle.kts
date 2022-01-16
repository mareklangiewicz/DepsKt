plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.19.0"
    // https://plugins.gradle.org/docs/publish-plugin
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io") // TODO: try to use Repos.jitpack (see comment in settings.gradle.kts)
}

dependencies {
    api("com.squareup.okio:okio:3.0.0")
    api("com.android.tools.build:gradle-api:7.2.0-alpha07")
//    gradleApi()

    testImplementation("com.github.langara.USpek:uspekx:0.0.17") // TODO: try to use Deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2") // TODO: try to use Deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2") // TODO: try to use Deps.uspek (see comment in settings)
    // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        jvmTarget = "17"
    }
}

tasks.test {
    useJUnitPlatform()
}

group = "pl.mareklangiewicz.deps"
version = "0.2.15"


gradlePlugin {
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
        }
        create("depsSettingsPlugin") {
            id = "pl.mareklangiewicz.deps.settings"
            implementationClass = "pl.mareklangiewicz.deps.DepsSettingsPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/langara/deps.kt"
    vcsUrl = "https://github.com/langara/deps.kt"
    tags = listOf("bom", "dependencies")
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }

    (plugins) {

        "depsPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Deps.kt plugin"
        }

        "depsSettingsPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Deps.kt settings plugin"
        }
    }
}
