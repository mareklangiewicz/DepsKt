package pl.mareklangiewicz.micro.collections

// extensions for ByteArray are used as template to generate extensions for all other types below

// region Byte Special Extensions

fun ByteArray.asMicroList() = asMicroMutableList() as MicroList<Byte>

fun ByteArray.asMicroMutableList() = object : MicroMutableList<Byte> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Byte) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}

// endregion Byte Special Extensions

// region Generated Special Extensions

fun ShortArray.asMicroList() = asMicroMutableList() as MicroList<Short>

fun ShortArray.asMicroMutableList() = object : MicroMutableList<Short> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Short) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun IntArray.asMicroList() = asMicroMutableList() as MicroList<Int>

fun IntArray.asMicroMutableList() = object : MicroMutableList<Int> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Int) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun LongArray.asMicroList() = asMicroMutableList() as MicroList<Long>

fun LongArray.asMicroMutableList() = object : MicroMutableList<Long> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Long) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun FloatArray.asMicroList() = asMicroMutableList() as MicroList<Float>

fun FloatArray.asMicroMutableList() = object : MicroMutableList<Float> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Float) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun DoubleArray.asMicroList() = asMicroMutableList() as MicroList<Double>

fun DoubleArray.asMicroMutableList() = object : MicroMutableList<Double> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Double) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun BooleanArray.asMicroList() = asMicroMutableList() as MicroList<Boolean>

fun BooleanArray.asMicroMutableList() = object : MicroMutableList<Boolean> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Boolean) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}


fun CharArray.asMicroList() = asMicroMutableList() as MicroList<Char>

fun CharArray.asMicroMutableList() = object : MicroMutableList<Char> {

  override fun get(idx: Int) = this@asMicroMutableList[idx]
  override fun set(idx: Int, item: Char) {
    this@asMicroMutableList[idx] = item
  }

  override val size: Int get() = this@asMicroMutableList.size
  override fun iterator() = this@asMicroMutableList.iterator()
}

// endregion Generated Special Extensions
