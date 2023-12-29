import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.includeAndSubstituteBuild

rootProject.name = "DepsKt"

pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.77" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.enterprise") version "3.16.1" // https://docs.gradle.com/enterprise/gradle-plugin/
}

//includeAndSubstituteBuild("../KommandLine", Langiewicz.kommandline.mvn, ":kommandline")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(System.getenv("GITHUB_ACTIONS") == "true")
        publishOnFailure()
    }
}
