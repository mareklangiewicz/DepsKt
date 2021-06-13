import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.uspekTestFactory

class UreTests {
    @TestFactory
    fun ureTestFactory() = uspekTestFactory {
        val ure = ure {
            1 of BOL
            1..MAX of oneCharOf("\\w", "-", "\\.")
            1 of ch("@")

            1..MAX of { // default created group is capturing (and unnamed); use: 'of group("name") of {' to change
                1..MAX of oneCharOf("\\w", "-")
                1 of ch("\\.")
            }
            2..4 of oneCharOf("\\w", "-")
            1 of EOL
        }
        val ureIR = ure.toIR()
        val regex = ure.compile()
        println("ure:\n$ure")
        println("ureIR:\n$ureIR")
        println("regex:\n$regex")
    }
}