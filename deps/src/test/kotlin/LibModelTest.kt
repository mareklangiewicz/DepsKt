import kotlin.test.*
import pl.mareklangiewicz.deps.*

/**
 * The [Lib] sibling model: its factory, its two derivations, and the defaults they produce.
 *
 * These started as an equivalence harness against the nested `LibDetails`, which was the control
 * while both models coexisted. The nested model is gone, so the derivations are now the spec and
 * the expected values are written down here instead of computed from a second implementation.
 *
 * See `docs/design/lib-details-denesting.md`.
 */
class LibModelTest {

  private val info = myLibInfo(name = "SomeLib", description = "desc", version = Ver(1, 2, 3))

  /** The 8 combinations of the inputs both derivations read. */
  private val flagCombos: List<LibFlags> = listOf(true, false).flatMap { withJvm ->
    listOf(true, false).flatMap { withJs ->
      listOf(true, false).map { withTestJUnit4 ->
        LibFlags(withJvm = withJvm, withJs = withJs, withTestJUnit4 = withTestJUnit4)
      }
    }
  }

  @Test
  fun sanityFlagCombosAreDistinct() {
    assertEquals(8, flagCombos.size)
    assertEquals(8, flagCombos.toSet().size)
  }

  /**
   * The compose derivation, spelled out rather than compared to another implementation: every
   * option it touches, for every combination of the flags it reads. The options it does NOT
   * derive keep [LibCompose]'s own defaults, which the full comparison at the end pins too.
   */
  @Test
  fun defaultLibComposeDerivesEveryOptionFromTheFlags() {
    for (flags in flagCombos) {
      val c = defaultLibCompose(flags)
      val anyJUnit = flags.withTestJUnit4 || flags.withTestJUnit5
      assertEquals(flags.withJvm, c.withComposeMaterial2, "material2 for $flags")
      assertEquals(flags.withJvm, c.withComposeMaterial3, "material3 for $flags")
      assertEquals(flags.withJvm, c.withComposeFullAnimation, "fullAnimation for $flags")
      assertEquals(flags.withJvm, c.withComposeDesktop, "desktop for $flags")
      assertEquals(flags.withJs, c.withComposeHtmlCore, "htmlCore for $flags")
      assertEquals(flags.withJs, c.withComposeHtmlSvg, "htmlSvg for $flags")
      assertEquals(flags.withJs, c.withComposeTestHtmlUtils, "testHtmlUtils for $flags")
      assertEquals(anyJUnit, c.withComposeTestUi, "testUi for $flags")
      assertEquals(flags.withTestJUnit4, c.withComposeTestUiJUnit4, "testUiJUnit4 for $flags")
      assertEquals(flags.withTestJUnit5, c.withComposeTestUiJUnit5, "testUiJUnit5 for $flags")
      // untouched by the derivation, so they must still be LibCompose's own defaults
      assertTrue(c.withComposeUi && c.withComposeFoundation, "undevived options changed for $flags")
    }
  }

  @Test
  fun defaultLibReposTracksWithKotlinxHtmlAndNothingElse() {
    for (withKotlinxHtml in listOf(true, false)) {
      val flags = LibFlags(withKotlinxHtml = withKotlinxHtml)
      assertEquals(LibRepos(withKotlinxHtml = withKotlinxHtml, withComposeJbDev = false), defaultLibRepos(flags))
    }
    // and it reads nothing else: the other flags must not move it
    for (flags in flagCombos) assertEquals(LibRepos(withKotlinxHtml = false, withComposeJbDev = false), defaultLibRepos(flags))
  }

  /** The derivations must actually VARY with their inputs, or the assertions above prove little. */
  @Test
  fun derivationsDependOnTheirInputs() {
    assertNotEquals(
      defaultLibCompose(LibFlags(withJvm = true, withJs = true)),
      defaultLibCompose(LibFlags(withJvm = false, withJs = false)),
    )
    assertNotEquals(
      defaultLibRepos(LibFlags(withKotlinxHtml = true)),
      defaultLibRepos(LibFlags(withKotlinxHtml = false)),
    )
  }

  @Test
  fun factoryEncodesPresenceInsteadOfNull() {
    assertNotNull(lib(info).compose, "compose is present by default")
    assertNull(lib(info).andro, "andro is absent by default")
    assertNull(lib(info, withCompose = false).compose)
    assertNotNull(lib(info, withAndro = true).andro)
    // an explicitly supplied sibling wins over the withXxx switch
    val explicit = LibAndro(sdkMin = 21)
    assertEquals(explicit, lib(info, withAndro = false, andro = explicit).andro)
  }

  /** The factory must apply the derivations, not LibCompose/LibRepos' bare constructor defaults. */
  @Test
  fun factoryBuildsItsSiblingsWithTheDerivations() {
    val flags = LibFlags(withJvm = false, withJs = false, withKotlinxHtml = true)
    val built = lib(info, flags, withAndro = true)
    assertEquals(Lib(info, flags, defaultLibRepos(flags), defaultLibCompose(flags), LibAndro()), built)
    assertNotEquals(LibCompose(), built.compose, "control: the derivation is not the bare default here")
  }

  /**
   * [LibInfo.id] defaults to [LibInfo.namespace], with no `".app"` suffix: that suffix was a
   * convention, not a rule, and repos that had already published without it had to override the
   * field to say so. An app that wants a distinct id still writes one.
   */
  @Test
  fun idDefaultsToTheBareNamespace() {
    val i = lib(info, LibFlags()).info
    assertEquals("pl.mareklangiewicz.somelib", i.namespace)
    assertEquals(i.namespace, i.id)
  }

  /** The live bug fixed in `d4e1d1d`; asserted here so it cannot come back. */
  @Test
  fun publishVariantTruthTable() {
    for ((variant, expected) in listOf(
      "" to Triple(false, true, false), // all, no, one
      "*" to Triple(true, false, false),
      "debug" to Triple(false, false, true),
    )) {
      val (all, no, one) = expected
      val andro = LibAndro(publishVariant = variant)
      assertEquals(all, andro.publishAllVariants, "publishAllVariants for '$variant'")
      assertEquals(no, andro.publishNoVariants, "publishNoVariants for '$variant'")
      assertEquals(one, andro.publishOneVariant, "publishOneVariant for '$variant'")
    }
  }

  /** Symptom 1 from the note: one statement instead of two, root named once. */
  @Test
  fun adjustingTwoFlagsIsOneStatement() {
    val base = lib(info)
    val adjusted = base.copy(flags = base.flags.copy(withJs = false, withLinuxX64 = false))
    assertFalse(adjusted.flags.withJs)
    assertFalse(adjusted.flags.withLinuxX64)
    assertEquals(base.info, adjusted.info)
  }
}
