package pl.mareklangiewicz.micro.collections

// TODO: use inline/value classes to wrap standard collections without overhead

fun <T> Array<T>.asMicroList() = asMicroMutableList() as MicroList<T>

fun <T> Array<T>.asMicroMutableList() = object : MicroMutableList<T> {

    override fun get(idx: Int) = this@asMicroMutableList[idx]
    override fun set(idx: Int, item: T) { this@asMicroMutableList[idx] = item }

    override val size: Int get() = this@asMicroMutableList.size
    override fun iterator() = this@asMicroMutableList.iterator()
    override fun contains(item: Any?) = item in this@asMicroMutableList
        // current standard Array.contains impl is same as MicroList.contains, but better to always use original impl.
}

fun <T> List<T>.asMicroList() = object : MicroList<T> {

    override fun get(idx: Int) = this@asMicroList.get(idx)

    override val size: Int get() = this@asMicroList.size
    override fun iterator() = this@asMicroList.iterator()
    override fun contains(item: Any?) = item in this@asMicroList
}

fun <T> MutableList<T>.asMicroMutableList() = object : MicroMutableList<T> {

    override fun get(idx: Int) = this@asMicroMutableList[idx]
    override fun set(idx: Int, item: T) { this@asMicroMutableList[idx] = item }

    override val size: Int get() = this@asMicroMutableList.size
    override fun iterator() = this@asMicroMutableList.iterator()
    override fun contains(item: Any?): Boolean = item in this@asMicroMutableList
}
