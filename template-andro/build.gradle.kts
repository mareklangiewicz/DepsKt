import okio.Path.Companion.toPath
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*

fun injectAndroAppBuildTemplate() = injectAndroAppBuildTemplate("template-andro-app/build.gradle.kts".toPath())
fun injectAndroLibBuildTemplate() = injectAndroLibBuildTemplate("template-andro-lib/build.gradle.kts".toPath())
fun checkAndroBuildTemplates() = pl.mareklangiewicz.ure.checkAndroBuildTemplates() // issues with vararg

tasks.registerAllThatGroupFun("inject",
    ::checkAndroBuildTemplates,
    ::injectAndroAppBuildTemplate,
    ::injectAndroLibBuildTemplate,
)

// TODO NOW: template and publishing stuff like in template-mpp/build.gradle.kts