package pl.mareklangiewicz.micro.collections

// region Byte Special Extensions

fun ByteArray.asMicroList() = asMicroMutableList() as MicroList<Byte>

fun ByteArray.asMicroMutableList() = object : MicroMutableList<Byte> {

    override fun get(idx: Int) = this@asMicroMutableList[idx]
    override fun set(idx: Int, item: Byte) { this@asMicroMutableList[idx] = item }

    override val size: Int get() = this@asMicroMutableList.size
    override fun iterator() = this@asMicroMutableList.iterator()
    override fun contains(item: Any?) = item is Byte && item in this@asMicroMutableList
        // current standard Array.contains impl is same as MicroList.contains, but better to always use original impl.
}

// endregion Byte Special Extensions

// region Generated Special Extensions

fun ShortArray.asMicroList() = asMicroMutableList() as MicroList<Short>

fun ShortArray.asMicroMutableList() = object : MicroMutableList<Short> {

    override fun get(idx: Int) = this@asMicroMutableList[idx]
    override fun set(idx: Int, item: Short) { this@asMicroMutableList[idx] = item }

    override val size: Int get() = this@asMicroMutableList.size
    override fun iterator() = this@asMicroMutableList.iterator()
    override fun contains(item: Any?) = item is Short && item in this@asMicroMutableList
        // current standard Array.contains impl is same as MicroList.contains, but better to always use original impl.
}

// endregion Generated Special Extensions
