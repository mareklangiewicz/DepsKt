@file:OptIn(ExperimentalFileSystem::class)

import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.buffer
import kotlin.io.println
import kotlin.io.use
import kotlin.text.RegexOption.MULTILINE

fun forAllKtFiles(path: Path, action: FileSystem.(Path) -> Unit) {
    val fs = FileSystem.SYSTEM
    val files = fs.findAllFiles(path).mapNotNull {
        it.takeIf { it.name.endsWith(".kt") }
    }
    files.forEach { fs.action(it) }
}


@Throws(IOException::class)
fun FileSystem.findAllFiles(path: Path, maxDepth: Int = Int.MAX_VALUE): Sequence<Path> {
    require(maxDepth >= 0)
    val md = metadata(path)
    return when {
        md.isRegularFile -> sequenceOf(path)
        maxDepth < 1 || !md.isDirectory -> emptySequence()
        else -> list(path).asSequence().flatMap { findAllFiles(it, maxDepth - 1) }
    }
}

fun commentOutMultiplatformFunInFileTree(path: Path) =
    forAllKtFiles(path) { commentOutMultiplatformFunInFile(it) }

fun undoCommentOutMultiplatformFunInFileTree(path: Path) =
    forAllKtFiles(path) { undoCommentOutMultiplatformFunInFile(it) }


fun FileSystem.commentOutMultiplatformFunInFile(file: Path) {
    println("\ncommenting: $file") // FIXME:remove/ulog

    val input = source(file).buffer().use { it.readUtf8() }

    val output1 = ureNotCommentedOutArea(ureExpectFun)
        .compile(MULTILINE)
        .replace(input) { "/*\n${it.value}\n*/" }

    val output2 = ir("actual fun")
        .compile()
        .replace(output1) { "/*actual*/ fun" }

    sink(file).buffer().use { it.writeUtf8(output2) }
}

fun FileSystem.undoCommentOutMultiplatformFunInFile(file: Path) {
    println("\nundo comments: $file") // FIXME:remove/ulog

    val input = source(file).buffer().use { it.readUtf8() }

    val myFun = named("myFun") { 1 of ureExpectFun }

    val output1 = ureCommentedOutArea(myFun)
        .compile(MULTILINE)
        .replace(input) { it.groups["myFun"]!!.value }

    val output2 = ir("/\\*actual\\*/ fun")
        .compile()
        .replace(output1) { "actual fun" }

    sink(file).buffer().use { it.writeUtf8(output2) }
}
