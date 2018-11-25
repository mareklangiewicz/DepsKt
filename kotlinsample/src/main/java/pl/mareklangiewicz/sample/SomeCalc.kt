package pl.mareklangiewicz.sample


class SomeCalc(var result: Int) {
    fun add(x: Int) { result += x }
    fun multiplyBy(x: Int) { result *= x }
}