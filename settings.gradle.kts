
rootProject.name = "deps.kt"

pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("pl.mareklangiewicz.deps.settings") version "0.2.23" }
