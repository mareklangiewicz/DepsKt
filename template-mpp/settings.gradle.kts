@file:Suppress("UnstableApiUsage")

import okio.Path.Companion.toOkioPath
import pl.mareklangiewicz.evts.*

gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement { includeBuild("..") } // deps.kt

plugins { id("pl.mareklangiewicz.deps.settings") }

rootProject.name = "template-mpp"

include(":template-mpp-lib")
include(":template-jvm-cli")
include(":template-mpp-app")
//include(":template-native-cli") TODO