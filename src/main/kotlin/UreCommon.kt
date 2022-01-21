package pl.mareklangiewicz.ure

import kotlin.text.RegexOption.*

fun ureWhateva(reluctant: Boolean = true) = ure { x(0..MAX, reluctant = reluctant) of any }
    .withOptionsEnabled(DOT_MATCHES_ALL) // Here I explicitly set DOT_MATCHES_ALL as a special case.
        // By default we assume this option is disabled. (see comment at Ure.compile())

fun ureAnyLine(withLF: Boolean = true, withCR: Boolean = false) = ure {
    1 of BOL
    0..MAX of any
    1 of EOL
    if (withCR) 1 of cr
    if (withLF) 1 of lf
}.withOptionsDisabled(DOT_MATCHES_ALL) // Here I explicitly disable DOT_MATCHES_ALL as a special case.
    // Normally we just assume this option is disabled anyway. (see comment at Ure.compile())


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

fun ureBlankStartOfLine() = ure {
    1 of BOL
    0..MAX of space
}

fun ureBlankRestOfLine(withOptCR: Boolean = true, withOptLF: Boolean = true) = ure {
    0..MAX of space
    1 of EOL
    if (withOptCR) 0..1 of cr
    if (withOptLF) 0..1 of lf
}

fun ureSimpleCommentLine(ureContent: Ure) = ure {
    1 of ureBlankStartOfLine()
    1 of ir("//")
    0..MAX of space
    1 of ureContent
    1 of ureBlankRestOfLine()
}

fun ureRegion(content: Ure, regionName: Ure? = null) = ure {
    1 of ureSimpleCommentLine(ureKeywordAndOptArg(ir("region"), regionName))
    1 of content
    1 of ureSimpleCommentLine(ureKeywordAndOptArg(ir("endregion"), regionName))
}

private fun ureKeywordAndOptArg(keyword: Ure, arg: Ure? = null, separator: Ure = space) = ure {
    1 of keyword
    arg?.let {
        1 of separator
        1 of it
    }
}

