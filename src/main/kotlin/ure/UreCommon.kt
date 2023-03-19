package pl.mareklangiewicz.ure

// TODO NOW: where else in standard ure utils I should use default withWordBoundaries??
fun ureIdent(first: Ure = chazAZ, withWordBoundaries: Boolean = true, allowHyphensInside: Boolean = false) = ure {
    1 of first
    0..MAX of chWord
    if (allowHyphensInside) 0..MAX of ure {
        1 of ch("-")
        1..MAX of chWord
    }
}.withWordBoundaries(withWordBoundaries, withWordBoundaries)

fun ureChain(
    element: Ure,
    separator: Ure = chSpaceInLine,
    times: IntRange = 1..MAX,
    reluctant: Boolean = false,
    possessive: Boolean = false,
): Ure = when (times.first) {
    0 ->
        if (times.last <= 0) ir("(?:)") // FIXME later: it should always match 0 chars. can it be totally empty?? be careful
        else ure { x(0..1, reluctant, possessive) of ureChain(element, separator, 1..times.last, reluctant, possessive) }
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
    ure { x(0..MAX, reluctant = reluctant) of if (inLine) chAny else chAnyMultiLine }

fun ureWhatevaInLine(reluctant: Boolean = true) = ureWhateva(reluctant, inLine = true)

fun ureBlankStartOfLine() = ure {
    1 of bBOL
    0..MAX of chSpaceInLine
}

fun ureBlankRestOfLine(withOptCR: Boolean = true, withOptLF: Boolean = true) = ure {
    0..MAX of chSpaceInLine
    1 of bEOL
    if (withOptCR) 0..1 of chCR
    if (withOptLF) 0..1 of chLF
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

fun Ure.withOptSpacesAround(inLine: Boolean = false, allowBefore: Boolean = true, allowAfter: Boolean = true) =
    if (!allowBefore && !allowAfter) this else ure {
        val s = if (inLine) chSpaceInLine else chSpace
        if (allowBefore) 0..MAX of s
        1 of this@withOptSpacesAround // it should flatten if this is UreProduct (see UreProduct.toIR()) TODO_later: doublecheck
        if (allowAfter) 0..MAX of s
    }

fun Ure.withOptSpacesAroundInLine(allowBefore: Boolean = true, allowAfter: Boolean = true) =
    withOptSpacesAround(inLine = true, allowBefore, allowAfter)

fun Ure.withOptWhatevaAround(
    reluctant: Boolean = true,
    inLine: Boolean = false,
    allowBefore: Boolean = true,
    allowAfter: Boolean = true
) = if (!allowBefore && !allowAfter) this else ure {
    if (allowBefore) 1 of ureWhateva(reluctant, inLine)
    1 of this@withOptWhatevaAround // it should flatten if this is UreProduct (see UreProduct.toIR()) TODO_later: doublecheck
    if (allowAfter) 1 of ureWhateva(reluctant, inLine)
}

fun Ure.withOptWhatevaAroundInLine(reluctant: Boolean = true, allowBefore: Boolean = true, allowAfter: Boolean = true) =
    withOptWhatevaAround(reluctant, inLine = true, allowBefore, allowAfter)

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
        0..maxSpacesBehind of if (traditional) chSpace else chSpaceInLine
        // Can not use MAX - java look-behind implementation complains (throws)
    }
    1 of this@notCommentedOut
    if (traditional) 1 of lookAhead(positive = false) {
        0..MAX of chSpace
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
    separator: Ure = ure { 1..MAX of chSpaceInLine },
) = ure {
    1 of keyword
    arg?.let {
        1 of separator
        1 of it
    }
}

