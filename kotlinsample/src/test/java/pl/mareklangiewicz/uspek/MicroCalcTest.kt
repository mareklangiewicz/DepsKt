package pl.mareklangiewicz.uspek

class MicroCalcTest {

    @Test
    fun testMicroCalc() {
        val sut = MicroCalc(10)
        sut.add(5)
        assertEquals(15, sut.result)
        sut.add(100)
        assertEquals(115, sut.result)
    }
}
