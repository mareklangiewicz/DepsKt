package pl.mareklangiewicz.maintenance

import okio.*
import okio.FileSystem.*
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*

internal val MyDepsKtRootPath: Path = "/home/marek/code/kotlin/deps.kt".toPath()

private val Path.isTmplSymlink get() =
    name.endsWith(".tmpl") && Companion.SYSTEM.metadata(this).symlinkTarget != null

internal fun updateDepsKtResourcesSymLinks() {
    val resPathRel = "src/main/resources".toPath()
    val resPathAbs = MyDepsKtRootPath / resPathRel
    FileSystem.SYSTEM.listRecursively(resPathAbs).forEach {
        if (FileSystem.SYSTEM.metadata(it).isDirectory) return@forEach
        check(it.isTmplSymlink) { "Unexpected file in resources: $it" }
        FileSystem.SYSTEM.delete(it)
    }
    FileSystem.SYSTEM.list(resPathAbs).forEach {
        check(FileSystem.SYSTEM.metadata(it).isDirectory) { "Some non directory left in resources: $it" }
        FileSystem.SYSTEM.deleteRecursively(it)
    }
    val buildFiles = FileSystem.SYSTEM
        .findAllFiles(MyDepsKtRootPath)
        .filter { it.segments.any { it.startsWith("template-") } }
        .filterExt("gradle.kts")
        .toList()
    val gradleStrNames =
        listOf("", ".bat").map { "gradlew$it" } + listOf("jar", "properties").map { "gradle/wrapper/gradle-wrapper.$it" }
    val gradleFiles = gradleStrNames.map { MyDepsKtRootPath / it }
    val files = buildFiles + gradleFiles
    files.forEach { srcAbs ->
        val srcRel = srcAbs.asRelativeTo(MyDepsKtRootPath)
        val linkRel = "src/main/resources".toPath() / srcRel.withName { "$it.tmpl" }
        val linkAbs = MyDepsKtRootPath / linkRel
        val targetDots = linkRel.parent!!.segments.joinToString("/") { ".." }
        val target = targetDots.toPath() / srcRel
        println("symlink $linkAbs -> $target")
        FileSystem.SYSTEM.createDirectories(linkAbs.parent!!)
        FileSystem.SYSTEM.createSymlink(linkAbs, target)
    }
}