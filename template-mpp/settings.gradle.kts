@file:Suppress("UnstableApiUsage")

import okio.Path.Companion.toOkioPath
import pl.mareklangiewicz.evts.*

gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement { includeBuild("..") } // deps.kt

plugins { id("pl.mareklangiewicz.deps.settings") }

rootProject.name = "template-mpp"

include(":template-mpp-lib")
//include(":template-mpp-app-jvm") TODO
//include(":template-mpp-app-web") TODO
//include(":template-mpp-app-native") TODO