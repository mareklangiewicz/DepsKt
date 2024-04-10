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
  id("pl.mareklangiewicz.deps.settings") version "0.2.96" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.enterprise") version "3.16.2" // https://docs.gradle.com/enterprise/gradle-plugin/
}

// includeAndSubstituteBuild("../KommandLine", Langiewicz.kommandline.mvn, ":kommandline")

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    val scanPublishEnabled: Boolean =
      System.getenv("GITHUB_ACTIONS") == "true"
    // true // careful with publishing fails especially from my machine (privacy)

    publishOnFailureIf(scanPublishEnabled)
    // publishAlwaysIf(scanPublishEnabled)
  }
}
