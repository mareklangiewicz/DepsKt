@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.evts.*
import pl.mareklangiewicz.utils.rootProjectPath

//gradle.logSomeEventsToFile(rootProjectPath / "my.gradle.log")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    includeBuild("..") // DepsKt
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.42" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.enterprise") version "3.13.4" // https://docs.gradle.com/enterprise/gradle-plugin/
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(System.getenv("GITHUB_ACTIONS") == "true")
        publishOnFailure()
    }
}

rootProject.name = "template-mpp"

include(":template-mpp-lib")
include(":template-mpp-app")
include(":template-jvm-lib")
include(":template-jvm-cli")
