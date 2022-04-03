import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.hello.*
import pl.mareklangiewicz.uspek.*

class AppJvmTest {
    @TestFactory fun exampleTests() = uspekTestFactory {
        "just launch main fun" o {
            main()
        }
    }
}