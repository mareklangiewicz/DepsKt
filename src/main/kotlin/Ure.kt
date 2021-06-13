/**
 * Multiplatform Kotlin Frontend / DSL for regular expressions. Actual regular expressions are used like IR
 * (intermediate representation) just to compile it to standard kotlin.text.Regex,
 * but developer is using nice DSL to build regular expressions instead of writing them by hand.
 *
 * Reference links to RE engines/backends docs, etc:
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/
 * https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp
 * https://www.w3schools.com/jsref/jsref_obj_regexp.asp
 * https://regexr.com/
 */

/**
 * Here UreIR is the traditional regular expression - no human should read - kind of "intermediate representation"
 */
typealias UreIR = String
// TODO_later: change to value class (when we have new kotlin in gradle scripts)

fun ure(init: UreProduct.() -> Unit) = UreProduct(init)


sealed class Ure {

    abstract fun toIR(): UreIR

    /**
     * Optionally wraps in non-capturing group before generating IR, so it's safe to use with quantifiers, unions, etc
     * Wrapping is done only when needed. For example concatenation(product) with more than one element is wrapped.
     * TODO: think more (and test!) cases when product list is empty! (and we apply some quantifiers etc)
     * UPDATE: I guess it's enough to obey invariant that ClosedIR can never be empty.
     */
    abstract fun toClosedIR(): UreIR

    fun compile(vararg options: RegexOption) = Regex(toIR(), options.toSet())

    // TODO_later: experiment with dropping U (Micro) prefix in classes nested in URegEx when I have some examples working.
    // I'm leaving it for now to have more unique names and less clashes, but design is not final.
}

infix fun Ure.or(that: Ure) = UreUnion(this, that)
infix fun Ure.and(that: Ure) = UreProduct(mutableListOf(this, that))

data class UreProduct(val product: MutableList<Ure> = mutableListOf()): Ure() {
    constructor(init: UreProduct.() -> Unit) : this() { init() }
    override fun toIR() = product.joinToString(separator = "") { it.toIR() }
    override fun toClosedIR() = when (product.size) {
        1 -> product[0].toClosedIR()
        else -> group(this).toIR()
    }
    class UreX(val times: IntRange, val reluctant: Boolean, val possessive: Boolean)
    fun x(times: IntRange, reluctant: Boolean = false, possessive: Boolean = false) = UreX(times, reluctant, possessive)
    fun x(times: Int) = x(times..times)
    infix fun UreX.of(ure: Ure) { product.add(quantify(ure, times, reluctant, possessive)) }
    infix fun UreX.of(init: UreProduct.() -> Unit) { product.add(quantify(times, reluctant, possessive, init)) }
    infix fun IntRange.of(ure: Ure) = x(this) of ure
    infix fun Int.of(ure: Ure) = x(this) of ure
    infix fun IntRange.of(init: UreProduct.() -> Unit) = x(this) of init
    infix fun Int.of(init: UreProduct.() -> Unit) = x(this) of init
}

data class UreUnion(val first: Ure, val second: Ure): Ure() {
    override fun toIR() = first.toClosedIR() + "|" + second.toClosedIR()
    override fun toClosedIR() = group(this).toIR()
}

data class UreGroup(val content: Ure, val name: String? = null, val capture: Boolean = true): Ure() {
    init { capture || name == null || error("Named group has to be capturing") }
    override fun toIR(): UreIR {
        val contentIR = content.toIR()
        val typeIR = if (!capture) "?:" else if (name != null) "?<$name>" else ""
        return "($typeIR$contentIR)"
    }
    override fun toClosedIR() = toIR()
}

// TODO_later: other "Special constructs" like lookaheads, lookbehinds, groups changing flags, etc

data class UreGroupRef(val nr: Int? = null, val name: String? = null): Ure() {
    init {
        nr == null || name == null || error("Can not reference capturing group by both nr ($nr) and name ($name)")
        nr == null && name == null && error("Either nr or name has to be provided for the group reference")
    }

    override fun toIR(): UreIR = if (nr != null) "\\$nr" else "\\k<$name>"
    override fun toClosedIR(): UreIR = toIR()
}

const val MAX = Int.MAX_VALUE

data class UreQuantifier(
    val content: Ure,
    val times: IntRange,
    val reluctant: Boolean = false,
    val possessive: Boolean = false,
): Ure() {
    init { reluctant && possessive && error("Quantifier can't be reluctant and possessive at the same time") }
    val greedy get() = !reluctant && !possessive
    override fun toIR(): UreIR {
        val timesIR = when(times) {
            1..1 -> return content.toIR()
            0..1 -> "?"
            0..MAX -> "*"
            1..MAX -> "+"
            else -> when(times.last) {
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
    override fun toClosedIR(): UreIR = group(this).toIR()
}

data class UreChar(val ir: UreIR) : Ure() {
    // TODO_later: separate sealed class for specials etc.. We should never ask user to manually provide UreIR

    override fun toIR(): UreIR = ir
    override fun toClosedIR(): UreIR = group(this).toIR()
    // Maybe grouping here is not strictly needed, but I'll leave it for now
    // TODO_someday: analyze carefully and maybe drop grouping if not needed.
}

// TODO_later: can I do something like: chars: Set<UChar> ??
data class UreCharSet(val chars: Set<UreIR>, val positive: Boolean = true) : Ure() {
    override fun toIR(): UreIR = chars.joinToString("", if (positive) "[" else "[^", "]")
    override fun toClosedIR(): UreIR = toIR()
}

// TODO_later: more complicated combinations of char classes
// TODO_later: analyze if some special kotlin progression/range would fit here better
data class UreCharRange(val from: UreIR, val to: UreIR) : Ure() {
    override fun toIR(): UreIR = "[$from-$to]"
    override fun toClosedIR(): UreIR = toIR()
}

data class UreQuote(val string: String): Ure() {
    override fun toIR() = "\\Q$string\\E"
    override fun toClosedIR(): UreIR = toIR()
}
// TODO: implement operator a.not() (!) with big pattern matching and negating more obvious stuff like
//  !nonDigit == digit; !digit == nonDigit; !word == nonWord; !UCharSet(.., true) == UCharSet(.., false), etc
// TODO_later: experiment more with different operators overloading (after impl some working examples)
//  especially indexed access operators and invoke operators..

fun ch(ir: UreIR) = UreChar(ir)

val backslash = ch("\\\\")

fun unicode(name: String) = ch("\\N{$name}")
val tab = ch("\\t")
val lf = ch("\\n")
val cr = ch("\\r")
val ff = ch("\\f")
val alert = ch("\\a")
val esc = ch("\\e")

val any = ch(".")

val digit = ch("\\d")
val nonDigit = ch("\\D")
val space = ch("\\s")
val nonSpace = ch("\\S")
val word = ch("\\w")
val nonWord = ch("\\W")

val posixLower = ch("\\p{Lower}")
val posixUpper = ch("\\p{Upper}")
val posixAlpha = ch("\\p{Alpha}")
val posixDigit = ch("\\p{Digit}")
val posixAlnum = ch("\\p{Alnum}")
val posixPunct = ch("\\p{Punct}")
val posixPrint = ch("\\p{Print}")
val posixBlank = ch("\\p{Blank}")
val posixCntrl = ch("\\p{Cntrl}")
val posixXDigit = ch("\\p{XDigit}")
val posixSpace = ch("\\p{Space}")

val BOL = ch("^")
val EOL = ch("$")
val BOInput = ch("\\A")
val EOInput = ch("\\z")
val EOPreviousMatch = ch("\\G")

val wordBoundary = ch("\\b")
val nonWordBoundary = ch("\\B")

fun control(x: String) = ch("\\c$x") // FIXME_later: what exactly is this?? (see std Pattern.java)
fun oneCharOf(vararg chars: UreIR) = UreCharSet(chars.toSet()) // TODO_later: Use UreChar
fun oneCharNotOf(vararg chars: UreIR) = UreCharSet(chars.toSet(), positive = false) // TODO_later: jw
fun oneCharOfRange(from: UreIR, to: UreIR) = UreCharRange(from, to)

fun group(content: Ure, name: String? = null, capture: Boolean = true) = UreGroup(content, name, capture)
fun group(name: String? = null, capture: Boolean = true, init: UreProduct.() -> Unit) =
    group(UreProduct(init), name, capture)

fun quantify(
    content: Ure,
    times: IntRange,
    reluctant: Boolean = false,
    possessive: Boolean = false,
) = if (times == 1..1) content else UreQuantifier(content, times, reluctant, possessive)

fun quantify(
    times: IntRange,
    reluctant: Boolean = false,
    possessive: Boolean = false,
    init: UreProduct.() -> Unit,
) = quantify(UreProduct(init), times, reluctant, possessive)

fun ref(nr: Int? = null, name: String? = null) = UreGroupRef(nr, name)

fun quote(string: String) = UreQuote(string)




fun commentOutActualKeywordInAllFun(input: String): String {
//    regex()
//        .startOfLine()
//        .then("actual")
//        .space()
//        .then("fun")
//        .build()
//
    TODO()
}

