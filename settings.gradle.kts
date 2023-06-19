import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.includeAndSubstituteBuild

rootProject.name = "DepsKt"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("pl.mareklangiewicz.deps.settings") version "0.2.40" // https://plugins.gradle.org/search?term=mareklangiewicz
    id("com.gradle.enterprise") version "3.13.3" // https://docs.gradle.com/enterprise/gradle-plugin/
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(System.getenv("GITHUB_ACTIONS") == "true")
        publishOnFailure()
    }
}
