import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*

private val libBuild = rootProjectPath / "template-andro-lib" / "build.gradle.kts"
private val appBuild = rootProjectPath / "template-andro-lib" / "build.gradle.kts"

fun injectTemplates() {
    injectKotlinModuleBuildTemplate(libBuild, appBuild)
    injectAndroModuleBuildTemplate(libBuild, appBuild)
}

fun checkTemplates() {
    checkKotlinModuleBuildTemplates(libBuild, appBuild)
    checkAndroModuleBuildTemplates(libBuild, appBuild)
}

tasks.registerAllThatGroupFun("inject", ::checkTemplates, ::injectTemplates)

// TODO NOW: template and publishing stuff like in template-mpp/build.gradle.kts