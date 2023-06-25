package pl.mareklangiewicz.maintenance

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*

internal val MyDepsKtRootPath = "/home/marek/code/kotlin/DepsKt".toPath()

private val resourcesRelPath = "src/main/resources".toPath()
private val resourcesAbsPath = MyDepsKtRootPath / resourcesRelPath

private val Path.isTmplSymlink
    get() = name.endsWith(".tmpl") && SYSTEM.metadata(this).symlinkTarget != null

fun updateDepsKtResourcesSymLinks(log: (Any?) -> Unit = ::println) = SYSTEM.run {
    listRecursively(resourcesAbsPath).forEach {
        if (metadata(it).isDirectory) return@forEach
        check(it.isTmplSymlink) { "Unexpected file in resources: $it" }
        delete(it)
    }
    list(resourcesAbsPath).forEach {
        check(metadata(it).isDirectory) { "Some non directory left in resources: $it" }
        deleteRecursively(it)
    }
    val buildFiles = findAllFiles(MyDepsKtRootPath)
        .filter { it.segments.any { it.startsWith("template-") } }
        .filterExt("gradle.kts")
        .toList()
    val gradlewFiles = gradlewRelPaths.map { MyDepsKtRootPath / it }
    val files = buildFiles + gradlewFiles
    files.forEach { srcAbs ->
        val srcRel = srcAbs.asRelativeTo(MyDepsKtRootPath)
        val linkRel = resourcesRelPath / srcRel.withName { "$it.tmpl" }
        val linkAbs = MyDepsKtRootPath / linkRel
        val targetDots = linkRel.parent!!.segments.joinToString("/") { ".." }
        val target = targetDots.toPath() / srcRel
        log("symlink $linkAbs -> $target")
        createDirectories(linkAbs.parent!!)
        createSymlink(linkAbs, target)
    }
}
