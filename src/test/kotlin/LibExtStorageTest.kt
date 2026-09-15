import kotlin.test.*
import org.gradle.testfixtures.ProjectBuilder
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

/**
 * Step 2 of `docs/design/lib-details-denesting.md`: the ext storage and the entry points.
 *
 * The claim under test is that there is exactly ONE stored representation ([extLib], a [Lib]) and
 * that [extLibDetails] is a VIEW over it rather than a second copy — so the two can never drift.
 */
class LibExtStorageTest {

  private val someLib = lib(
    info = myLibInfo(name = "SomeLib", description = "some description", version = Ver(1, 2, 3)),
    flags = LibFlags(withJs = false),
    withAndro = true,
  )

  private fun project() = ProjectBuilder.builder().build()

  @Test
  fun siblingSetSurvivesExtStorage() {
    val p = project()
    p.extLib = someLib
    assertEquals(someLib, p.extLib)
  }

  @Test
  fun nestedViewReadsWhatTheSiblingSetterWrote() {
    val p = project()
    p.extLib = someLib
    assertEquals(someLib.toNested(), p.extLibDetails)
  }

  @Test
  fun siblingViewReadsWhatTheNestedSetterWrote() {
    val p = project()
    val nested = someLib.toNested()
    p.extLibDetails = nested
    assertEquals(someLib, p.extLib)
    assertEquals(nested, p.extLibDetails)
  }

  /** The point of a view: writing through one accessor is visible through the other, not shadowed. */
  @Test
  fun thereIsOnlyOneStoredValue() {
    val p = project()
    p.extLib = someLib
    p.extLibDetails = someLib.toNested().copy(description = "changed via the nested view")
    assertEquals("changed via the nested view", p.extLib.info.description)
    assertEquals(1, p.extensions.extraProperties.properties.keys.count { it == "Lib" || it == "LibDetails" })
  }

  @Test
  fun findExtLibWalksUpTheProjectHierarchy() {
    val root = project()
    val child = ProjectBuilder.builder().withParent(root).withName("child").build()
    root.extLib = someLib
    assertEquals(someLib, child.findExtLib())
    assertEquals(someLib.toNested(), child.findExtLibDetails())
  }

  @Test
  fun findExtLibThrowsWhenNothingIsSetAnywhere() {
    val child = ProjectBuilder.builder().withParent(project()).withName("child").build()
    assertFailsWith<LibDetailsNotFoundException> { child.findExtLib() }
  }

  @Test
  fun defaultGroupAndVerAndDescriptionReadsTheSiblingSet() {
    val p = project()
    p.defaultGroupAndVerAndDescription(someLib)
    assertEquals(someLib.info.group, p.group)
    assertEquals(someLib.info.version.str, p.version.toString())
    assertEquals("some description", p.description)
  }

  /** The nested shim must reach the same state — it is the only thing existing callers have. */
  @Test
  fun nestedShimAgreesWithTheSiblingEntryPoint() {
    val viaSibling = project().also { it.defaultGroupAndVerAndDescription(someLib) }
    val viaNested = project().also { it.defaultGroupAndVerAndDescription(someLib.toNested()) }
    assertEquals(viaSibling.group, viaNested.group)
    assertEquals(viaSibling.version.toString(), viaNested.version.toString())
    assertEquals(viaSibling.description, viaNested.description)
  }

  /** With no argument at all it must resolve to the sibling overload and read root ext. */
  @Test
  fun defaultGroupAndVerAndDescriptionDefaultsToRootExtLib() {
    val p = project()
    p.rootExtLib = someLib
    p.defaultGroupAndVerAndDescription()
    assertEquals(someLib.info.version.str, p.version.toString())
    assertEquals("some description", p.description)
  }
}
