import okio.Path.Companion.toPath
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*

fun injectTemplates() {
    injectKotlinModuleBuildTemplate("template-andro-lib/build.gradle.kts".toPath())
    injectKotlinModuleBuildTemplate("template-andro-app/build.gradle.kts".toPath())
    injectAndroModuleBuildTemplate("template-andro-lib/build.gradle.kts".toPath())
    injectAndroModuleBuildTemplate("template-andro-app/build.gradle.kts".toPath())
}

fun checkTemplates() {
    checkKotlinModuleBuildTemplates(
        "template-andro-lib/build.gradle.kts".toPath(),
        "template-andro-app/build.gradle.kts".toPath(),
    )
    checkAndroModuleBuildTemplates(
        "template-andro-lib/build.gradle.kts".toPath(),
        "template-andro-app/build.gradle.kts".toPath(),
    )
}

tasks.registerAllThatGroupFun("inject",
    ::checkTemplates,
    ::injectTemplates,
)

// TODO NOW: template and publishing stuff like in template-mpp/build.gradle.kts