plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "pl.mareklangiewicz.deps"
version = "0.2.2"

gradlePlugin {
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
        }
    }
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}
