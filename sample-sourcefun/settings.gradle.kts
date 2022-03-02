@file:Suppress("UnstableApiUsage")

rootProject.name = "sample-sourcefun"

pluginManagement {
    includeBuild("..") // deps.kt
}

plugins {
    id("pl.mareklangiewicz.deps.settings")
}

include(":sample-lib")
