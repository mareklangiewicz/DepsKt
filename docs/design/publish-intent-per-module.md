# Publish intent is a per-MODULE value

Status: **SHIPPED — steps 2, 3 and 4 all done**. Triggered by a real incident in USpek on
2026-09-18 — a FAILED RELEASE, not a hypothetical — measured there with a control on the
pre-migration commit. USpek is mitigated at the consumer (`14e8247`) and released as 0.0.46; this
note is the durable fix behind it.

What is done: `LibPublish` exists, `LibFlags.withCentralPublish` and `LibAndro.publishVariant` are
gone, all ten `defaultBuildTemplateFor*` entry points take `publish: LibPublish? = null`, and the
`hasPlugin` inference is replaced by `defaultPublishingOrNot`, which errors on either mismatch
(relaxed to ONE direction in 0.4.65 — see "The tripwire is retired" below).
Exercised for real by `samplefun`, which substitutes the local build through `includeBuild("..")`.

Released as **DepsKt 0.4.63** (Gradle Plugin Portal) and migrated onto in all four repos that
tracked it: KGround (`f92ec8a6`, `eeaced07`), USpek (`33b3926`), UPue (`9d3f7f7`) and DepsKt's own
build scripts (`1feb661`). Per-repo publication task sets were diffed byte-identical against
baselines taken before the migration, and both new error paths were proven to fire.

What is NOT done: consumer repos outside those four are still pinned below 0.4.62 and will meet
the breaking change when they migrate. That is by design — see the migration hazard below.

The design section was rewritten after review caught the first draft re-introducing the nesting
this repo had just spent a release cycle removing. That correction is kept below rather than
tidied away, because the mistake is easy to repeat.

The thesis in one line: **a module publishes because someone said so at that module, or it does not
publish.** Today intent is inherited, inferred, and spread across three types.

## The incident, and what it measured

USpek `ec35047` (post-templatefun, deps.settings 0.4.62), `./gradlew publishToMavenLocal`:

```
Invalid publication 'js':
  - Variant 'jsRuntimeElements-published' contains a dependency on
    enforced platform 'org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom'
```

`:ktjsreactsample` is a SAMPLE APP. It had a full signed maven publication, and so did the five
other `kt*sample` modules. Nobody decided that.

Two findings, deliberately kept apart:

1. **The enforced-platform failure is NOT a migration regression.** Checked out `8a75b28` (the
   pre-templatefun commit, deps.settings 0.3.99) in a throwaway worktree and ran the same task:
   byte-identical failure. It has been latent since the BOM was added to `jsMain`. It never
   surfaced because nothing ran a repo-wide `publishToMavenLocal`, and `drelease` runs
   `publishAndReleaseToMavenCentral`, for which the sample had **no tasks at all** in the old model.

2. **Maven Central reachability IS a migration regression.** Task counts, `:ktlinuxsample`:

   | tasks matching "mavenCentral" | `8a75b28` (nested) | `ec35047` (siblings) |
   |---|---|---|
   | count | **0** | **8**, incl. `publishAllPublicationsToMavenCentralRepository` |

   The next `v0.0.44` tag would have pushed six sample artifacts to Maven Central under
   `pl.mareklangiewicz`. Permanently. `ktjsreactsample` would have failed first and aborted the
   release — the latent bug in (1) is what prevented the irreversible half of (2).

Residue confirming it was live, not theoretical: `~/.m2/repository/pl/mareklangiewicz/ktandrosample`
and `ktandrosample-android` at `0.0.44`, signed, written by an ordinary local publish.

## Why the de-nesting made (2) happen

Not a wrong default — `LibFlags.withCentralPublish` defaults to `false` (`deps/…/Lib.kt:82`).

Nested model: each sample built a **fresh** `LibSettings(...)`, so it got the default `false`
regardless of what the root said.

Sibling model: each sample writes `gradle.extLib.copy(flags = flags.copy(withJs = false, …))`. That
is the idiom the de-nesting was *for*, and it is correct for platform flags. But `withCentralPublish`
lives in `LibFlags` too, so cloning the flags for a platform reason silently clones the repo's
publish intent.

**`LibFlags` is the object modules routinely copy. Anything in it is inherited by default.** That is
the right property for `withJs` and the wrong one for "does this go on Maven Central forever".

## Intent lives in three places today, and none of them is the module

1. `LibFlags.withCentralPublish` — inherited via `.copy()`, as above.
2. `if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing()` —
   `MppBuildTemplates.kt:113`, `JvmBuildTemplates.kt:39`, `AndroBuildTemplates.kt:250`.
   This is the deepest one: it reads intent from the `plugins {}` block, which in these repos is
   inside a region-marked boilerplate header. Six USpek samples "decided" to publish by inheriting
   a copy-pasted line.
3. `LibAndro.publishVariant` — the only one that is genuinely per-module, and the only one that
   behaved.

## Andro App already has the right shape

`defaultBuildTemplateForAndroApp` (`AndroBuildTemplates.kt:321`):

```kotlin
require(!andro.publishAllVariants) { "Only single app variant can be published" }
val variant = andro.publishVariant.takeIf { andro.publishOneVariant }
…
variant?.let { defaultPublishingOfAndroApp(it) }
```

`publishVariant` defaults to `NoVariants == ""`, so an android app publishes **nothing** unless a
variant is named. Explicit, per-module, defaults to silence. That is the shape to generalise.

Its weakness is encoding, not policy: `""` and `"*"` as magic strings, with the three predicates
(`publishAllVariants` / `publishNoVariants` / `publishOneVariant`) derived from them.

## The design: one flat per-module value, passed at the call site

An earlier draft of this note proposed `LibModule(artifactId, name, description, publish: LibPublish?)`
— a two-level tree. That was wrong, and it was wrong in exactly the way `lib-details-denesting.md`
documents: flipping one boolean would have cost
`module.copy(publish = module.publish!!.copy(toCentral = true))`, which is symptom #1 of that note
verbatim, `!!` included. A design whose whole point is "stop reaching through levels" cannot
introduce a level.

So: no wrapper type. **One flat type, supplied as a template argument, absent by default.**

```kotlin
/**
 * Per-MODULE publishing. Deliberately NOT a field of [Lib] and NOT reachable from `gradle.extLib`
 * — see "Why it cannot live in Lib" below. Supplied at the call site; absent means not published.
 */
data class LibPublish(
  val artifactId: String? = null,     // null = project.name
  val pomName: String? = null,        // null = info.name
  val pomDescription: String? = null, // null = info.description
  /** false = publications exist (mavenLocal, composite handoff, CI fixtures), Central untouched. */
  val toCentral: Boolean = false,
  /** Absorbs LibAndro.publishVariant. */
  val androVariant: String? = null,
)
```

Entry points take it as an ordinary defaulted parameter:

```kotlin
fun Project.defaultBuildTemplateForBasicMppLib(
  lib: Lib = gradle.extLib,
  publish: LibPublish? = null,
  …
) {
  …
  publish?.let { context(it) { defaultPublishing() } }
}
```

That `?.let { context(it) { … } }` is not a new idiom — `AndroBuildTemplates` already writes
`lib.compose?.let { context(compose) { … } }`. Presence becomes scope, which is the de-nesting
note's strongest argument, applied one level out.

### Call sites stay flat

```kotlin
defaultBuildTemplateForBasicMppLib(publish = LibPublish(toCentral = true))   // :uspek
defaultBuildTemplateForBasicJvmLib(publish = LibPublish(artifactId = "DepsKt", toCentral = true))
defaultBuildTemplateForBasicMppApp()                                        // a sample: nothing
```

One constructor call, no `copy`, no `!!`, no reaching. A module that does not publish says nothing
at all — which is the property the whole note exists to get.

### Why it cannot live in `Lib`

The obvious alternative is a sixth sibling: `Lib(info, flags, repos, compose?, andro?, publish?)`.
That reproduces the bug it is meant to fix.

`compose` and `andro` work as nullable siblings because they are genuinely repo-shaped. Publishing
is not: a repo publishes some of its modules and not the rest. And `Lib` is bound to
`gradle.extLib`, so **anything reachable from it is inherited the moment a module writes
`gradle.extLib.copy(flags = flags.copy(withJs = false))`** — which is now the standard idiom, and
correct for platform flags. USpek would repeat verbatim.

The rule this generalises to, and the one actually worth keeping:

> A value belongs in `Lib` only if it is repo-shaped. Anything per-module is a call-site argument,
> because `gradle.extLib.copy(..)` will carry whatever is within reach.

`LibFlags` is the object modules routinely clone. That is the right property for `withJs` and the
wrong one for "does this go on Maven Central forever."

### What this removes

The proposal is net-negative in types and parameters. It deletes:

- `LibFlags.withCentralPublish` — so the cloned object stops carrying publish intent. The USpek
  regression becomes structurally impossible, not merely defaulted away.
- the three `if (plugins.hasPlugin("com.vanniktech.maven.publish"))` guards.
- `LibAndro.publishVariant`, its three derived predicates (`publishAllVariants` /
  `publishNoVariants` / `publishOneVariant`, keyed on `""` and `"*"`) and the
  `require(!publishAllVariants)` runtime check.
- one context parameter: `defaultPublishing` takes `context(info: LibInfo, flags: LibFlags)` today
  and needs `flags` only for `withCentralPublish`. It becomes
  `context(info: LibInfo, publish: LibPublish)` — and is then uncallable outside a publish scope,
  which is a stronger guarantee than any null check.
- `:sourcefun`'s hand-rolled publishing block and `:deps`' second `coordinates(..)` call relying on
  last-call-wins — `artifactId` / `pomName` / `pomDescription` are what the fields are for.

Net: one new flat type, five things gone.

## Staging

**Step 1 — now, non-breaking.** Make the Jvm and Mpp `*App` templates never call
`defaultPublishing()`, matching `defaultBuildTemplateForAndroApp`. Libraries are unaffected; no lib
has ever been published by accident. This alone makes the USpek shape impossible and needs no model
change. Does not fix the `hasPlugin` inference for libs.

**Step 2 — the real fix.** Introduce `LibModule` + `LibPublish`; retire `LibFlags.withCentralPublish`,
the three `hasPlugin` guards, and `LibAndro.publishVariant`.

### The migration hazard, and the rule that contains it

`publish = null` by default means every publishable module in every repo must opt in explicitly — or
it silently stops publishing and a release ships nothing. Silent is the unacceptable word.

Rule: **`defaultBuildTemplateFor*Lib` must FAIL when the vanniktech plugin is applied and
`module.publish` is null.** A half-migrated module is then a build error at configuration time, not a
green release that published nothing. This inverts today's inference — the plugin no longer *grants*
a publication, it is *required by* one.

Scope: four repos on 0.4.62 (DepsKt, KGround, USpek, UPue), plus ~20 carrying older pins that will
meet this whenever they migrate.

### The tripwire is retired (0.4.65)

The plugin-without-`LibPublish` error is gone; `LibPublish` without the plugin still fails. A module
that applies `plugs.VannikPublish` and passes no `LibPublish` is now simply not published.

Why: the tripwire made the plugins {} line carry per-module intent again, and plugins {} lives in
SYNCED regions. `[[Andro App Build Imports and Plugs]]` is shared by `:template-andro-app`, which
publishes (`androVariant = "debug"`), and `:template-full-andro-app`, which does not — no single
copy of the region satisfied both, so the 2026-09-22 template sync broke `template-andro` (the
region is copied from template-full). With the plugin inert, the region carries it unconditionally.

What the tripwire guarded is mostly guarded elsewhere: a repo that really published to Central had
`withCentralPublish = true`, which no longer exists, so it does not compile until it is migrated.
What is left unguarded is a repo that published only LOCALLY, bumps DepsKt, and forgets
`LibPublish` — it would quietly lose local publications. Accepted: nothing irreversible happens.

## Open questions

- Does `LibPublish` need a non-Central *repository* (GitHub Packages, a staging repo), or is
  `toCentral: Boolean` enough with everything else being `publishToMavenLocal`? Boolean until a
  second destination actually exists.
- `androVariant: String?` keeps "*" for all-variants as a magic value. A sealed `AndroVariant`
  (`One(name)` / `All`, with absence carried by the `null`) would make
  `require(!publishAllVariants)` unrepresentable rather than checked — but it would be the only
  sealed type in the model, which is its own inconsistency. Strings until a second caller wants it.
- Three POM fields on `LibPublish` cover `:sourcefun` and `:deps`. If a module ever needs to
  disagree with the repo about more than name/description/artifactId, that is the point to
  reconsider — but as a WIDER `LibPublish`, not as a wrapper around it.
- Do apps ever want `toCentral = true`? A runnable CLI tool published for `cs launch` / JBang is
  the plausible case, and nothing in this design forbids it — an app opts in the same way a lib
  does. The change is only that it must say so.

## Evidence

- USpek `ec35047`, six `kt*sample` modules; control worktree at `8a75b28`.
- After removing `plugs.VannikPublish` from the six samples: `publishToMavenLocal` green,
  `~/.m2/repository/pl/mareklangiewicz` contains exactly the four libs and their platform variants
  (`uspek`, `uspekx`, `uspekx-junit4`, `uspekx-junit5` + `-js`/`-jvm`/`-linuxx64`), 64 `.asc` files,
  no sample artifacts. Per-module "mavenCentral" task counts: all six samples 0, `:uspek` still 10.
- That workaround edits six build files inside region-marked headers. It is a mitigation at the
  consumer, which is exactly why the fix belongs here.
- The release it unblocked: `drelease` run 35329688620 on `v0.0.46` green (the first successful
  USpek release since 0.0.43, and the first ever with `ktandrosample` enabled). On Central: all 12
  library coordinates at 0.0.46, and 404 for every one of the 8 sample coordinates that `v0.0.44`
  would have created.
- The two failed attempts, for the record: run 35321727015 (`v0.0.44`) failed on the enforced
  platform; run 35329132446 (`v0.0.45`) was cancelled. Neither published anything.

## See also

- `lib-details-denesting.md` — the sibling model this note refines. Nothing here argues against it;
  the leak is about which sibling a value lives in, not about siblings.
- `releasing.md` — read before publishing anything.
