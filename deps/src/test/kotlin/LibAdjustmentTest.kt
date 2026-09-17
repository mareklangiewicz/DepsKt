import kotlin.test.*
import pl.mareklangiewicz.deps.*

/**
 * What adjusting a [Lib] does, and — more usefully — what it does NOT do.
 *
 * Ported from KGround's probes 16-18, which asserted the sibling `copy` rebuilt exactly what the
 * nested copy-dance rebuilt. That control is gone with the nested model, so these now state the
 * behaviour directly. Both remaining claims are about things that do not recompute: derived
 * siblings and derived identity fields are computed once, at construction.
 *
 * See `docs/design/lib-details-denesting.md`.
 */
class LibAdjustmentTest {

  /**
   * Andro present and compose present, so the untouched siblings have something to lose.
   *
   * `withJs = true` is stated rather than defaulted: the control below flips it to false and needs
   * that to be a real change. It used to ride on LibFlags' default, which is now false.
   */
  private val base: Lib = lib(
    info = myLibInfo(name = "SomeLib", description = "desc", version = Ver(1, 2, 3)),
    flags = LibFlags(withJs = true),
    withAndro = true,
  )

  /**
   * Adjusting flags touches the flags only. The siblings nobody named arrive unchanged — including
   * [Lib.compose], which [lib] DERIVED from the old flags and which `copy` does not re-derive.
   *
   * That is the sharp edge worth a test: a lib adjusted this way keeps compose options computed
   * from flags it no longer has. Re-derivation is [lib]'s job, and the second half asserts the two
   * really differ, so this is a choice rather than an accident nobody would notice.
   */
  @Test
  fun adjustingFlagsRebuildsNothingElse() {
    val newFlags = base.flags.copy(withJs = false, withLinuxX64 = false, withKotlinxHtml = true)
    val adjusted = base.copy(flags = newFlags)

    assertEquals(newFlags, adjusted.flags)
    assertEquals(base.compose, adjusted.compose, "compose must arrive untouched, not re-derived")
    assertEquals(base.repos, adjusted.repos)
    assertEquals(base.andro, adjusted.andro)
    assertEquals(base.info, adjusted.info)

    // ..and re-deriving really would have produced something else, so the assertions above bite
    val rebuilt = lib(base.info, newFlags, withAndro = true)
    assertNotEquals(adjusted.compose, rebuilt.compose, "control: the two differ for these flags")
    assertNotEquals(adjusted.repos, rebuilt.repos, "control: the two differ for these flags")
  }

  /**
   * `namespace` and `id` are CONSTRUCTOR DEFAULTS derived from group and name, and constructor
   * defaults run at construction only — `copy(name = ..)` does not recompute them. So a renamed lib
   * keeps the namespace it was built with. Worth a test rather than a comment: the alternative
   * behaviour would silently move an android library's package.
   */
  @Test
  fun renamingDoesNotRecomputeNamespaceOrId() {
    val original = base.info.namespace
    assertEquals("pl.mareklangiewicz.somelib", original) // the value really is name-derived..
    assertEquals(original, base.info.id)

    val renamed = base.copy(info = base.info.copy(name = "Kommand Line", description = "Kotlin DSL for CLIs."))
    assertEquals("Kommand Line", renamed.info.name)
    assertEquals(original, renamed.info.namespace) // ..and renaming still does not move it
    assertEquals(original, renamed.info.id)

    // building fresh under the new name WOULD move it — that is what makes the assertions above real
    assertEquals("pl.mareklangiewicz.kommand line", myLibInfo(name = "Kommand Line").namespace)
  }
}
