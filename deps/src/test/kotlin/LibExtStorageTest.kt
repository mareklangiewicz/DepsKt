import kotlin.test.*
import org.gradle.testfixtures.ProjectBuilder
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

/**
 * The ext storage and the entry points that read it: exactly ONE stored representation
 * ([extLib], a [Lib]), found by walking up the project hierarchy.
 *
 * See `docs/design/lib-details-denesting.md`.
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
    assertEquals(1, p.extensions.extraProperties.properties.keys.count { it == "Lib" }, "one stored entry")
  }

  @Test
  fun findExtLibWalksUpTheProjectHierarchy() {
    val root = project()
    val child = ProjectBuilder.builder().withParent(root).withName("child").build()
    root.extLib = someLib
    assertEquals(someLib, child.findExtLib())
  }

  @Test
  fun findExtLibThrowsWhenNothingIsSetAnywhere() {
    val child = ProjectBuilder.builder().withParent(project()).withName("child").build()
    assertFailsWith<LibNotFoundException> { child.findExtLib() }
  }

  @Test
  fun defaultGroupAndVerAndDescriptionReadsTheSiblingSet() {
    val p = project()
    p.defaultGroupAndVerAndDescription(someLib)
    assertEquals(someLib.info.group, p.group)
    assertEquals(someLib.info.version.str, p.version.toString())
    assertEquals("some description", p.description)
  }

  /** [rootExtLib] is the storage build scripts read from; no entry point reaches for it implicitly. */
  @Test
  fun rootExtLibReadsAndWritesTheRootProjectExt() {
    val p = project()
    p.rootExtLib = someLib
    assertEquals(someLib, p.rootExtLib)
    p.defaultGroupAndVerAndDescription(p.rootExtLib)
    assertEquals(someLib.info.version.str, p.version.toString())
    assertEquals("some description", p.description)
  }
}
