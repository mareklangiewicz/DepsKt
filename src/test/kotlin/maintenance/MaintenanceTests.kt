@file:Suppress("UnusedImport")

package pl.mareklangiewicz.maintenance

import org.junit.jupiter.api.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.uspek.*

// TODO_later: move whole maintenance stuff to separate cli app, but inside deps.kt repo (but separate gradle and all)

class MaintenanceTests {

    @TestFactory
    fun maintenanceTestFactory() = uspekTestFactory {
        // "check all known regions synced" o { checkAllKnownRegionsSynced() }
        // "check all known regions in all my kotlin projects" o { checkAllKnownRegionsInAllMyProjects() }
        // "check all workflows in all my kotlin projects" o { checkAllWorkflowsInAllMyProjects() }
        // "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
        // "DANGEROUS inject all known regions to all my projects" o { injectAllKnownRegionsToAllMyProjects() }
        // "DANGEROUS inject default workflows to all my projects" o { injectDefaultWorkflowsToAllMyProjects() }
        // "DANGEROUS inject default workflows to AbcdK" o { injectDefaultWorkflowsToMyProjects("AbcdK") }
        // "DANGEROUS inject default workflows to TupleK" o { injectDefaultWorkflowsToMyProjects("TupleK") }

        "experiment" o {
            // updateDepsKtResourcesSymLinks()
            // updateGradlewFilesInAllMyProjects()
        }
    }
}