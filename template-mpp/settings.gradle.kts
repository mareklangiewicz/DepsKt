@file:Suppress("UnstableApiUsage")

import okio.Path.Companion.toOkioPath
import pl.mareklangiewicz.evts.*

gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement {
    repositories {
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
