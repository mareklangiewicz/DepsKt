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
  id("pl.mareklangiewicz.deps.settings") version "0.2.98" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "3.17.1" // https://docs.gradle.com/enterprise/gradle-plugin/
}

// includeAndSubstituteBuild("../KommandLine", Langiewicz.kommandline.mvn, ":kommandline")

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { // careful with publishing fails especially from my machine (privacy)
      true &&
        it.buildResult.failures.isNotEmpty() &&
        // it.buildResult.failures.isEmpty() &&
        System.getenv("GITHUB_ACTIONS") == "true" &&
        // System.getenv("GITHUB_ACTIONS") != "true" &&
        true
        // false
    }
  }
}
