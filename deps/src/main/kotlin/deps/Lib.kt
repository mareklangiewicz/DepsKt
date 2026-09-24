@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

// region [[Lib — the sibling model]]

/**
 * Everything a build needs to know about the lib it is building, as five SIBLINGS rather than one
 * tree. See `docs/design/lib-details-denesting.md` for the design and the evidence behind it.
 *
 * Siblings, not a flattening: each of the five can be supplied independently as its own context
 * parameter, and the nullable ones ([compose], [andro]) can encode presence as *scope* instead of
 * as `null`. Nothing here needs context parameters itself, so build scripts — which Gradle compiles
 * flagless — can use all of it.
 */
data class Lib(
  val info: LibInfo,
  val flags: LibFlags,
  val repos: LibRepos,
  val compose: LibCompose?,
  val andro: LibAndro?,
)

/** Identity and coordinates: who this lib is, not how it is built. */
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
  /**
   * Generic identity slot: the one reverse-DNS name this thing is known by. Used as an android
   * `applicationId`, and equally as a bundle id, a plugin id, a desktop app id -- it is not
   * app-only, which is why it is [id] and not `appId`.
   *
   * Defaults to [namespace], WITHOUT the old `".app"` suffix: that suffix was a convention, not a
   * rule, and repos that had already published without it (kokpit667) had to override the field to
   * say so. The default now matches what is actually published; append `".app"` explicitly if an
   * app really wants a separate id from its library namespace.
   */
  val id: String = namespace,
  val appMainPackage: String = namespace,
  val appMainClass: String = "App_jvmKt", // for compose jvm
  val appMainFun: String = "main", // for native
  val appVerCode: Int = version.code, // currently used in andro apps
  val appVerName: String = version.str, // currently used in andro apps
) {
  fun withVer(version: Ver) = copy(version = version)
}

/**
 * Platform/testing flags only — deliberately no `compose`, `andro` or `repos` field, and no
 * `withCompose`/`withAndro`. Presence is answered by whether [Lib.compose] / [Lib.andro] is there
 * (and later: by whether a scope is open), never by a property on this object.
 */
data class LibFlags(
  val withJvm: Boolean = true,
  val withJvmVer: String? = Vers.JvmDefaultVer.takeIf { withJvm },
  /**
   * Off by default, like every other target but jvm. A js target is a real cost -- it pulls the
   * compose-html deps below, a kotlin-js-store/yarn.lock to keep current, and the slowest
   * compilation in a typical build -- so it should be asked for, not arrived at by not saying
   * anything. template-andro was carrying withJs = true purely by omission while having no js
   * source set at all.
   */
  val withJs: Boolean = false,
  val withLinuxX64: Boolean = false,
  val withKotlinxHtml: Boolean = false,
  val withTestJUnit5: Boolean = withJvm,
  val withTestJUnit4: Boolean = false,
  /** Needed because JUnit5 is STILL not supported for android on device tests.. */
  val withTestJUnit4OnAndroidDevice: Boolean = false,
  val withTestUSpekX: Boolean = true,
  val withTestGoogleTruth: Boolean = false,
  val withTestMockitoKotlin: Boolean = false,
)

/** Compose options. A sibling of [Lib], not a field of anything. */
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
  /**
   * Whether the js target gets Compose UI (skiko on a canvas) as well as compose-html.
   *
   * Default false, because the usual js shape here is compose-html only: a LIBRARY that pulls
   * ui/foundation/material into js cannot bundle skiko without an executable binary, which is what
   * the commonMain -> composeMain -> composeUiMain split exists to prevent.
   *
   * UWidgets is the counter-example that earns the flag: it renders Compose UI on a skiko canvas in
   * the browser on purpose (`ComposeViewport` in its `USkikoBoxDom`), and comparing dom-based and
   * canvas-based widgets side by side is the whole point of the library. Set it true there, so js
   * hangs off composeUiMain instead of composeMain.
   */
  val withComposeUiOnJs: Boolean = false,
  val withComposeTestUi: Boolean = false,
  val withComposeTestUiJUnit4: Boolean = false,
  val withComposeTestUiJUnit5: Boolean = false,
  val withComposeTestHtmlUtils: Boolean = false,
)

/** Android options. A sibling of [Lib], not a field of anything. */
data class LibAndro(
  /** Should override [sdkCompile] when not null */
  val sdkCompilePreview: String? = null,
  /** Should be ignored when [sdkCompilePreview] is not null */
  val sdkCompile: Int = Vers.AndroSdkCompile,
  /** Minor API level for [sdkCompile]. Was templatefun's `AndroSdkCompileMinor` const. */
  val sdkCompileMinor: Int = Vers.AndroSdkCompileMinor,
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
)

/**
 * How ONE module is published. Deliberately **not** a field of [Lib] and not reachable from
 * `gradle.extLib` — see `docs/design/publish-intent-per-module.md`.
 *
 * [Lib] is a per-REPO value: one `gradle.extLib` for the whole build. Publishing is per-MODULE, and
 * a repo publishes some of its modules and not the rest. Anything reachable from [Lib] is inherited
 * the moment a module writes `gradle.extLib.copy(flags = flags.copy(withJs = false))` — the normal,
 * correct idiom for platform flags. That is exactly how USpek's six SAMPLE apps acquired signed
 * publications and Maven Central tasks nobody asked for: `withCentralPublish` used to live on
 * [LibFlags], the one object modules routinely clone.
 *
 * So this type is passed as an argument to a `defaultBuildTemplateFor*` entry point, and **absence
 * means the module is not published at all**. A module that publishes says so, at the module.
 * Presence alone means LOCAL: publications exist and `publishToMavenLocal` works. [toCentral] is the
 * only switch that uploads anywhere. There is deliberately no `toLocal`: it would make
 * `LibPublish(toLocal = false)` a second spelling of `null`, and Central needs the very
 * publications that make local work, so `toCentral` without local is not a real state.
 *
 * It is also flat on purpose. An earlier draft wrapped it in a `LibModule(.., publish: LibPublish?)`,
 * which would have made flipping one boolean cost
 * `module.copy(publish = module.publish!!.copy(toCentral = true))` — symptom 1 of
 * `docs/design/lib-details-denesting.md`, reintroduced. One level, one `copy`, no `!!`.
 *
 * @param artifactId published artifactId; null means the project name. `:deps` publishes as
 *   `DepsKt` and `:sourcefun` as `SourceFun`, so a directory name and a coordinate can disagree.
 * @param pomName POM `<name>`; null means [LibInfo.name], which is the REPO name and right for most
 *   modules.
 * @param pomDescription POM `<description>`; null means [LibInfo.description].
 * @param toCentral whether this module goes to Maven Central. **The irreversible one** — a released
 *   coordinate is public forever. False still produces publications, for `publishToMavenLocal`,
 *   cross-repo handoff and CI fixtures; it just never uploads.
 * @param androVariant which android variant produces the published component; null means the module
 *   has no android component to publish, and `"*"` means all variants. Absorbed from
 *   `LibAndro.publishVariant`, whose default `""` meant "none".
 */
data class LibPublish(
  val artifactId: String? = null,
  val pomName: String? = null,
  val pomDescription: String? = null,
  val toCentral: Boolean = false,
  val androVariant: String? = null,
) {
  val androAllVariants get() = androVariant == AllVariants

  /** A single named variant, as opposed to none ([androVariant] null) or [AllVariants]. */
  val androOneVariant get() = androVariant != null && !androAllVariants

  companion object {
    const val AllVariants = "*"
  }
}

/** Repository options. See [defaultLibRepos] for the one flag they take from [LibFlags]. */
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

// endregion [[Lib — the sibling model]]

// region [[Lib — derivations and factory]]

/**
 * THE HARD PART, isolated: ten compose options that are derived from [LibFlags].
 *
 * Siblings cannot express this as a constructor default — a default argument only sees earlier
 * parameters of the SAME declaration — so the derivation is an explicit function. That is a gain,
 * not a workaround: the rule is named, callable and overridable in one place, instead of being
 * spelled out in a default that fires only when you omit the argument.
 *
 * Takes [flags] as a plain parameter, not a context parameter, so no compiler flag is needed here.
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

/** The second derivation: [LibRepos.withKotlinxHtml] tracks [LibFlags.withKotlinxHtml]. */
fun defaultLibRepos(flags: LibFlags): LibRepos = LibRepos(
  withKotlinxHtml = flags.withKotlinxHtml,
  withComposeJbDev = false,
)

/**
 * Assembles the sibling set, applying the derivations above for whatever is not given explicitly.
 *
 * [withCompose] / [withAndro] say whether those siblings EXIST at all.
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

/** Marek's defaults for everything identity-ish. */
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
