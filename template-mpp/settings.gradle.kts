@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.rootProjectPath

//gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
    repositories {
        google() // unfortunately needed for deps.kt, because it has to add andro gradle plugin to classpath
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    includeBuild("..") // deps.kt
}

plugins { id("pl.mareklangiewicz.deps.settings") }

rootProject.name = "template-mpp"

include(":template-mpp-lib")
include(":template-mpp-app")
include(":template-jvm-cli")
