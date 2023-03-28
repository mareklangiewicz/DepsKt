
rootProject.name = "DepsKt"

pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.26"
    id("de.fayard.refreshVersions") version "0.51.0" // See https://jmfayard.github.io/refreshVersions
}
