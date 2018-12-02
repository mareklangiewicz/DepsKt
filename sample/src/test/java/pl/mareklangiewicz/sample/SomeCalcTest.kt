package pl.mareklangiewicz.sample

import org.junit.Assert.assertEquals
import org.junit.Test

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
