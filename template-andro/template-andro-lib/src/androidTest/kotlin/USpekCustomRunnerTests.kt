import android.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.test.junit4.*
import org.junit.*
import org.junit.Test
import org.junit.runner.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*


@RunWith(USpekJUnit4Runner::class)
class USpekCustomRunnerTests {

    init {
        uspekLog = {
            if (it.failed) Log.e("uspek", it.status)
            else Log.w("uspek", it.status)
        }
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun simpleTest() = assertEquals(2, 2)


    @USpekTestTree(6)
    fun layoutUSpek() = with(rule) {
        "On simple box content" o {
            setContent {
                Box {
                    Text("First simple box")
                }
            }
        }
        "On second nothing test" o {
            setContent {
                Box(Modifier.background(Color.Blue)) {
                    Text("Second simple box")
                }
            }
            Thread.sleep(1800)
            assertEquals(4, 4)
        }
        "On third nothing test" o {
            assertEquals(5, 5)
            "On inner UI test" o {
                setContent {
                    Box(Modifier.background(Color.Cyan)) {
                        Text("Third inner box")
                    }
                }
                "wait a bit with content and fail" o {
                    Thread.sleep(1000)
                    fail()
                }
                "wait a bit again with content and finish" o {
                    Thread.sleep(1000)
                }
            }
        }
    }
}