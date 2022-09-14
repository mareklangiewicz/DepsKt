package pl.mareklangiewicz.ure

import okio.*
import pl.mareklangiewicz.io.*


fun FileSystem.readAndMatchUre(file: Path, vararg opts: RegexOption, init: UreProduct.() -> Unit): MatchResult? =
    readAndMatchUre(file, ure(*opts) { init() })

fun FileSystem.readAndMatchUre(file: Path, ure: Ure): MatchResult? = readUtf8(file).let { ure.compile().matchEntire(it) }

fun FileSystem.commentOutMultiplatformFunInFile(file: Path) {
    println("\ncommenting: $file") // FIXME:remove/ulog
    processFile(file, file, String::commentOutMultiplatformFun)
}

fun FileSystem.undoCommentOutMultiplatformFunInFile(file: Path) {
    println("\nundo comments: $file") // FIXME:remove/ulog
    processFile(file, file, String::undoCommentOutMultiplatformFun)
}

fun FileSystem.commentOutMultiplatformFunInEachKtFile(root: Path) =
    findAllFiles(root).filterExt("kt").forEach { commentOutMultiplatformFunInFile(it) }

fun FileSystem.undoCommentOutMultiplatformFunInEachKtFile(root: Path) =
    findAllFiles(root).filterExt("kt").forEach { undoCommentOutMultiplatformFunInFile(it) }
