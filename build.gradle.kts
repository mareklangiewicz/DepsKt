plugins {
    kotlin("jvm") version "1.4.32"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.14.0"
    // https://plugins.gradle.org/docs/publish-plugin
}

repositories {
    mavenCentral()
}

group = "pl.mareklangiewicz.deps"
version = "0.2.08"

gradlePlugin {
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "DepsPlugin"
        }
        create("depsSettingsPlugin") {
            id = "pl.mareklangiewicz.deps.settings"
            implementationClass = "DepsSettingsPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/langara/deps.kt"
    vcsUrl = "https://github.com/langara/deps.kt"
    tags = listOf("bom", "dependencies")
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."

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