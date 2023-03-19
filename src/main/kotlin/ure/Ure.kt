package pl.mareklangiewicz.ure

import kotlin.reflect.*
import kotlin.text.RegexOption.*

/**
 * Multiplatform Kotlin Frontend / DSL for regular expressions. Actual regular expressions are used like IR
 * (intermediate representation) just to compile it to standard kotlin.text.Regex,
 * but developer is using nice DSL to build regular expressions instead of writing them by hand.
 *
 * Reference links to RE engines/backends docs, etc:
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/
 * https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 * https://docs.oracle.com/javase/tutorial/essential/regex/quant.html
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp
 * https://www.w3schools.com/jsref/jsref_obj_regexp.asp
 * https://regexr.com/
 */

/**
 * Here UreIR is the traditional regular expression - no human should read - kind of "intermediate representation"
 */
typealias UreIR = String
// TODO_later: change to value class (when we have new kotlin in gradle scripts)

fun ure(vararg opts: RegexOption, init: UreProduct.() -> Unit) = ure(enable = opts.toSet(), disable = emptySet(), init)

// TODO_later: maybe remove options here? and always use Ure.withOptions? (check in practice first)
fun ure(enable: Set<RegexOption> = emptySet(), disable: Set<RegexOption> = emptySet(), init: UreProduct.() -> Unit) =
    if (enable.isEmpty() && disable.isEmpty()) UreProduct(init)
    else UreProduct(init).withOptions(enable, disable)

@Deprecated("Use Ure.withOptions")
fun ureWithOptions(content: Ure, enable: Set<RegexOption> = emptySet(), disable: Set<RegexOption> = emptySet()) =
    UreChangeOptionsGroup(content, enable, disable)

// TODO_later: maybe remove name and opts here? and always use Ure.with...? (check in practice first)
fun ure(name: String, vararg opts: RegexOption, init: UreProduct.() -> Unit) =
    if (opts.isEmpty()) UreProduct(init).withName(name)
    else UreProduct(init).withOptionsEnabled(*opts).withName(name)

@Deprecated("Use Ure.withName")
fun ureWithName(name: String, content: Ure) = UreNamedGroup(content, name)

fun Ure.withName(name: String?) = if (name == null) this else UreNamedGroup(this, name)
fun Ure.withOptions(enable: Set<RegexOption> = emptySet(), disable: Set<RegexOption> = emptySet()) =
    UreChangeOptionsGroup(this, enable, disable)

fun Ure.withOptionsEnabled(vararg options: RegexOption) = withOptions(enable = options.toSet())
fun Ure.withOptionsDisabled(vararg options: RegexOption) = withOptions(disable = options.toSet())

fun Ure.withWordBoundaries(boundaryBefore: Boolean = true, boundaryAfter: Boolean = true) =
    if (!boundaryBefore && !boundaryAfter) this else ure {
        if (boundaryBefore) 1 of bchWord
        1 of this@withWordBoundaries // it should flatten if this is UreProduct (see UreProduct.toIR()) TODO_later: doublecheck
        if (boundaryAfter) 1 of bchWord
    }

sealed class Ure {

    abstract fun toIR(): UreIR

    /**
     * Optionally wraps in non-capturing group before generating IR, so it's safe to use with quantifiers, unions, etc
     * Wrapping is done only when needed. For example concatenation(product) with more than one element is wrapped.
     * (UreProduct with 0 elements also is wrapped - so f. e. external UreQuantifier only catches empty product)
     */
    abstract fun toClosedIR(): UreIR

    /**
     * It sets MULTILINE by default.
     * Also I decided NOT to use DO_MATCHES_ALL by default. Lets keep "any" as single line matcher.
     * Lets use explicit ureAnyLine and/or ureWhateva utils instead of changing "any" meaning freely.
     * Lets assume in all normal val/fun ureSth.. that DOT_MATCHES_ALL is disabled
     * (so we don't enable/disable it all the time "just to make sure")
     */
    fun compile(vararg options: RegexOption) = compileMultiLine(*options)

    fun compileMultiLine(vararg options: RegexOption) = Regex(toIR(), setOf(MULTILINE) + options)

    fun compileSingleLine(vararg options: RegexOption) = Regex(toIR(), options.toSet())
        .also { check(MULTILINE !in options) }

    // TODO_later: experiment with dropping U (Micro) prefix in classes nested in Ure when I have some examples working.
    // I'm leaving it for now to have more unique names and less clashes, but design is not final.
}

infix fun Ure.or(that: Ure) = UreUnion(this, that)
infix fun Ure.then(that: Ure) = UreProduct(mutableListOf(this, that))
// Do not rename "then" to "and". The "and" is more like special lookahead/lookbehind group

data class UreProduct(val product: MutableList<Ure> = mutableListOf()) : Ure() {
    constructor(init: UreProduct.() -> Unit) : this() {
        init()
    }

    override fun toIR(): UreIR = when (product.size) {
        0 -> ""
        1 -> product[0].toIR()
        else -> product.joinToString(separator = "") { if (it is UreProduct) it.toIR() else it.toClosedIR() }
    }

    override fun toClosedIR() = when (product.size) {
        1 -> product[0].toClosedIR()
        else -> ncapt(this).toIR() // in 0 case we also want ncapt!
        // To avoid issues when outside operator captures something else instead of empty product.
        // I decided NOT to throw IllegalStateError in 0 case so we can always monitor IR in half-baked UREs.
        // (like when creating UREs with some compose UI)
    }

    class UreX(val times: IntRange, val reluctant: Boolean, val possessive: Boolean)

    fun x(times: IntRange, reluctant: Boolean = false, possessive: Boolean = false) = UreX(times, reluctant, possessive)
    fun x(times: Int) = x(times..times)
    infix fun UreX.of(ure: Ure) {
        product.add(quantify(ure, times, reluctant, possessive))
    }

    infix fun UreX.of(init: UreProduct.() -> Unit) {
        product.add(quantify(times, reluctant, possessive, init))
    }

    infix fun IntRange.of(ure: Ure) = x(this) of ure
    infix fun Int.of(ure: Ure) = x(this) of ure
    infix fun IntRange.of(init: UreProduct.() -> Unit) = x(this) of init
    infix fun Int.of(init: UreProduct.() -> Unit) = x(this) of init
}

data class UreUnion(val first: Ure, val second: Ure) : Ure() {
    override fun toIR() = first.toClosedIR() + "|" + second.toClosedIR()
    override fun toClosedIR() = ncapt(this).toIR()
}

abstract class UreGroup : Ure() {
    abstract val content: Ure
    private val contentIR get() = content.toIR()
    protected abstract val typeIR: String

    override fun toIR(): UreIR = "($typeIR$contentIR)"
    // looks like all possible typeIR prefixes can not be confused with first contentIR characters.
    // (meaning: RE designers thought about it, so I don't have to be extra careful here)

    override fun toClosedIR() = toIR() // group is always "closed" - has parentheses outside
}

data class UreNamedGroup(override val content: Ure, val name: String) : UreGroup() {
    override val typeIR get() = "?<$name>"
}

data class UreNonCaptGroup(override val content: Ure) : UreGroup() {
    override val typeIR get() = "?:"
}

data class UreCaptGroup(override val content: Ure) : UreGroup() {
    override val typeIR get() = ""
}

// TODO: test it!
data class UreChangeOptionsGroup(
    override val content: Ure,
    val enable: Set<RegexOption> = emptySet(),
    val disable: Set<RegexOption> = emptySet(),
) : UreGroup() {
    init {
        require((enable intersect disable).isEmpty()) { "Can not enable and disable the same option at the same time" }
        require(enable.isNotEmpty() || disable.isNotEmpty()) { "No options provided" }
    }

    override val typeIR get() = "?${enable.ir}-${disable.ir}:" // TODO_later: check if either set can be empty

    // TODO_later: review flags we support - but probably want to be multiplatform??
    private val RegexOption.code
        get() = when (this) {
            IGNORE_CASE -> "i"
            MULTILINE -> "m"
            else -> error("Only multiplatform regex options are supported.")
//            LITERAL -> TODO()
//            UNIX_LINES -> "d"
//            COMMENTS -> "x" // but not really supported... maybe in UreRawIR, but I wouldn't use it
//            DOT_MATCHES_ALL -> "s" // s means - treat all as a single line (so dot matches terminators too)
//            CANON_EQ -> TODO()
//            // TODO_someday "u" is not supported (unicode case)
        }

    private val Set<RegexOption>.ir get() = joinToString("") { it.code }
}
// TODO_someday: there are also similar "groups" without content (see Pattern.java), add support for it (content nullable?)


data class UreLookGroup(override val content: Ure, val ahead: Boolean = true, val positive: Boolean = true) : UreGroup() {
    override val typeIR
        get() = when (ahead to positive) {
            true to true -> "?="
            true to false -> "?!"
            false to true -> "?<="
            false to false -> "?<!"
            else -> error("Impossible case")
        }
}


// TODO_someday: "independent" non-capturing group - what is that? (see Pattern.java)

data class UreGroupRef(val nr: Int? = null, val name: String? = null) : Ure() {
    init {
        nr == null || name == null || error("Can not reference capturing group by both nr ($nr) and name ($name)")
        nr == null && name == null && error("Either nr or name has to be provided for the group reference")
    }

    override fun toIR(): UreIR = if (nr != null) "\\$nr" else "\\k<$name>"
    override fun toClosedIR(): UreIR = toIR()
}

const val MAX = Int.MAX_VALUE

/**
 * By default it's "greedy" - tries to match as many "times" as possible. But backs off one by one if it would fail.
 * @param reluctant - Tries to eat as little "times" as possible. Opposite to default "greedy" behavior.
 * @param possessive - It's like more greedy than default greedy. Never backs off - fails instead.
 */
data class UreQuantifier(
    val content: Ure,
    val times: IntRange,
    val reluctant: Boolean = false,
    val possessive: Boolean = false,
) : Ure() {
    init {
        reluctant && possessive && error("Quantifier can't be reluctant and possessive at the same time")
    }

    val greedy get() = !reluctant && !possessive
    override fun toIR(): UreIR {
        val timesIR = when (times) {
            1..1 -> return content.toIR()
            0..1 -> "?"
            0..MAX -> "*"
            1..MAX -> "+"
            else -> when (times.last) {
                times.first -> "{${times.first}}"
                MAX -> "{${times.first},}"
                else -> "{${times.first},${times.last}}"
            }
        }
        val suffixIR = when {
            reluctant -> "?"
            possessive -> "+"
            else -> ""
        }
        return content.toClosedIR() + timesIR + suffixIR
    }
    //    override fun toClosedIR(): UreIR = ncapt(this).toIR()
    override fun toClosedIR() = toIR()
    // TODO_later: I think it's correct, but should be analyzed more carefully (and write tests!).
}

data class UreChar(val ir: UreIR) : Ure() {
    // TODO_later: separate sealed class for specials etc. We should never ask user to manually provide UreIR

    override fun toIR(): UreIR = ir
    override fun toClosedIR(): UreIR = if (isClosed) ir else ncapt(this).toIR()
    private val isClosed = when {
        ir.length == 1 -> true
        ir.length == 2 && ir[0] == '\\' -> true
        else -> false
    }
    // TODO_someday: analyze more carefully and drop grouping when actually not needed.
}

// TODO_later: can I do something like: chars: Set<UreChar> ??
data class UreCharSet(val chars: Set<UreIR>, val positive: Boolean = true) : Ure() {
    override fun toIR(): UreIR = chars.joinToString("", if (positive) "[" else "[^", "]")
    override fun toClosedIR(): UreIR = toIR()
}

// TODO_later: more complicated combinations of char classes
// TODO_later: analyze if some special kotlin progression/range would fit here better
data class UreCharRange(val from: UreIR, val to: UreIR, val positive: Boolean = true) : Ure() {
    private val neg = if (positive) "" else "^"
    override fun toIR(): UreIR = "[$neg$from-$to]"
    override fun toClosedIR(): UreIR = toIR()
}

data class UreRawIR(val ir: UreIR) : Ure() {
    // TODO_later: this is a dirty way to inject whole strings fast. TODO_later: think what would be better.
    // maybe still ask user for string, but validate and transform to actual UreProduct of UreChar's

    override fun toIR(): UreIR = ir
    override fun toClosedIR(): UreIR = ncapt(this).toIR()
}

data class UreQuote(val string: String) : Ure() {
    override fun toIR() = "\\Q$string\\E"
    override fun toClosedIR(): UreIR = toIR()
}

operator fun Ure.not(): Ure = when (this) {
    is UreChar -> when (this) {
        chWord -> chNonWord
        chNonWord -> chWord
        chDigit -> chNonDigit
        chNonDigit -> chDigit
        chSpace -> chNonSpace
        chNonSpace -> chSpace
        bchWord -> bchWordNot
        bchWordNot -> bchWord
        else -> oneCharNotOf(ir)
        // TODO: check if particular ir is appropriate for such wrapping
        // TODO_later: other special cases?
    }

    is UreCharRange -> UreCharRange(from, to, !positive)
    is UreCharSet -> UreCharSet(chars, !positive)
    is UreGroup -> when (this) {
        is UreLookGroup -> UreLookGroup(content, ahead, !positive)
        else -> error("Unsupported UreGroup for negation: ${this::class.simpleName}")
    }

    is UreGroupRef -> error("UreGroupRef can not be negated")
    is UreProduct -> error("UreProduct can not be negated")
    is UreQuantifier -> error("UreQuantifier can not be negated")
    is UreQuote -> error("UreQuote can not be negated")
    is UreRawIR -> error("UreRawIR can not be negated")
    is UreUnion -> error("UreUnion can not be negated")
    else -> error("Unexpected Ure type: ${this::class.simpleName}")
    // had to add "else" branch because Android Studio 2021.2.1 canary 7 complains..
    // TODO_later: Remove "else" when newer AS stops complaining
}
// TODO_later: experiment more with different operators overloading (after impl some working examples)
//  especially indexed access operators and invoke operators..

fun ch(ir: UreIR) = UreChar(ir)
fun ir(ir: UreIR) = UreRawIR(ir)

val backslash = ch("\\\\")

fun unicode(name: String) = ch("\\N{$name}")


// Ure constants matching one char (special chars; common categories). All names start with ch.
// (turns out it's really more important to have common prefix, than to be shorter)

val chTab = ch("\\t")
val chLF = ch("\\n")
val chCR = ch("\\r")
val chFF = ch("\\f")
val chAlert = ch("\\a")
val chEsc = ch("\\e")

val chDot = ch("\\.")
val chAny = ch(".")
val chAnyMultiLine = ch("(?s:.)")

val chDigit = ch("\\d")
val chNonDigit = ch("\\D")
val chSpace = ch("\\s")
val chSpaceInLine = ch(" ") or chTab
val chNonSpace = ch("\\S")

/** [a-zA-Z_0-9] */
val chWord = ch("\\w")
val chNonWord = ch("\\W")
val chWordOrHyphen = ch("\\W") or ch("-") // also points out (when typing chWo..) that normal chWord doesn't match hyphen.

val chaz = oneCharOfRange("a", "z")
val chAZ = oneCharOfRange("A", "Z")
val chazAZ = chaz or chAZ

val chPosixLower = ch("\\p{Lower}")
val chPosixUpper = ch("\\p{Upper}")
val chPosixAlpha = ch("\\p{Alpha}")
val chPosixDigit = ch("\\p{Digit}")
val chPosixAlnum = ch("\\p{Alnum}")
val chPosixPunct = ch("\\p{Punct}")
val chPosixPrint = ch("\\p{Print}")
val chPosixBlank = ch("\\p{Blank}")
val chPosixCntrl = ch("\\p{Cntrl}")
val chPosixXDigit = ch("\\p{XDigit}")
val chPosixSpace = ch("\\p{Space}")


// boundaries (b...)

val bBOL = ir("^")
val bEOL = ir("$")
val bBOInput = ir("\\A")
val bEOInput = ir("\\z")
val bEOPreviousMatch = ir("\\G")

val bchWord = ir("\\b")
val bchWordNot = ir("\\B") // calling it "non-word boundary" is wrong. it's more like negation of bchWord

fun control(x: String) = ch("\\c$x") // FIXME_later: what exactly is this?? (see std Pattern.java)
fun oneCharOf(vararg chars: UreIR) = UreCharSet(chars.toSet()) // TODO_later: Use UreChar as vararg type
fun oneCharNotOf(vararg chars: UreIR) = UreCharSet(chars.toSet(), positive = false) // TODO_later: jw
fun oneCharOfRange(from: UreIR, to: UreIR) = UreCharRange(from, to)
fun oneCharNotOfRange(from: UreIR, to: UreIR) = UreCharRange(from, to, positive = false)

fun capt(content: Ure) = UreCaptGroup(content)
fun capt(init: UreProduct.() -> Unit) = capt(UreProduct(init))
fun ncapt(content: Ure) = UreNonCaptGroup(content)
fun ncapt(init: UreProduct.() -> Unit) = ncapt(UreProduct(init))
fun lookAhead(content: Ure, positive: Boolean = true) = UreLookGroup(content, true, positive)
fun lookAhead(positive: Boolean = true, init: UreProduct.() -> Unit) = lookAhead(UreProduct(init), positive)
fun lookBehind(content: Ure, positive: Boolean = true) = UreLookGroup(content, false, positive)
fun lookBehind(positive: Boolean = true, init: UreProduct.() -> Unit) = lookBehind(UreProduct(init), positive)


/**
 * By default it's "greedy" - tries to match as many "times" as possible. But backs off one by one if it would fail.
 * @param reluctant - Tries to eat as little "times" as possible. Opposite to default "greedy" behavior.
 * @param possessive - It's like more greedy than default greedy. Never backs off - fails instead.
 */
fun quantify(
    content: Ure,
    times: IntRange,
    reluctant: Boolean = false,
    possessive: Boolean = false,
) = if (times == 1..1) content else UreQuantifier(content, times, reluctant, possessive)

/**
 * By default it's "greedy" - tries to match as many "times" as possible. But backs off one by one if it would fail.
 * @param reluctant - Tries to eat as little "times" as possible. Opposite to default "greedy" behavior.
 * @param possessive - It's like more greedy than default greedy. Never backs off - fails instead.
 */
fun quantify(
    times: IntRange,
    reluctant: Boolean = false,
    possessive: Boolean = false,
    init: UreProduct.() -> Unit,
) = quantify(UreProduct(init), times, reluctant, possessive)

fun ref(nr: Int? = null, name: String? = null) = UreGroupRef(nr, name)

fun quote(string: String) = UreQuote(string)

fun CharSequence.replace(ure: Ure, transform: (MatchResult) -> CharSequence) = ure.compile().replace(this, transform)
fun CharSequence.replace(ure: Ure, replacement: String): String = ure.compile().replace(this, replacement)
fun CharSequence.replaceFirst(ure: Ure, replacement: String): String = ure.compile().replaceFirst(this, replacement)
fun CharSequence.findAll(ure: Ure, startIndex: Int = 0) = ure.compile().findAll(this, startIndex)
fun CharSequence.find(ure: Ure, startIndex: Int = 0) = ure.compile().find(this, startIndex)
fun CharSequence.matchEntire(ure: Ure) = ure.compile().matchEntire(this)

@Deprecated("Explicit groups is better") // experiment more before removing (or not) (at least should not !! but be nullable)
operator fun MatchResult.get(name: String) = named[name]!!.value

@Deprecated("Explicit named is better") // experiment more before removing (or not)
operator fun MatchResult.getValue(thisObj: Any?, property: KProperty<*>) = get(property.name)

operator fun MatchNamedGroupCollection.getValue(thisObj: Any?, property: KProperty<*>) = get(property.name)?.value

// FIXME_someday: this is hack, but I can't reliably get named groups from MatchResult (at least in multiplatform)
// TRACK: https://youtrack.jetbrains.com/issue/KT-51908
// see also:
//    https://youtrack.jetbrains.com/issue/KT-41890
//    https://youtrack.jetbrains.com/issue/KT-29241/Unable-to-use-named-Regex-groups-on-JDK-11
//    https://youtrack.jetbrains.com/issue/KT-20865/Retrieving-groups-by-name-is-not-supported-on-Java-9-even-with-kotlin-stdlib-jre8-in-the-classpath
//    https://github.com/JetBrains/kotlin/commit/9c4c1ed557a889bf57c754b81f4897a0d8405b0d
val MatchResult.named get() = groups as? MatchNamedGroupCollection
    ?: throw UnsupportedOperationException("Retrieving groups by name is not supported on this platform.")
