package pl.mareklangiewicz.sample

import org.junit.Assert.assertEquals
import org.junit.Test

class MicroCalcTest {

    @Test
    fun testMicroCalc() {
        val calc = MicroCalc(10)
        calc.add(5)
        assertEquals(15, calc.result)
        calc.add(100)
        assertEquals(115, calc.result)
    }
}
