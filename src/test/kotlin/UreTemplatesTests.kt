package pl.mareklangiewicz.ure

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.myprojects.*
import pl.mareklangiewicz.uspek.*

class UreTemplatesTests {

    @TestFactory
    fun ureTemplatesTestFactory() = uspekTestFactory {
        "check all known regions synced" o { checkAllKnownRegionsSynced() }
        // "check all known regions in my kotlin projects" o { checkAllKnownRegionsInMyProjects() }
        // "DANGEROUS inject all known regions to sync" o { injectAllKnownRegionsToSync() }
        // "DANGEROUS inject all known regions to my projects" o { injectAllKnownRegionsToMyProjects() }

        "experiment" o {
            // updateResourcesSymLinks()
        }
    }
}

private val Path.isTmplSymlink get() =
    name.endsWith(".tmpl") && SYSTEM.metadata(this).symlinkTarget != null

private fun updateResourcesSymLinks() {
    val resPathRel = "src/main/resources".toPath()
    val resPathAbs = defaultDepsKtRootPath / resPathRel
    SYSTEM.listRecursively(resPathAbs).forEach {
        if (SYSTEM.metadata(it).isDirectory) return@forEach
        check(it.isTmplSymlink) { "Unexpected file in resources: $it" }
        SYSTEM.delete(it)
    }
    SYSTEM.list(resPathAbs).forEach {
        check(SYSTEM.metadata(it).isDirectory) { "Some non directory left in resources: $it" }
        SYSTEM.deleteRecursively(it)
    }
    val buildFiles = SYSTEM
        .findAllFiles(defaultDepsKtRootPath)
        .filter { it.segments.any { it.startsWith("template-") } }
        .filterExt("gradle.kts")
        .toList()
    val gradleStrNames =
        listOf("", ".bat").map { "gradlew$it" } + listOf("jar", "properties").map { "gradle/wrapper/gradle-wrapper.$it" }
    val gradleFiles = gradleStrNames.map { defaultDepsKtRootPath / it }
    val files = buildFiles + gradleFiles
    files.forEach { srcAbs ->
        val srcRel = srcAbs.asRelativeTo(defaultDepsKtRootPath)
        val linkRel = "src/main/resources".toPath() / srcRel.withName { "$it.tmpl" }
        val linkAbs = defaultDepsKtRootPath / linkRel
        val targetDots = linkRel.parent!!.segments.joinToString("/") { ".." }
        val target = targetDots.toPath() / srcRel
        println("symlink $linkAbs -> $target")
        SYSTEM.createDirectories(linkAbs.parent!!)
        SYSTEM.createSymlink(linkAbs, target)
    }
}
