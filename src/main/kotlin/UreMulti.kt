package pl.mareklangiewicz.deps

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

fun ureNotCommentedOutArea(area: Ure, maxSpacesBehind: Int = 100) = ure {
    1 of lookBehind(positive = false) {
        1 of BOL
        1 of ir("/\\*")
        0..maxSpacesBehind of space // Can not use MAX - java look-behind implementation complains (throws)
    }
    1 of area
    1 of lookAhead(positive = false) {
        0..MAX of space
        1 of ir("\\*/")
        1 of EOL
    }
}

fun ureCommentedOutArea(area: Ure) = ure {
    1 of {
        1 of BOL
        1 of ir("/\\*")
        0..MAX of space
    }
    1 of area
    1 of {
        0..MAX of space
        1 of ir("\\*/")
        1 of EOL
    }
}

