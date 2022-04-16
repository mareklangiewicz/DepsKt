@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.hello.cli

import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.hello.*
import pl.mareklangiewicz.uspek.*

class MainCliTest {
    @TestFactory fun mainCliTest() = uspekTestFactory {
        "On template jvm cli project" o {
            "Check helloCommon output" o { helloCommon() eq "Hello Pure Common World!" }
            "Check helloCommon output - should fail" ox { helloCommon() eq "Incorrect output" }
            "Check helloPlatform output" o { helloPlatform() eq "Hello JVM World!" }
            "Check helloPlatform output - should fail" ox { helloPlatform() eq "Incorrect output" }
            "Just run whole main fun" o { main() }
        }
    }
}