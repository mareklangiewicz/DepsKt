@file:Suppress("PackageDirectoryMismatch", "unused", "DEPRECATION")

package pl.mareklangiewicz.deps

// region [[Lib — the de-nested sibling model]]

/**
 * The de-nested replacement for [LibDetails] and friends. See
 * `docs/design/lib-details-denesting.md` for the full design and the evidence behind it.
 *
 * This is step 1 of the migration: the DATA shape only. The nested types still exist, the adapters
 * below bridge them, and nothing in this file needs context parameters — so build scripts (which
 * Gradle compiles flagless) can already use it.
 *
 * The change is NOT flattening. The five types stay; they stop being fields of each other, so each
 * can later be supplied independently as its own context parameter, and the nullable ones
 * ([LibCompose], [LibAndro]) can encode presence as *scope* instead of as `null`.
 */
data class Lib(
  val info: LibInfo,
  val flags: LibFlags,
  val repos: LibRepos,
  val compose: LibCompose?,
  val andro: LibAndro?,
)

/** Identity and coordinates. Everything that was NOT `settings` in the nested [LibDetails]. */
data class LibInfo(
  val name: String,
  val group: String,
  val description: String,
  val authorId: String, // unique id in SCM like github
  val authorName: String,
  val authorEmail: String,
  val githubUrl: String,
  val licenceName: String,
  val licenceUrl: String,
  val version: Ver,
  val namespace: String = "$group.$name".lowercase(), // currently used in andro libs and apps
  val appId: String = "$namespace.app", // currently used in andro apps
  val appMainPackage: String = namespace,
  val appMainClass: String = "App_jvmKt", // for compose jvm
  val appMainFun: String = "main", // for native
  val appVerCode: Int = version.code, // currently used in andro apps
  val appVerName: String = version.str, // currently used in andro apps
) {
  fun withVer(version: Ver) = copy(version = version)
}

/**
 * Platform/testing flags only. Note what is GONE compared to [LibSettings]: the `compose`, `andro`
 * and `repos` fields, and with them `withCompose`/`withAndro`. Presence is answered by whether
 * [Lib.compose] / [Lib.andro] is there (and later: by whether a scope is open), not by a property
 * on this object.
 */
data class LibFlags(
  val withJvm: Boolean = true,
  val withJvmVer: String? = Vers.JvmDefaultVer.takeIf { withJvm },
  val withJs: Boolean = true,
  val withLinuxX64: Boolean = false,
  val withKotlinxHtml: Boolean = false,
  val withTestJUnit5: Boolean = withJvm,
  val withTestJUnit4: Boolean = false,
  /** Needed because JUnit5 is STILL not supported for android on device tests.. */
  val withTestJUnit4OnAndroidDevice: Boolean = false,
  val withTestUSpekX: Boolean = true,
  val withTestGoogleTruth: Boolean = false,
  val withTestMockitoKotlin: Boolean = false,
  val withCentralPublish: Boolean = false,
)

/** Same fields as [LibComposeSettings]; no longer a field of anything. */
data class LibCompose(
  val withComposeUi: Boolean = true,
  val withComposeFoundation: Boolean = true,
  val withComposeMaterial2: Boolean = true,
  val withComposeMaterial3: Boolean = true,
  val withComposeMaterialIconsExtended: Boolean = false,
  val withComposeFullAnimation: Boolean = true,
  val withComposeDesktop: Boolean = true,
  val withComposeDesktopComponents: Boolean = false,
  val withComposeHtmlCore: Boolean = false,
  val withComposeHtmlSvg: Boolean = false,
  val withComposeTestUi: Boolean = false,
  val withComposeTestUiJUnit4: Boolean = false,
  val withComposeTestUiJUnit5: Boolean = false,
  val withComposeTestHtmlUtils: Boolean = false,
)

/** Same fields as [LibAndroSettings]; no longer a field of anything. */
data class LibAndro(
  /** Should override [sdkCompile] when not null */
  val sdkCompilePreview: String? = null,
  /** Should be ignored when [sdkCompilePreview] is not null */
  val sdkCompile: Int = Vers.AndroSdkCompile,
  /** Should override [sdkTarget] when not null */
  val sdkTargetPreview: String? = null,
  /** Should be ignored when [sdkTargetPreview] is not null */
  val sdkTarget: Int = Vers.AndroSdkTarget,
  val sdkMin: Int = Vers.AndroSdkMin,
  val withAppCompat: Boolean = true,
  val withLifecycle: Boolean = true,
  val withActivityCompose: Boolean = true, // only used when compose is also present
  val withMDC: Boolean = false,
  val withTestEspresso: Boolean = true,
  val withTestRunner: String? = Vers.AndroTestRunner,
  val publishVariant: String = "", // for now only single variant or all variants can be published.
) {
  val publishAllVariants get() = publishVariant == AllVariants
  val publishNoVariants get() = publishVariant == NoVariants
  val publishOneVariant get() = !publishNoVariants && !publishAllVariants
  val AllVariants get() = "*"
  val NoVariants get() = ""
}

/** Same fields as [LibReposSettings]; no longer derived from [LibSettings.withKotlinxHtml]. */
data class LibRepos(
  /**
   * It's a huge footgun! If REALLY needed, then do it manually with strict repository content filter.
   * See: https://docs.gradle.org/current/userguide/supported_repository_types.html#sec:case-for-maven-local
   * See: https://github.com/typesafegithub/github-workflows-kt/issues/1694#issuecomment-2439799129
   */
  @Deprecated("It's a huge footgun! If REALLY needed, then do it manually with strict repository content filter.")
  val withMavenLocal: Boolean = false,
  val withMavenCentral: Boolean = true,
  val withGradle: Boolean = false,
  val withGoogle: Boolean = true,
  val withKotlinx: Boolean = true,
  val withKotlinxHtml: Boolean = false,
  val withComposeJbDev: Boolean = false,
  val withKtorEap: Boolean = false,
  val withJitpack: Boolean = false,
)

// endregion [[Lib — the de-nested sibling model]]

// region [[Lib — derivations and factory]]

/**
 * THE HARD PART, isolated. In the nested model these ten flags are default values of
 * [LibSettings.compose], so they can read `withJvm` / `withJs` / `withTestJUnit4` / `withTestJUnit5`
 * from the enclosing declaration. Siblings cannot do that — a default argument only sees earlier
 * parameters of the SAME declaration — so the derivation becomes an explicit function.
 *
 * That is a gain, not a workaround: the rule is now named, callable, and overridable at one place,
 * instead of being spelled out in a constructor default that fires only when you omit the argument.
 *
 * Takes [flags] as a plain parameter, not a context parameter, so step 1 needs no compiler flag;
 * it becomes `context(flags: LibFlags)` in step 3.
 */
fun defaultLibCompose(flags: LibFlags): LibCompose = with(flags) {
  LibCompose(
    withComposeMaterial2 = withJvm,
    withComposeMaterial3 = withJvm,
    withComposeFullAnimation = withJvm,
    withComposeDesktop = withJvm,
    withComposeHtmlCore = withJs,
    withComposeHtmlSvg = withJs,
    withComposeTestUi = withTestJUnit4 || withTestJUnit5,
    withComposeTestUiJUnit4 = withTestJUnit4,
    withComposeTestUiJUnit5 = withTestJUnit5,
    withComposeTestHtmlUtils = withJs,
  )
}

/** The second cross-object derivation: [LibReposSettings.withKotlinxHtml] tracked [LibSettings.withKotlinxHtml]. */
fun defaultLibRepos(flags: LibFlags): LibRepos = LibRepos(
  withKotlinxHtml = flags.withKotlinxHtml,
  withComposeJbDev = false,
)

/**
 * Assembles the sibling set, applying the derivations above for whatever is not given explicitly.
 *
 * [withCompose] / [withAndro] say whether those siblings EXIST at all — the one thing the nested
 * model expressed as `null` and consumers had to re-check with `!!` or `withCompose`.
 */
fun lib(
  info: LibInfo,
  flags: LibFlags = LibFlags(),
  withCompose: Boolean = true,
  withAndro: Boolean = false,
  repos: LibRepos? = null,
  compose: LibCompose? = null,
  andro: LibAndro? = null,
): Lib = Lib(
  info = info,
  flags = flags,
  repos = repos ?: defaultLibRepos(flags),
  compose = compose ?: defaultLibCompose(flags).takeIf { withCompose },
  andro = andro ?: LibAndro().takeIf { withAndro },
)

/** Sibling counterpart of [myLibDetails]: Marek's defaults for everything identity-ish. */
fun myLibInfo(
  name: String,
  group: String = "pl.mareklangiewicz",
  description: String = "",
  authorId: String = "mareklangiewicz",
  authorName: String = "Marek Langiewicz",
  authorEmail: String = "marek.langiewicz@gmail.com",
  githubUrl: String = "https://github.com/mareklangiewicz",
  licenceName: String = "Apache-2.0",
  licenceUrl: String = "https://opensource.org/licenses/Apache-2.0",
  version: Ver = Ver(0, 0, 1),
) = LibInfo(
  name = name,
  group = group,
  description = description,
  authorId = authorId,
  authorName = authorName,
  authorEmail = authorEmail,
  githubUrl = githubUrl,
  licenceName = licenceName,
  licenceUrl = licenceUrl,
  version = version,
)

// endregion [[Lib — derivations and factory]]

// region [[Lib — adapters to and from the nested model]]

/*
 * Both directions exist only while the two models coexist, and both are deleted in step 4:
 *  - [LibDetails.toLib] lets an existing nested value drive the new shape (what consumers use first);
 *  - [Lib.toNested] lets the new shape feed still-nested internals, and is the measuring instrument
 *    the equivalence tests compare against.
 *
 * They are total and lossless in both directions: the sibling types carry exactly the fields the
 * nested ones do, no more. (Notably `sdkCompileMinor`, which the prototype added to its andro
 * settings, is deliberately NOT here — adding it would make `toNested` lossy and weaken these tests.
 * It belongs in a later, additive step.)
 */

fun LibDetails.toLib(): Lib = Lib(
  info = LibInfo(
    name = name,
    group = group,
    description = description,
    authorId = authorId,
    authorName = authorName,
    authorEmail = authorEmail,
    githubUrl = githubUrl,
    licenceName = licenceName,
    licenceUrl = licenceUrl,
    version = version,
    namespace = namespace,
    appId = appId,
    appMainPackage = appMainPackage,
    appMainClass = appMainClass,
    appMainFun = appMainFun,
    appVerCode = appVerCode,
    appVerName = appVerName,
  ),
  flags = settings.toFlags(),
  repos = settings.repos.toSibling(),
  compose = settings.compose?.toSibling(),
  andro = settings.andro?.toSibling(),
)

fun LibSettings.toFlags() = LibFlags(
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

fun LibComposeSettings.toSibling() = LibCompose(
  withComposeUi = withComposeUi,
  withComposeFoundation = withComposeFoundation,
  withComposeMaterial2 = withComposeMaterial2,
  withComposeMaterial3 = withComposeMaterial3,
  withComposeMaterialIconsExtended = withComposeMaterialIconsExtended,
  withComposeFullAnimation = withComposeFullAnimation,
  withComposeDesktop = withComposeDesktop,
  withComposeDesktopComponents = withComposeDesktopComponents,
  withComposeHtmlCore = withComposeHtmlCore,
  withComposeHtmlSvg = withComposeHtmlSvg,
  withComposeTestUi = withComposeTestUi,
  withComposeTestUiJUnit4 = withComposeTestUiJUnit4,
  withComposeTestUiJUnit5 = withComposeTestUiJUnit5,
  withComposeTestHtmlUtils = withComposeTestHtmlUtils,
)

fun LibAndroSettings.toSibling() = LibAndro(
  sdkCompilePreview = sdkCompilePreview,
  sdkCompile = sdkCompile,
  sdkTargetPreview = sdkTargetPreview,
  sdkTarget = sdkTarget,
  sdkMin = sdkMin,
  withAppCompat = withAppCompat,
  withLifecycle = withLifecycle,
  withActivityCompose = withActivityCompose,
  withMDC = withMDC,
  withTestEspresso = withTestEspresso,
  withTestRunner = withTestRunner,
  publishVariant = publishVariant,
)

@Suppress("DEPRECATION")
fun LibReposSettings.toSibling() = LibRepos(
  withMavenLocal = withMavenLocal,
  withMavenCentral = withMavenCentral,
  withGradle = withGradle,
  withGoogle = withGoogle,
  withKotlinx = withKotlinx,
  withKotlinxHtml = withKotlinxHtml,
  withComposeJbDev = withComposeJbDev,
  withKtorEap = withKtorEap,
  withJitpack = withJitpack,
)

fun Lib.toNested(): LibDetails = LibDetails(
  name = info.name,
  group = info.group,
  description = info.description,
  authorId = info.authorId,
  authorName = info.authorName,
  authorEmail = info.authorEmail,
  githubUrl = info.githubUrl,
  licenceName = info.licenceName,
  licenceUrl = info.licenceUrl,
  version = info.version,
  namespace = info.namespace,
  appId = info.appId,
  appMainPackage = info.appMainPackage,
  appMainClass = info.appMainClass,
  appMainFun = info.appMainFun,
  appVerCode = info.appVerCode,
  appVerName = info.appVerName,
  settings = LibSettings(
    withJvm = flags.withJvm,
    withJvmVer = flags.withJvmVer,
    withJs = flags.withJs,
    withLinuxX64 = flags.withLinuxX64,
    withKotlinxHtml = flags.withKotlinxHtml,
    withTestJUnit5 = flags.withTestJUnit5,
    withTestJUnit4 = flags.withTestJUnit4,
    withTestJUnit4OnAndroidDevice = flags.withTestJUnit4OnAndroidDevice,
    withTestUSpekX = flags.withTestUSpekX,
    withTestGoogleTruth = flags.withTestGoogleTruth,
    withTestMockitoKotlin = flags.withTestMockitoKotlin,
    withCentralPublish = flags.withCentralPublish,
    compose = compose?.toNested(),
    andro = andro?.toNested(),
    repos = repos.toNested(),
  ),
)

fun LibCompose.toNested() = LibComposeSettings(
  withComposeUi = withComposeUi,
  withComposeFoundation = withComposeFoundation,
  withComposeMaterial2 = withComposeMaterial2,
  withComposeMaterial3 = withComposeMaterial3,
  withComposeMaterialIconsExtended = withComposeMaterialIconsExtended,
  withComposeFullAnimation = withComposeFullAnimation,
  withComposeDesktop = withComposeDesktop,
  withComposeDesktopComponents = withComposeDesktopComponents,
  withComposeHtmlCore = withComposeHtmlCore,
  withComposeHtmlSvg = withComposeHtmlSvg,
  withComposeTestUi = withComposeTestUi,
  withComposeTestUiJUnit4 = withComposeTestUiJUnit4,
  withComposeTestUiJUnit5 = withComposeTestUiJUnit5,
  withComposeTestHtmlUtils = withComposeTestHtmlUtils,
)

fun LibAndro.toNested() = LibAndroSettings(
  sdkCompilePreview = sdkCompilePreview,
  sdkCompile = sdkCompile,
  sdkTargetPreview = sdkTargetPreview,
  sdkTarget = sdkTarget,
  sdkMin = sdkMin,
  withAppCompat = withAppCompat,
  withLifecycle = withLifecycle,
  withActivityCompose = withActivityCompose,
  withMDC = withMDC,
  withTestEspresso = withTestEspresso,
  withTestRunner = withTestRunner,
  publishVariant = publishVariant,
)

@Suppress("DEPRECATION")
fun LibRepos.toNested() = LibReposSettings(
  withMavenLocal = withMavenLocal,
  withMavenCentral = withMavenCentral,
  withGradle = withGradle,
  withGoogle = withGoogle,
  withKotlinx = withKotlinx,
  withKotlinxHtml = withKotlinxHtml,
  withComposeJbDev = withComposeJbDev,
  withKtorEap = withKtorEap,
  withJitpack = withJitpack,
)

// endregion [[Lib — adapters to and from the nested model]]
