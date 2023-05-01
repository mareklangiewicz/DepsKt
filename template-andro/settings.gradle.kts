@file:Suppress("UnstableApiUsage")

pluginManagement {
   includeBuild("..") // this is DepsKt
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.36"
}

rootProject.name = "template-andro"
include(":template-andro-app")
include(":template-andro-lib")

