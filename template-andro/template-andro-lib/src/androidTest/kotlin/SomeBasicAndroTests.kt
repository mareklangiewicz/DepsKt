import android.util.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.*
import org.junit.*
import pl.mareklangiewicz.templateandrolib.*
import pl.mareklangiewicz.uspek.*

// I don't even try to use @RunWith(USpekJUnit4Runner::class) in android tests,
// because android tests are sooo broken / so many moving parts / so unreliable, I don't want to add another moving part.
// see also https://developer.android.com/studio/test/test-in-android-studio#bumblebee-unified-test-runner
// it's safer to stick with default runner as set up in build.gradle.kts:fun LibraryExtension.defaultDefaultConfig
// See also: template-andro-lib:build.gradle.kts:defaultBuildTemplateForAndroidApp...
// debugImplementation(AndroidX.Tracing.ktx) // https://github.com/android/android-test/issues/1755
// See also file USpekCustomRunnerTests.kt where I try my own runner and it looks like working,
// but I wouldn't trust it where I try my own runner and it looks like working, but I wouldn't trust it.
class SomeBasicAndroTests {

    init {
        // This is very simple reporting. watch logcat with filter "uspek" during tests.
        // Ultimately I want to connect uspekLog to some separate better reporting tool/library,
        // to be less dependent on old junit/andro bs...
        uspekLog = {
            if (it.failed) Log.e("uspek", it.status)
            else Log.w("uspek", it.status)

            // optionally to make whole test fail if any uspek branch failed
            if (it.failed) throw it.end!!
        }
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun basicComposeTest() {
        var info by mutableStateOf("")
        rule.setContent {
            Column {
                Text(info)
                HelloStuff(name = "tesssst")
            }
        }
        Thread.sleep(1000)
        for (i in 1..20) {
            info = "some info nr $i"
            rule.onNodeWithText("Rotate", substring = true).performClick()
            Thread.sleep(200)
            rule.onNodeWithText("nr $i", substring = true).assertIsDisplayed()
        }
    }

    @Test
    fun basicComposeUSpekTests() {

        var info by mutableStateOf("")
        rule.setContent {
            Column {
                Text(info)
                HelloStuff(name = "tesssst")
            }
        }

        // See logcat filtered with "uspek" for reasonable report with scenarios, results, line numbers, etc.
        uspek {
            "On i in 1..20" o {
                for (i in 1..20) {
                    "On i == $i" o {
                        // it's important that this subtree has different name each time in the loop!
                        // (tests with the same name on the same level are skipped when encountered second time)

                        "On new info state" o {
                            info = "some info nr $i"

                            "On click Rotate" o {

                                rule.onNodeWithText("Rotate", substring = true).performClick()
                                    // HelloStuff is stateful and I don't reset state so each click will rotate it even more..
                                    // Also note: the check below is separate test, so uspek will performClick twice for each "i"

                                Thread.sleep(200)

                                "check if info with selected i is displayed" o {
                                    rule.onNodeWithText("nr $i", substring = true).assertIsDisplayed()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}