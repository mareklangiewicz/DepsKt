import okio.Path.Companion.toPath
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*

fun injectAndroAppBuildTemplate() = injectAndroAppBuildTemplate("app/build.gradle.kts".toPath())
fun injectAndroLibBuildTemplate() = injectAndroLibBuildTemplate("lib/build.gradle.kts".toPath())

tasks.registerAllThatGroupFun("inject",
    ::checkAndroBuildTemplates,
    ::injectAndroAppBuildTemplate,
    ::injectAndroLibBuildTemplate,
)

