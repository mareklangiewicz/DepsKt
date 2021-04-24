plugins {
    id("com.gradle.plugin-publish") version "0.14.0"
        // https://plugins.gradle.org/docs/publish-plugin
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

group = "pl.mareklangiewicz.deps"
version = "0.2.3"

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
    vcsUrl = "https://github.com/langara/deps.kt"

    // tags and description can be set for the whole bundle here, but can also
    // be set / overridden in the config for specific plugins
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."

    // The plugins block can contain multiple plugin entries.
    //
    // The name for each plugin block below (greetingsPlugin, goodbyePlugin)
    // does not affect the plugin configuration, but they need to be unique
    // for each plugin.

    // Plugin config blocks can set the id, displayName, version, description
    // and tags for each plugin.

    // id and displayName are mandatory.
    // If no version is set, the project version will be used.
    // If no tags or description are set, the tags or description from the
    // pluginBundle block will be used, but they must be set in one of the
    // two places.

    (plugins) {

        // first plugin
        "depsPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Deps.kt plugin"
        }

        // another plugin
        "depsSettingsPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Depps.kt settings plugin"
        }
    }
}