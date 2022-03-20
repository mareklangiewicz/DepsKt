import pl.mareklangiewicz.hello.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

class HelloTest {
    @Test fun testHello() = uspek {
        "On helloCommon" o {
            helloCommon()
        }
        "On helloPlatform" o {
            helloPlatform()
        }
    }
}