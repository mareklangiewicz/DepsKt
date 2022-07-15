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
    }


    // experiment with this test manually, but
    // be careful not to commit this "test" without @Ignore
    // @Ignore
    @Test
    fun checkAllKnownRegionsInAllMyKotlinCode() {
        // val myKotlinSrcCodeRoot = "/home/marek/code/kotlin".toPath(normalize = true)
        val myKotlinSrcCodeRoot = "/home/marek/code/kotlin/KommandLine".toPath(normalize = true)
        SYSTEM.checkAllKnownRegionsInAllFoundFiles(myKotlinSrcCodeRoot, verbose = true)
    }
}