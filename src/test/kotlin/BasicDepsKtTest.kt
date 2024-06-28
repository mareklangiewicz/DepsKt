import kotlin.test.*
import pl.mareklangiewicz.deps.Dep
import pl.mareklangiewicz.deps.Ver
import pl.mareklangiewicz.deps.verLastStable

class BasicDepsKtTest {

  @Test
  fun printSomStuff() {
    println(vers.DepsPlug)
    println(plugs.Deps)
    println(vers.ComposeJb)
    println(plugs.ComposeJb)
    println(repos.composeJbDev)
  }

  @Test
  fun getLastVer() {
    val dep = Dep("org.jetbrains.compose", "compose-gradle-plugin", listOf(Ver("1.6.11"), Ver("1.6.20-dev1673")))
    check(dep.verLast.str == "1.6.20-dev1673")
    check(dep.verLastStable.str == "1.6.11")
  }
}
