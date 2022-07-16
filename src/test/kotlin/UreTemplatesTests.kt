package pl.mareklangiewicz.ure

import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*

class UreTemplatesTests {

    @TestFactory
    fun ureTemplatesTestFactory() = uspekTestFactory {
        "check all build regions sync" o {
            checkAllKnownRegionsSynced()
        }
        "check all known regions in KommandLine project" ox {
            val myKotlinSrcCodeRoot = "/home/marek/code/kotlin/KommandLine".toPath()
            SYSTEM.checkAllKnownRegionsInAllFoundFiles(myKotlinSrcCodeRoot, verbose = true)
        }
    }
}