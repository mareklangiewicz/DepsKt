package pl.mareklangiewicz.maintenance

import org.junit.jupiter.api.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.uspek.*

class MaintenanceTests {

    @TestFactory
    fun maintenanceTestFactory() = uspekTestFactory {
        // "check all known regions synced" o { checkAllKnownRegionsSynced() }
        // "check all known regions in my kotlin projects" o { checkAllKnownRegionsInMyProjects() }
        // "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
        // "DANGEROUS inject all known regions to my projects" o { injectAllKnownRegionsToMyProjects() }

        "experiment" o {
            // updateDepsKtResourcesSymLinks()
        }
    }
}