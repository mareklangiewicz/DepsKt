package pl.mareklangiewicz.micro.collections

interface MicroList<out T> : Iterable<T> {
    operator fun get(idx: Int): T

    val size: Int
    override operator fun iterator(): Iterator<T> = MicroIterator(this)
    operator fun contains(item: Any?): Boolean = indexOf(item) >= 0
}

interface MicroMutableList<T> : MicroList<T> {
    operator fun set(idx: Int, item: T)
}

class MicroIterator<out T>(private val list: MicroList<T>, private var idx: Int = 0) : Iterator<T> {
    override fun hasNext(): Boolean = idx < list.size
    override fun next(): T = list[idx++]
}

open class MicroListSlice<out T, ListT: MicroList<T>>(val src: ListT, astart: Int, astop: Int) : MicroList<T> {

    val stop : Int = astop.pos(src.size).chk(0, src.size)
    val start: Int = astart.pos(src.size).chk(0, stop)
    override val size: Int = stop - start

    override fun get(idx: Int) = src[start + idx.pos(size).chk(0, size-1)]
    override operator fun iterator(): Iterator<T> = MicroIterator(this)
}

class MicroMutableListSlice<T>(asrc: MicroMutableList<T>, astart: Int, astop: Int) : MicroMutableList<T>, MicroListSlice<T, MicroMutableList<T>>(asrc, astart, astop) {
    override fun set(idx: Int, item: T) { src[start + idx.pos(size).chk(0, size-1)] = item }
}

operator fun <T> MicroList<T>.get(start: Int, stop: Int) = MicroListSlice(this, start, stop) // TODO LATER: test it
operator fun <T> MicroMutableList<T>.get(start: Int, stop: Int) = MicroMutableListSlice(this, start, stop) // TODO LATER: test it


open class MicroListConcat<out T, ListT: MicroList<T>>(val first: ListT, val second: ListT): MicroList<T> {
    override val size get() = first.size + second.size
    override fun get(idx: Int) = if (idx < first.size) first[idx] else second[idx - first.size]
}

class MicroMutableListConcat<T>(afirst: MicroMutableList<T>, asecond: MicroMutableList<T>): MicroMutableList<T>, MicroListConcat<T, MicroMutableList<T>>(afirst, asecond) {
    override fun set(idx: Int, item: T) = if (idx < first.size) first[idx] =  item else second[idx - first.size] = item
}

operator fun <T> MicroList<T>.plus(other: MicroList<T>) = MicroListConcat(this, other)



internal fun Int.pos(size: Int) = if(this < 0) size + this else this
internal fun Int.chk(min: Int, max: Int) = if(this < min || this > max) throw IndexOutOfBoundsException() else this
