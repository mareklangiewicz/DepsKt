@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.rootProjectPath

//gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
//    includeBuild("..") // deps.kt
}

plugins { id("pl.mareklangiewicz.deps.settings") version "0.2.25" }

rootProject.name = "template-mpp"

include(":template-mpp-lib")
include(":template-mpp-app")
include(":template-jvm-cli")
