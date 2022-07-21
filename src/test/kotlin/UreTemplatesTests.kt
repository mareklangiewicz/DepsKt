package pl.mareklangiewicz.ure

import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*

private val someOfMyKotlinProjects = listOf(
    "AbcdK",
    // "AutomateK",
    "CoEdges",
    "dbus-kotlin",
    "IPShareK",
    "Koder",
    "kokpit667",
    "KommandLine",
    "kthreelhu",
    "KWSocket",
    "MyBlog",
    "MyScripts",
    "MyStolenPlaygrounds",
    "RxDebugBridge",
    "RxEdges",
    "SMokK",
    "SourceFun",
    "TixyPlayground",
    "TupleK",
    "upue",
    "USpek",
    "uspek-js-playground",
    "uspek-painters",
    "UWidgets",
)

class UreTemplatesTests {

    @TestFactory
    fun ureTemplatesTestFactory() = uspekTestFactory {
        oCheckAllKnownRegionsSynced()
        oCheckAllKnownRegionsInKotlinProject("KommandLine")
        oCheckAllKnownRegionsInKotlinProject("AbcdK")
        // oCheckAllKnownRegionsInAllKotlinProjects()
        // oDangerousInjectAllKnownRegionsToSync()
        // oDangerousInjectAllKnownRegionsToKotlinProjects(*someOfMyKotlinProjects.toTypedArray())
    }

}

private val pathToKotlinProjects = "/home/marek/code/kotlin".toPath()

private fun oCheckAllKnownRegionsSynced() = "check all known regions synced" o { checkAllKnownRegionsSynced() }
private fun oCheckAllKnownRegionsInAllKotlinProjects() = "check all known regions in all kotlin projects" o {
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(pathToKotlinProjects, verbose = true)
}
private fun oCheckAllKnownRegionsInKotlinProject(projectDir: String) =
    "check all known regions in $projectDir project" o {
        SYSTEM.checkAllKnownRegionsInAllFoundFiles(pathToKotlinProjects / projectDir, verbose = true)
    }
private fun oDangerousInjectAllKnownRegionsToKotlinProject(projectDir: String) =
    "DANGEROUS inject all known regions to $projectDir project" o {
        SYSTEM.injectAllKnownRegionsToAllFoundFiles(pathToKotlinProjects / projectDir)
    }
private fun oDangerousInjectAllKnownRegionsToKotlinProjects(vararg projectDirs: String) =
    projectDirs.forEach { oDangerousInjectAllKnownRegionsToKotlinProject(it) }

private fun oDangerousInjectAllKnownRegionsToSync() =
    "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
