@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("..") // this is deps.kt
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

rootProject.name = "template-andro"
include(":template-andro-app")
include(":template-andro-lib")

