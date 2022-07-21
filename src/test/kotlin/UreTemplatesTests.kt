package pl.mareklangiewicz.ure

import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*

class UreTemplatesTests {

    @TestFactory
    fun ureTemplatesTestFactory() = uspekTestFactory {
        oCheckAllKnownRegionsSynced()
        oCheckAllKnownRegionsInKotlinProject("KommandLine")
        oCheckAllKnownRegionsInKotlinProject("AbcdK")
        // oCheckAllKnownRegionsInAllKotlinProjects()
        // oDangerousInjectAllKnownRegionsToSync()
        // oDangerousInjectAllKnownRegionsToKotlinProject("AbcdK")
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
private fun oDangerousInjectAllKnownRegionsToSync() =
    "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
