package pl.mareklangiewicz.deps

import org.gradle.api.*
import org.gradle.api.initialization.*

class DepsSettingsPlugin : Plugin<Settings> {
  override fun apply(target: Settings) =
    println("DepsSettingsPlugin ${Vers.DepsPlug.str} apply in project ${target.rootProject.name}")
}

class DepsPlugin : Plugin<Project> {
  override fun apply(target: Project) =
    println("DepsPlugin ${Vers.DepsPlug.str} apply in project ${target.name}")
}

