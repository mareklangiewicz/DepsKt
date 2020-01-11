package pl.mareklangiewicz.sample

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SomeCalcTest {

    @Test
    fun testSomeCalc() {
        val calc = SomeCalc(10)
        calc.add(5)
        assertEquals(15, calc.result)
        calc.add(100)
        assertEquals(115, calc.result)
    }
}
