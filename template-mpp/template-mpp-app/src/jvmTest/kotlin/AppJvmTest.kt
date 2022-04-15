import org.junit.*
import pl.mareklangiewicz.hello.*
import pl.mareklangiewicz.uspek.*

class AppJvmTest {

    @Ignore
    @Test
    fun exampleTests() = uspek {
        "Launch main window (user has to close it)" o {
            main()
        }
    }
}