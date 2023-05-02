import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*

@RunWith(USpekJUnit4Runner::class)
class SomeBasicUnitTests {

    @USpekTestTree(2)
    fun uspekTest() {
        "On some list" o {
            val list = listOf(4, 5, 6)

            "list is not empty" o { assertNotEquals(0, list.size) }
        }
    }
}