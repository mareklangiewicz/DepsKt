package pl.mareklangiewicz.sample

fun main(args: Array<String>) {
    val calc = SomeCalc(0)
    println(calc.result)
    calc.add(10)
    println(calc.result)
}

