package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

context(project: Project)
fun testContextParameters() {
    println("Successfully used context parameters in project: ${project.name}")
}

// NOTE: deliberately NO `context(Project) val lib get() = gradle.extLib` here.
// It would read the AMBIENT details and silently ignore the per-module overrides that
// kgroundx-maintenance/-experiments/-workflows/-jupyter and both kommand modules rely on.
// The effective Lib / LibFlags is passed down as a context parameter instead,
// established by each defaultBuildTemplateFor* entry point.
