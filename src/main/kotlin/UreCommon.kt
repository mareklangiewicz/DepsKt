package pl.mareklangiewicz.ure

import kotlin.text.RegexOption.*

val ureIdent = ure {
    1 of azAZ
    0..MAX of word
}

fun ureChain(
    element: Ure,
    separator: Ure = spaceInLine,
    times: IntRange = 1..MAX,
    reluctant: Boolean = false,
    possessive: Boolean = false,
): Ure = when (times.first) {
    0 -> ure { 0..1 of ureChain(element, separator, 1..times.last, reluctant, possessive) }
    else -> ure {
        1 of element
        val last = if (times.last == MAX) MAX else times.last - 1
        x(0..last, reluctant, possessive) of ure {
            1 of separator
            1 of element
        }
    }
}

fun ureWhateva(reluctant: Boolean = true, inLine: Boolean = false) =
    ure { x(0..MAX, reluctant = reluctant) of any }.run {
        if (inLine) withOptionsDisabled(DOT_MATCHES_ALL)
        else withOptionsEnabled(DOT_MATCHES_ALL)
    }

fun ureBlankStartOfLine() = ure {
    1 of BOL
    0..MAX of spaceInLine
}

fun ureBlankRestOfLine(withOptCR: Boolean = true, withOptLF: Boolean = true) = ure {
    0..MAX of spaceInLine
    1 of EOL
    if (withOptCR) 0..1 of cr
    if (withOptLF) 0..1 of lf
}

fun ureLineWithContent(content: Ure, withOptCR: Boolean = true, withOptLF: Boolean = true) = ure {
    1 of ureBlankStartOfLine()
    1 of content
    1 of ureBlankRestOfLine(withOptCR, withOptLF)
}

fun ureAnyLine(withOptCR: Boolean = true, withOptLF: Boolean = true) =
    ureLineWithContent(ureWhateva(inLine = true), withOptCR, withOptLF)

fun Ure.withOptSpacesAround() = ure {
    0..MAX of spaceInLine
    1 of this@withOptSpacesAround
    0..MAX of spaceInLine
}

fun ureCommentLine(content: Ure = ureWhateva(inLine = true), traditional: Boolean = false) =
    ureLineWithContent(ure {
        1 of if (traditional) ir("/\\*") else ir("//")
        1 of content.withOptSpacesAround()
        if (traditional) 1 of ir("\\*/")
    })

fun ureRegion(content: Ure, regionName: Ure? = null) = ure {
    1 of ureCommentLine(ureKeywordAndOptArg(ir("region"), regionName))
    1 of content
    1 of ureCommentLine(ureKeywordAndOptArg(ir("endregion"), regionName))
}

fun ureKeywordAndOptArg(
    keyword: Ure,
    arg: Ure? = null,
    separator: Ure = ure { 1..MAX of spaceInLine },
) = ure {
    1 of keyword
    arg?.let {
        1 of separator
        1 of it
    }
}


fun ureCommentedOutArea(area: Ure = ureWhateva()) = ure {
    1 of ureLineWithContent(ir("/\\*"))
    1 of area
    1 of ureLineWithContent(ir("\\*/"))
}

fun ureNotCommentedOutArea(area: Ure = ureWhateva(reluctant = false), maxSpacesBehind: Int = 100) = ure {
    1 of lookBehind(positive = false) {
        1 of ir("/\\*")
        0..maxSpacesBehind of space // Can not use MAX - java look-behind implementation complains (throws)
    }
    1 of area
    1 of lookAhead(positive = false) {
        0..MAX of space
        1 of ir("\\*/")
    }
}

