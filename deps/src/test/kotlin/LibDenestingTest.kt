@file:Suppress("DEPRECATION") // the nested model is the CONTROL these tests compare against

import kotlin.test.*
import pl.mareklangiewicz.deps.*

/**
 * The equivalence harness for the de-nesting migration (step 1: data shape).
 *
 * KGround's prototype was held up by 19 Gradle probes because it lived in a consumer and had no
 * test source set of its own. Here the same claims are plain JUnit5 tests: the sibling model must
 * be field-for-field interchangeable with the nested one, and the two derivations that used to be
 * constructor defaults must reproduce those defaults exactly.
 *
 * See `docs/design/lib-details-denesting.md`.
 */
class LibDenestingTest {

  private val info = myLibInfo(name = "SomeLib", description = "desc", version = Ver(1, 2, 3))

  /** The 8 combinations the prototype checked: the inputs both cross-object derivations read. */
  private val flagCombos: List<LibFlags> = listOf(true, false).flatMap { withJvm ->
    listOf(true, false).flatMap { withJs ->
      listOf(true, false).map { withTestJUnit4 ->
        LibFlags(withJvm = withJvm, withJs = withJs, withTestJUnit4 = withTestJUnit4)
      }
    }
  }

  private fun LibFlags.toNestedSettings() = LibSettings(
    withJvm = withJvm,
    withJvmVer = withJvmVer,
    withJs = withJs,
    withLinuxX64 = withLinuxX64,
    withKotlinxHtml = withKotlinxHtml,
    withTestJUnit5 = withTestJUnit5,
    withTestJUnit4 = withTestJUnit4,
    withTestJUnit4OnAndroidDevice = withTestJUnit4OnAndroidDevice,
    withTestUSpekX = withTestUSpekX,
    withTestGoogleTruth = withTestGoogleTruth,
    withTestMockitoKotlin = withTestMockitoKotlin,
    withCentralPublish = withCentralPublish,
  )

  @Test
  fun sanityFlagCombosAreDistinct() {
    assertEquals(8, flagCombos.size)
    assertEquals(8, flagCombos.toSet().size)
  }

  @Test
  fun defaultLibComposeEqualsTheNestedConstructorDefault() {
    for (flags in flagCombos) {
      val nested = flags.toNestedSettings().compose
      assertNotNull(nested, "control: nested compose default is never null")
      assertEquals(nested.toSibling(), defaultLibCompose(flags), "compose derivation differs for $flags")
    }
  }

  @Test
  fun defaultLibReposEqualsTheNestedConstructorDefault() {
    for (flags in listOf(false, true).map { LibFlags(withKotlinxHtml = it) } + flagCombos) {
      assertEquals(flags.toNestedSettings().repos.toSibling(), defaultLibRepos(flags), "repos derivation differs for $flags")
    }
  }

  /** The derivations must actually VARY with their inputs, or equality above would prove nothing. */
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
  fun nestedRoundTripsThroughTheSiblingModel() {
    val nestedValues = flagCombos.flatMap { flags ->
      val settings = flags.toNestedSettings()
      listOf(
        LibDetails(
          name = info.name, group = info.group, description = info.description,
          authorId = info.authorId, authorName = info.authorName, authorEmail = info.authorEmail,
          githubUrl = info.githubUrl, licenceName = info.licenceName, licenceUrl = info.licenceUrl,
          version = info.version, settings = settings,
        ),
      ).flatMap { details ->
        listOf(
          details,
          details.copy(settings = settings.copy(compose = null)),
          details.copy(settings = settings.copy(andro = LibAndroSettings(publishVariant = "debug"))),
          details.copy(settings = settings.copy(andro = LibAndroSettings(sdkCompilePreview = "Bakla"))),
          // Non-default on purpose: with both sides defaulting, this round trip would pass even
          // if an adapter dropped sdkCompileMinor entirely.
          details.copy(settings = settings.copy(andro = LibAndroSettings(sdkCompileMinor = 7))),
        )
      }
    }
    for (details in nestedValues) assertEquals(details, details.toLib().toNested(), "nested round trip lost data")
  }

  @Test
  fun siblingRoundTripsThroughTheNestedModel() {
    val libs = flagCombos.flatMap { flags ->
      listOf(
        lib(info, flags),
        lib(info, flags, withCompose = false),
        lib(info, flags, withAndro = true),
        lib(info, flags, withCompose = false, withAndro = true, andro = LibAndro(publishVariant = "*")),
        lib(info, flags, withAndro = true, andro = LibAndro(sdkCompileMinor = 7)),
      )
    }
    for (l in libs) assertEquals(l, l.toNested().toLib(), "sibling round trip lost data")
  }

  @Test
  fun factoryEncodesPresenceInsteadOfNull() {
    assertNotNull(lib(info).compose, "compose is present by default, as in the nested model")
    assertNull(lib(info).andro, "andro is absent by default, as in the nested model")
    assertNull(lib(info, withCompose = false).compose)
    assertNotNull(lib(info, withAndro = true).andro)
    // an explicitly supplied sibling wins over the withXxx switch
    val explicit = LibAndro(sdkMin = 21)
    assertEquals(explicit, lib(info, withAndro = false, andro = explicit).andro)
  }

  /**
   * The ONE deliberate divergence from the nested defaults, asserted rather than commented:
   * [LibInfo.id] defaults to [LibInfo.namespace], while the nested `appId` defaulted to
   * `"$namespace.app"`. The suffix was a convention some repos already published without, so the
   * generic id slot does not bake it in. The adapters stay total -- `toLib`/`toNested` carry
   * whatever value is there -- it is only the DEFAULTS that differ.
   */
  @Test
  fun factoryMatchesTheNestedDefaultsForADefaultLibExceptId() {
    val fromFactory = lib(info, LibFlags())
    val fromNested = LibDetails(
      name = info.name, group = info.group, description = info.description,
      authorId = info.authorId, authorName = info.authorName, authorEmail = info.authorEmail,
      githubUrl = info.githubUrl, licenceName = info.licenceName, licenceUrl = info.licenceUrl,
      version = info.version, settings = LibSettings(),
    )
    assertEquals(fromFactory.info.namespace, fromFactory.info.id, "id defaults to namespace, bare")
    assertEquals(fromFactory.info.namespace + ".app", fromNested.appId, "nested appId kept the suffix")
    // everything else must still agree, so normalise the one field and compare in full
    assertEquals(fromNested.toLib().let { it.copy(info = it.info.copy(id = fromFactory.info.id)) }, fromFactory)
  }

  /** The live bug fixed in `d4e1d1d`; asserted here so it cannot come back in either model. */
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
      val nested = andro.toNested()
      assertEquals(all, nested.publishAllVariants, "nested publishAllVariants for '$variant'")
      assertEquals(no, nested.publishNoVariants, "nested publishNoVariants for '$variant'")
      assertEquals(one, nested.publishOneVariant, "nested publishOneVariant for '$variant'")
    }
  }

  /** Symptom 1 from the note: one statement instead of two, root named once. Compiles = win holds. */
  @Test
  fun adjustingTwoFlagsIsOneStatement() {
    val base = lib(info)
    val adjusted = base.copy(flags = base.flags.copy(withJs = false, withLinuxX64 = false))
    assertFalse(adjusted.flags.withJs)
    assertFalse(adjusted.flags.withLinuxX64)
    assertEquals(base.info, adjusted.info)
  }
}
