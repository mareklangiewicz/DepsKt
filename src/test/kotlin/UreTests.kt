import okio.ExperimentalFileSystem
import okio.Path.Companion.toPath
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.eq
import pl.mareklangiewicz.uspek.o
import pl.mareklangiewicz.uspek.uspekTestFactory

class UreTests {
    @TestFactory
    fun ureTestFactory() = uspekTestFactory {
        val simpleEmailURE = ure {
            1 of BOL
            1 of named("user") {
                1..MAX of oneCharOf("\\w", "-", "\\.")
            }
            1 of ch("@")
            1 of named("domain") {
                1..MAX of {
                    1..MAX of oneCharOf("\\w", "-")
                    1 of ch("\\.")
                }
                2..4 of oneCharOf("\\w", "-")
            }
            1 of EOL
        }
        val ureIR = simpleEmailURE.toIR()
        val regex = simpleEmailURE.compile()
        println("ure:\n$simpleEmailURE")
        println("ureIR:\n$ureIR")
        println("regex:\n$regex")
        "assert IR as expected" o { ureIR eq "^(?<user>[\\w-\\.]+)@(?<domain>(?:[\\w-]+\\.)+[\\w-]{2,4})\$" }
        testWithEmail(regex, "marek.langiewicz@gmail.com", "marek.langiewicz", "gmail.com")
        testWithEmail(regex, "langara@wp.pl", "langara", "wp.pl")
        testWithEmail(regex, "a.b.c@d.e.f.hhh", "a.b.c", "d.e.f.hhh")
        testWithIncorrectEmail(regex, "a.b.cd.e.f.hhh")
        testWithIncorrectEmail(regex, "a@b@c")
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

    @OptIn(ExperimentalFileSystem::class)
    @TestFactory
    fun testCommentOutMultiplatformStuff() = uspekTestFactory {
        val dir = "/home/marek/code/kotlin/uspek-painters/lib/src"
        "On dir: $dir" o {
            "comment out multiplatform stuff inside" o {
                commentOutMultiplatformFunInFileTree(dir.toPath())
            }
        }
    }

    @OptIn(ExperimentalFileSystem::class)
    @TestFactory
    fun testUndoCommentOutMultiplatformStuff() = uspekTestFactory {
        val dir = "/home/marek/code/kotlin/uspek-painters/lib/src"
        "On dir: $dir" o {
            "undo comment out multiplatform stuff inside" o {
                undoCommentOutMultiplatformFunInFileTree(dir.toPath())
            }
        }
    }
}