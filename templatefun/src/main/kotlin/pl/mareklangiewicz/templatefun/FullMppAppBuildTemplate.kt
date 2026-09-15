package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[Full MPP App Build Template]]

fun Project.defaultBuildTemplateForFullMppApp(
  lib: Lib = gradle.extLib,
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) {
  defaultBuildTemplateForComposeMppApp(
    lib = lib,
    ignoreAndroConfig = true,
    addCommonMainDependencies = addCommonMainDependencies,
  )
}


// endregion [[Full MPP App Build Template]]
