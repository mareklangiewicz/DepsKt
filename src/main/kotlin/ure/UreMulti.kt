package pl.mareklangiewicz.ure

fun String.commentOutMultiplatformFun(): String {
    val output1 = ureExpectFun.notCommentedOut().compile().replace(this) { "/*\n${it.value}\n*/" }
    val output2 = ir("actual fun").compile().replace(output1) { "/*actual*/ fun" }
    return ir("actual suspend fun").compile().replace(output2) { "/*actual*/ suspend fun" }
    // FIXME_maybe: merge this two replaces to one using better URE
}

fun String.undoCommentOutMultiplatformFun(): String {
    val myFun = ure("myFun") { 1 of ureExpectFun }
    val output1 = myFun.commentedOut().compile().replace(this) { it["myFun"] }
    return ir("/\\*actual\\*/").compile().replace(output1) { "actual" }
}

// TODO: refactor - also check where should I change space to spaceInLine
val ureExpectFun = ure {
    val keyword = ure {
        1 of wordBoundary
        1..MAX of posixLower
        1 of wordBoundary
    }
    1 of BOL
    0..1 of { 1 of ir("@Composable"); 1..MAX of space }
    0..MAX of { 1 of keyword; 1..MAX of space }
    1 of ir("expect ")
    0..1 of ir("suspend ")
    1 of ir("fun")
    1..MAX of space
    1..MAX of word // funname
    1 of { // (..,..,..)
        1 of ch("\\(")
        0..MAX of any
        1 of ch("\\)")
    }
    val typedef = ure {
        1 of word
        0..1 of {
            0..1 of space
            ch("\\<")
            1..MAX of any
            ch("\\>")
        }
    }
    0..1 of { // :Type<..>
        0..1 of space
        1 of ch(":")
        0..MAX of space
        1 of typedef
    }
    0..MAX of space
    1 of EOL
}
