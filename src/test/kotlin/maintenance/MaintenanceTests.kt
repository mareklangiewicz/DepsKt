@file:Suppress("UnusedImport")

package pl.mareklangiewicz.maintenance

import okio.FileSystem.Companion.SYSTEM
import okio.Path
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import pl.mareklangiewicz.io.readUtf8
import pl.mareklangiewicz.kommand.CliPlatform.Companion.SYS
import pl.mareklangiewicz.kommand.kommand
import pl.mareklangiewicz.ure.checkAllKnownRegionsSynced
import pl.mareklangiewicz.ure.downloadAndInjectFileToSpecialRegion
import pl.mareklangiewicz.ure.injectAllKnownRegionsToSync
import pl.mareklangiewicz.ure.injectCustomRegion
import pl.mareklangiewicz.uspek.*
import kotlin.math.absoluteValue
import kotlin.random.Random

// TODO_later: move the whole maintenance stuff to separate cli app, but inside deps.kt repo (but separate gradle and all)

class MaintenanceTests {

    // DepsKt$ ./gradlew cleanTest (if needed - especially after "successful" or "disabled" run)
    // DepsKt$ UPDATE_GENERATED_DEPS=true ./gradlew test --tests MaintenanceTests.updateGeneratedDeps
    @EnabledIfEnvironmentVariable(named = "UPDATE_GENERATED_DEPS", matches = "true")
    @Test
    fun updateGeneratedDeps() = downloadAndInjectFileToSpecialRegion(
        inFileUrl = "https://raw.githubusercontent.com/langara/refreshDeps/main/plugins/dependencies/src/test/resources/objects-for-deps.txt",
        outFilePath = "src/main/kotlin/deps/DepsNew.kt".toPath(),
        outFileRegionLabel = "Deps Generated"
    )

    // TODO: I have to run it from command line, due to issue (todo track it):
    // https://youtrack.jetbrains.com/issue/IDEA-320303
    // DepsKt$ ./gradlew cleanTest
    // DepsKt$ ./gradlew test --tests MaintenanceTests.maintenanceTestFactory
    @TestFactory
    fun maintenanceTestFactory() = uspekTestFactory {
//        "check all known regions synced" o { checkAllKnownRegionsSynced() }
        "check all known regions in all my kotlin projects" o { checkAllKnownRegionsInAllMyProjects() }
//        "check all workflows in all my kotlin projects" o { checkAllWorkflowsInAllMyProjects() }
//
//        "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
//        "DANGEROUS inject all known regions to all my projects" o { injectAllKnownRegionsToAllMyProjects() }
//        "DANGEROUS inject default workflows to all my projects" o { injectDefaultWorkflowsToAllMyProjects() }
//        "DANGEROUS inject default workflows to Some Proj" o { injectDefaultWorkflowsToMyProjects("KommandLine") }
//
//        "DANGEROUS updateDepsKtResourcesSymLinks" o { updateDepsKtResourcesSymLinks() }
//        "DANGEROUS updateGradlewFilesInAllMyProjects" o { updateGradlewFilesInAllMyProjects() }
//
//        "DANGEROUS someIgnoredStuff" o { someIgnoredStuff() }
//
//        "DANGEROUS inject hacky workflow to refreshDeps repo" o { injectHackyGenerateDepsWorkflowToRefreshDepsRepo() }
//        "DANGEROUS inject updateGeneratedDeps workflow to DepsKt repo" o { injectUpdateGeneratedDepsWorkflowToDepsKtRepo() }
    }
}