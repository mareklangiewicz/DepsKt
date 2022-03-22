import okio.Path.Companion.toPath
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*

fun injectAndroLibBuildTemplate() = injectAndroLibBuildTemplate("template-andro-lib/build.gradle.kts".toPath())
fun injectAndroAppBuildTemplate() = injectAndroAppBuildTemplate("template-andro-app/build.gradle.kts".toPath())
fun checkAndroBuildTemplates() = checkAndroBuildTemplates(
    "template-andro-lib/build.gradle.kts".toPath(),
    "template-andro-app/build.gradle.kts".toPath(),
)

tasks.registerAllThatGroupFun("inject",
    ::checkAndroBuildTemplates,
    ::injectAndroAppBuildTemplate,
    ::injectAndroLibBuildTemplate,
)

// TODO NOW: template and publishing stuff like in template-mpp/build.gradle.kts