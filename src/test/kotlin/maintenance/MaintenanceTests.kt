package pl.mareklangiewicz.maintenance

import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*

class MaintenanceTests {

    @TestFactory
    fun maintenanceTestFactory() = uspekTestFactory {
        // "check all known regions synced" o { checkAllKnownRegionsSynced() }
        // "check all known regions in all my kotlin projects" o { checkAllKnownRegionsInAllMyProjects() }
        // "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
        // "DANGEROUS inject all known regions to all my projects" o { injectAllKnownRegionsToAllMyProjects() }

        "experiment" o {
            // updateDepsKtResourcesSymLinks()
            // updateGradlewFilesInAllMyProjects()
        }
    }
}