@file:Suppress("UnstableApiUsage")

rootProject.name = "sample-sourcefun"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
//    includeBuild("..") // DepsKt
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.25"
}

include(":sample-lib")
