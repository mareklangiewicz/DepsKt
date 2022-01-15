package pl.mareklangiewicz.deps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

class DepsSettingsPlugin: Plugin<Settings> {
    override fun apply(target: Settings) = println("DepsSettingsPlugin.apply(settings for ${target.rootProject.name})")
}

class DepsPlugin: Plugin<Project> {
    override fun apply(target: Project) = println("DepsPlugin.apply(project ${target.name})")
}

// https://publicobject.com/2021/03/11/includebuild/

fun Settings.includeAndSubstituteBuild(rootProject: Any, substituteModule: String, withProject: String) {
    includeBuild(rootProject) {
        dependencySubstitution {
            substitute(module(substituteModule))
                .using(project(withProject))
        }
    }
}
