@file:Suppress("DEPRECATION") // the nested model is the CONTROL these tests compare against

import kotlin.test.*
import pl.mareklangiewicz.deps.*

/**
 * Adjusting a [Lib] must rebuild exactly what the nested copy-dance rebuilt.
 *
 * Ported from KGround's probes 16-18, which asserted this against that repo's real
 * `gradle.extLibDetails` from a Gradle task. They were the last of the 19 probes with content worth
 * keeping, and they are model claims, not claims about the script/module seam — so they belong
 * here, where they run in seconds and cannot rot on a branch. See
 * `docs/design/lib-details-denesting.md`.
 *
 * Deliberately NOT ported: `probeAndroScope`. Its interesting half — that a function needing andro
 * cannot be CALLED without an andro scope — is a compile error, which no test can assert; the note
 * records it as proven by construction. Its runtime half only says `sdkCompileMinor` survives on
 * [LibAndro], which the round-trip tests in [LibDenestingTest] already cover with a non-default
 * value. Porting it would have meant putting `-Xcontext-parameters` on this source set to buy no
 * evidence.
 */
class LibAdjustmentTest {

  /** Andro present and compose present, so the untouched siblings have something to lose. */
  private val base: Lib = lib(
    info = myLibInfo(name = "SomeLib", description = "desc", version = Ver(1, 2, 3)),
    withAndro = true,
  )

  @Test
  fun adjustingFlagsRebuildsAnIdenticalLibDetails() {
    val nestedDance = base.toNested().let {
      it.copy(settings = it.settings.copy(withJs = false, withLinuxX64 = false, withKotlinxHtml = true))
    }
    val siblingForm = base
      .copy(flags = base.flags.copy(withJs = false, withLinuxX64 = false, withKotlinxHtml = true))
      .toNested()
    // Data-class equality, so this covers compose/andro/repos too: the siblings nobody touched must
    // arrive unchanged, or the whole module configuration diverges with them.
    assertEquals(nestedDance, siblingForm)
  }

  /** Without this the assertion above would pass even if both sides ignored their arguments. */
  @Test
  fun thatComparisonCanTellTwoConfigsApart() {
    val nestedDance = base.toNested().let {
      it.copy(settings = it.settings.copy(withJs = false, withLinuxX64 = false, withKotlinxHtml = true))
    }
    val different = base.copy(flags = base.flags.copy(withJs = true)).toNested()
    assertNotEquals(nestedDance, different)
  }

  /**
   * The identity-adjusting form (KGround's kommand-line / -samples rename it before configuring).
   *
   * This one guards the published coordinate: `coordinates(artifactId = ..)` reads the lib name, so
   * both forms must agree, and both must carry the ORIGINAL namespace forward — see below.
   */
  @Test
  fun adjustingIdentityRebuildsAnIdenticalLibDetails() {
    val renamed = "Kommand Line"
    val newDesc = "Kotlin DSL for popular CLI commands."
    val nestedRenamed = base.toNested().copy(name = renamed, description = newDesc)
    val siblingRenamed = base
      .copy(info = base.info.copy(name = renamed, description = newDesc))
      .toNested()
    assertEquals(nestedRenamed, siblingRenamed)
    assertEquals(renamed, siblingRenamed.name)
  }

  /**
   * `namespace` and `appId` are CONSTRUCTOR DEFAULTS derived from group and name, and constructor
   * defaults run at construction only — `copy(name = ..)` does not recompute them. So a renamed lib
   * keeps the namespace it was built with, in both models. Worth a test rather than a comment:
   * the alternative behaviour would silently move an android library's package.
   */
  @Test
  fun renamingDoesNotRecomputeNamespace() {
    val original = base.info.namespace
    assertEquals("pl.mareklangiewicz.somelib", original) // the value really is name-derived...
    val siblingRenamed = base.copy(info = base.info.copy(name = "Kommand Line")).toNested()
    assertEquals(original, siblingRenamed.namespace) // ...and renaming still does not move it
    assertEquals(base.toNested().copy(name = "Kommand Line").namespace, siblingRenamed.namespace)
  }
}
