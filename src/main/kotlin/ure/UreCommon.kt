package pl.mareklangiewicz.ure

import kotlin.text.RegexOption.*

fun ureIdent(first: Ure = azAZ) = ure {
    1 of first
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

fun ureWhatevaInLine(reluctant: Boolean = true) = ureWhateva(reluctant, inLine = true)

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

fun ureLineWithContentFragments(vararg contentFragment: Ure, withOptCR: Boolean = true, withOptLF: Boolean = true) = ure {
    1 of ureBlankStartOfLine()
    1 of ureWhateva(inLine = true)
    for (fragment in contentFragment) {
        1 of fragment
        1 of ureWhateva(inLine = true)
    }
    1 of ureBlankRestOfLine(withOptCR, withOptLF)
}

fun ureAnyLine(withOptCR: Boolean = true, withOptLF: Boolean = true) =
    ureLineWithContent(ureWhateva(inLine = true), withOptCR, withOptLF)

fun Ure.withOptSpacesAround(inLine: Boolean = false) = ure {
    val s = if (inLine) spaceInLine else space
    0..MAX of s
    1 of this@withOptSpacesAround
    0..MAX of s
}

fun Ure.withOptSpacesAroundInLine() = withOptSpacesAround(inLine = true)

fun Ure.withOptWhatevaAround(reluctant: Boolean = true, inLine: Boolean = false) =
    ureWhateva(reluctant, inLine) then this then ureWhateva(reluctant, inLine)

fun Ure.withOptWhatevaAroundInLine(reluctant: Boolean = true) =
    withOptWhatevaAround(reluctant, inLine = true)

fun Ure.commentedOut(inLine: Boolean = false, traditional: Boolean = true, kdoc: Boolean = false) = ure {
    require(inLine || traditional) { "Non traditional comments are only single line" }
    require(!kdoc || traditional) { "Non traditional comments can't be used as kdoc" }
    1 of when {
        kdoc -> ir("/\\**")
        traditional -> ir("/\\*")
        else -> ir("//")
    }
    1 of this@commentedOut.withOptSpacesAround(inLine)
    if (traditional) 1 of ir("\\*/")
}

fun Ure.notCommentedOut(traditional: Boolean = true, maxSpacesBehind: Int = 100) = ure {
    1 of lookBehind(positive = false) {
        1 of if (traditional) ir("/\\*") else ir("//")
        0..maxSpacesBehind of if (traditional) space else spaceInLine
        // Can not use MAX - java look-behind implementation complains (throws)
    }
    1 of this@notCommentedOut
    if (traditional) 1 of lookAhead(positive = false) {
        0..MAX of space
        1 of ir("\\*/")
    }
}

fun ureCommentLine(content: Ure = ureWhateva(inLine = true), traditional: Boolean = true, kdoc: Boolean = false) =
    ureLineWithContent(content.commentedOut(inLine = true, traditional, kdoc))

fun ureLineWithEndingComment(comment: Ure) =
    ureLineWithContent(ureWhateva(inLine = true) then comment.commentedOut(inLine = true, traditional = false))


fun ureRegion(content: Ure, regionName: Ure? = null) = ure {
    1 of ureCommentLine(ureKeywordAndOptArg(ir("region"), regionName), traditional = false)
    1 of content
    1 of ureCommentLine(ureKeywordAndOptArg(ir("endregion"), regionName), traditional = false)
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

