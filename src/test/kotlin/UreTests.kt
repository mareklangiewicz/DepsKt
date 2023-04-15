package pl.mareklangiewicz.ure

import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.junit.jupiter.api.*
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.uspek.*

class UreTests {

    @Disabled // FIXME later
    @TestFactory
    fun ureTestFactory() = uspekTestFactory {
        val simpleEmailURE = ure {
            1 of bBOLine
            1 of ure("user") {
                1..MAX of oneCharOf("\\w", "-", "\\.")
            }
            1 of ch("@")
            1 of ure("domain") {
                1..MAX of {
                    1..MAX of oneCharOf("\\w", "-")
                    1 of ch("\\.")
                }
                2..4 of oneCharOf("\\w", "-")
            }
            1 of bEOLine
        }
        val ureIR = simpleEmailURE.toIR()
        val regex = simpleEmailURE.compile()
        println("ure:\n$simpleEmailURE")
        println("ureIR:\n$ureIR")
        println("regex:\n$regex")
        "assert IR as expected" o { ureIR.str eq "^(?<user>[\\w-\\.]+)@(?<domain>(?:[\\w-]+\\.)+[\\w-]{2,4})\$" }
        testWithEmail(regex, "marek.langiewicz@gmail.com", "marek.langiewicz", "gmail.com")
        testWithEmail(regex, "langara@wp.pl", "langara", "wp.pl")
        testWithEmail(regex, "a.b.c@d.e.f.hhh", "a.b.c", "d.e.f.hhh")
        testWithIncorrectEmail(regex, "a.b.cd.e.f.hhh")
        testWithIncorrectEmail(regex, "a@b@c")
        testUreMultiplatform()
    }

    private fun testWithEmail(regex: Regex, email: String, expectedUser: String, expectedDomain: String) {
        "for email: $email" o {
            "it matches" o { regex.matches(email) eq true }
            "for match result" o {
                val result = regex.matchEntire(email)!!
                val groups = result.groups
                "it captures expected user name: $expectedUser" o { groups["user"]!!.value eq expectedUser }
                "it captures expected domain: $expectedDomain" o { groups["domain"]!!.value eq expectedDomain }
            }
        }
    }

    private fun testWithIncorrectEmail(regex: Regex, email: String) {
        "for incorrect email: $email" o {
            "it does not match" o { regex.matches(email) eq false }
            "match result is null" o { regex.matchEntire(email) eq null }
        }
    }

    private fun testUreMultiplatform() {
        "On UWidgetsCmnKt" o {
            val path = "/home/marek/code/kotlin/UWidgets/uwidgets/src/commonMain/kotlin/uwidgets/UWidgets.cmn.kt".toPath()
            val content = SYSTEM.readUtf8(path)
            val output = content.commentOutMultiplatformFun()
            println(output) // TODO_later: better tests
        }
    }

    @TestFactory
    @Disabled("Has side effects in other project.")
    fun testCommentOutMultiplatformStuff() = uspekTestFactory {
        val dir = "/home/marek/code/kotlin/uspek-painters/lib/src"
        "On dir: $dir" o {
            "comment out multiplatform stuff inside" o {
                SYSTEM.commentOutMultiplatformFunInEachKtFile(dir.toPath())
            }
        }
    }

    @TestFactory
    @Disabled("Has side effects in other project.")
    fun testUndoCommentOutMultiplatformStuff() = uspekTestFactory {
        val dir = "/home/marek/code/kotlin/uspek-painters/lib/src"
        "On dir: $dir" o {
            "undo comment out multiplatform stuff inside" o {
                SYSTEM.undoCommentOutMultiplatformFunInEachKtFile(dir.toPath())
            }
        }
    }
}