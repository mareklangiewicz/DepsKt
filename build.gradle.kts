plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
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

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}
