@file:Suppress("UnusedImport")

package pl.mareklangiewicz.maintenance

import okio.FileSystem.Companion.SYSTEM
import okio.Path
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.io.readUtf8
import pl.mareklangiewicz.kommand.Platform.Companion.SYS
import pl.mareklangiewicz.kommand.kommand
import pl.mareklangiewicz.ure.injectCustomRegion
import pl.mareklangiewicz.uspek.*

// TODO_later: move the whole maintenance stuff to separate cli app, but inside deps.kt repo (but separate gradle and all)

// TODO: Add curl to KommandLine library, then use it here
private fun kdownload(inUrl: String, outPath: Path) =
    kommand("curl", "-o", outPath.toString(), inUrl)

class MaintenanceTests {

    @Disabled
    @Test
    fun downloadAndInjectDepsObjects() = SYS.run {
        val objsName = "objects-for-deps.txt"
        val objsPath = "build".toPath() / objsName
        val url = "https://raw.githubusercontent.com/langara/refreshDeps/main/plugins/dependencies/src/test/resources/$objsName"
        kdownload(url, objsPath)()
        val regionContent = SYSTEM.readUtf8(objsPath)
        val region = "// region [Deps Generated]\n\n$regionContent\n// endregion [Deps Generated]"
        val depsPath = "src/main/kotlin/deps/DepsNew.kt".toPath()
        SYSTEM.injectCustomRegion("Deps Generated", region, depsPath)
    }

    @TestFactory
    fun maintenanceTestFactory() = uspekTestFactory {
        // "check all known regions synced" o { checkAllKnownRegionsSynced() }
        // "check all known regions in all my kotlin projects" o { checkAllKnownRegionsInAllMyProjects() }
        // "check all workflows in all my kotlin projects" o { checkAllWorkflowsInAllMyProjects() }
        // "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
        // "DANGEROUS inject all known regions to all my projects" o { injectAllKnownRegionsToAllMyProjects() }
        // "DANGEROUS inject default workflows to all my projects" o { injectDefaultWorkflowsToAllMyProjects() }
        // "DANGEROUS inject default workflows to Some Proj" o { injectDefaultWorkflowsToMyProjects("KommandLine") }
        // "DANGEROUS someIgnoredStuff" o { someIgnoredStuff() }
        // "DANGEROUS inject hacky workflow to refreshDeps repo" o { injectHackyGenerateDepsWorkflowToRefreshDepsRepo() }

        "experiment" o {
            // updateDepsKtResourcesSymLinks()
            // updateGradlewFilesInAllMyProjects()
        }
    }
}