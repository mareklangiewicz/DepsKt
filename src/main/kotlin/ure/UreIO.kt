package pl.mareklangiewicz.ure

import pl.mareklangiewicz.io.*
import okio.*


fun FileSystem.commentOutMultiplatformFunInEachKtFile(root: Path) =
    findAllFiles(root).filterExt("kt").forEach { commentOutMultiplatformFunInFile(it) }

fun FileSystem.undoCommentOutMultiplatformFunInEachKtFile(root: Path) =
    findAllFiles(root).filterExt("kt").forEach { undoCommentOutMultiplatformFunInFile(it) }



fun FileSystem.readAndMatchUre(file: Path, vararg opts: RegexOption, init: UreProduct.() -> Unit): MatchResult? =
    readAndMatchUre(file, ure(*opts) { init() })

fun FileSystem.readAndMatchUre(file: Path, ure: Ure): MatchResult? = readUtf8(file).let { ure.compile().matchEntire(it) }

fun FileSystem.commentOutMultiplatformFunInFile(file: Path) {
    println("\ncommenting: $file") // FIXME:remove/ulog

    processFile(file, file) { input ->

        val output1 = ureExpectFun.notCommentedOut().compile()
            .replace(input) { "/*\n${it.value}\n*/" }

        val output2 = ir("actual fun").compile().replace(output1) { "/*actual*/ fun" }

        ir("actual suspend fun").compile().replace(output2) { "/*actual*/ suspend fun" }
        // FIXME: merge this two replaces to one using better URE
    }
}

fun FileSystem.undoCommentOutMultiplatformFunInFile(file: Path) {
    println("\nundo comments: $file") // FIXME:remove/ulog

    processFile(file, file) { input ->

        val myFun = ure("myFun") { 1 of ureExpectFun }

        val output1 = myFun.commentedOut().compile().replace(input) { it["myFun"] }

        ir("/\\*actual\\*/").compile().replace(output1) { "actual" }
    }
}
