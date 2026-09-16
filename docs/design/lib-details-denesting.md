# De-nesting LibDetails / LibSettings

Status: **steps 1-3 done** (DepsKt on master + published; KGround ported). Step 4 is blocked — see
"Step 4 is gated on consumers" below. The nested model is **deprecated as of 0.4.27**.
Originally **proven in a prototype**: Identified 2026-09-14 while using
context parameters in KGround's `template-logic` (branch `build-logic-context-params`), then
prototyped there in full as `LibDetailsTMP` / `LibSettingsTMP` / `LibComposeSettingsTMP` /
`LibAndroSettingsTMP` / `LibReposSettingsTMP` + a `LibTMP` bundle (2026-09-15).

Everything below is measured against that prototype, which runs on the REAL `gradle.extLibDetails`
through an adapter, so no `settings.gradle.kts` changed and no DepsKt version was burned. Its gate:
19 probes, KGround assembling (11 modules), all four templates assembling, an android apk produced.

**Read alongside** `~/code/kotlin/KGround/template-logic/migration-status.md` on that branch — it
has every probe, control and compiler error text. This note is the DepsKt-side design; that file is
the evidence.

## The current shape

`LibDetails` → `LibSettings` → { `LibComposeSettings?`, `LibAndroSettings?`, `LibReposSettings` }
— three levels, all in `src/main/kotlin/deps/LibDetails.kt`.

## Symptoms this causes in consumers

Measured in KGround, not hypothesised:

1. **Nested-copy boilerplate in build scripts.** Changing two booleans costs three lines and
   names the root twice:

   ```kotlin
   val settings = gradle.extLibDetails.settings.copy(withJs = false, withLinuxX64 = false)
   val details = gradle.extLibDetails.copy(settings = settings)
   defaultBuildTemplateForBasicMppLib(details) { … }
   ```

   Four of KGround's eleven modules carry exactly this, verbatim. A `settings = { copy(…) }`
   lambda on the entry point removes it for *one* level, but a consumer reaching the third
   level gets something worse: `settings = { copy(compose = compose!!.copy(…)) }`.

2. **Two context arguments for one logical thing.** Every entry point in `template-logic`
   opens with `context(details, details.settings)`, because supplying the outer value does
   not supply the inner one.

3. **Helpers reach through the tree.** `addRepos` takes `context(settings: LibSettings)` and
   immediately does `with(settings.repos)`; `androDefault` does `settings.andro!!`.

## What would actually help — siblings, NOT one flat class

Flattening into a single ~60-field `LibDetails` would make this worse: one giant context
argument, `copy()` for everything, and no scoping. The useful change is to keep the five
types and stop making them fields of each other, so they can be supplied independently:

```kotlin
context(details: LibDetails, settings: LibSettings, repos: LibReposSettings)
```

Each is then adjusted with a single flat `copy` at the one place that cares, and each flows
down the call tree on its own.

**Consumers still want one value to pass around**, so the prototype adds a bundle,
`LibTMP(details, settings, repos, compose?, andro?)` — carried together, but NOT nested: nothing
in it derives from anything in it, no member reaches through another, and the nullable two are
consumed by opening a scope. One build script argument, five independently-openable scopes.

## The strongest reason: presence becomes scope

`compose: LibComposeSettings?` and `andro: LibAndroSettings?` are nullable, so the nesting
encodes *presence*. As sibling context parameters, a function that needs android declares
`context(andro: LibAndroSettings)` and cannot be called outside an android scope at all.

**Confirmed, and it is a compile error, not a test.** A call with no andro scope open fails with:

```
No context argument for 'andro: LibAndroSettingsTMP' found.
```

Negative control: an open *compose* scope does not satisfy it — same error. (These claims are not
probes; a probe task cannot assert a compile error, so each was proven by construction with the
error text recorded in `migration-status.md`.)

### Word the win carefully: the checks MOVE, they do not vanish

"All the `!!`s disappear" would be wrong, and the honest version is more persuasive. Something must
still turn a nullable into a scope at the edge, because a build script may or may not have
configured android. What changes is that it happens ONCE per entry point:

```kotlin
val andro = lib.andro ?: error("No andro settings.")   // the only check
context(lib.settings, andro) { defaultAndroDeps(); defaultAndroTestDeps() }
```

Measured across the andro family: **6 `?: error` + 1 `!!` became 3 `?: error` + 0 `!!`** — one per
place where "details that may or may not have android" genuinely arrives from outside.

### Scopes accumulate — the compositional half of the argument

Once an entry point opens `context(details, settings)` at the top, every helper below names only
what it ADDS:

```kotlin
context(lib.details, andro) { androDefault() }   // before, at each call site
context(andro) { androDefault() }                // after, with details already open
```

Four more call sites shortened the same way. The nested model cannot do this: `details.settings` is
reached through a field, so it must be spelled out again at every level.

### The `ignoreXxx` story — better AND worse than first stated

The original guess was "four booleans, plausibly all deleted". What actually happened:

- **Three died outright** (`ignoreCompose`, `ignoreAndroTarget`, `ignoreAndroConfig`). They existed
  so code could be injected into scripts where compose/android plugins are not applied at all.
  `LibSettingsTMP` has no `compose` and no `andro` to begin with, so the body cannot name either
  and the caller opts out by not opening the scope.
- **One was renamed and then mostly dissolved.** `composeConfiguredByMpp` became a routing question
  answered by whether the caller opens a compose scope; it survives only as `configureComposeAndro`
  on `defaultAndroLib` / `defaultAndroApp`, where a caller that knows both facts states its
  decision once.
- **One stays: `ignoreAndroPublish`.** It constrains the CONTENT of the andro settings
  (`publishNoVariants`), not their presence, so it belongs where the content is visible. Presence-
  as-scope does nothing for it.

**The `withXxx` READS die too, not just the parameters.** `if (details.settings.withAndro)` appeared
three times in one template around code that then re-derived the settings; it is now one
`lib.andro?.let { andro -> … }`, where the guard and the value arrive together. Same for
`settings.withCompose`. Count these when tallying the win.

### An argument the note was missing: duplicated `require`s

Three `require`s inside `allDefault` were **verbatim duplicates** of the ones in its entry point —
the helper re-checked what its only caller had already checked, because the nested value let it.
De-nesting deleted the inner copies with nothing to replace them. Redundant validation is a
symptom of a type that carries more than the callee needs.

## The hard part — interdependent defaults — is SMALLER than feared

The current defaults derive from each other *because* they are nested:

- `LibSettings.compose` computes ten flags from `withJvm` / `withJs` / `withTestJUnit4/5`
- `LibSettings.repos` derives `withKotlinxHtml` from `LibSettings.withKotlinxHtml`
- `withJvmVer` and `withTestJUnit5` derive from `withJvm`

Measured: **only the first two actually cross object boundaries.** `withJvmVer` and
`withTestJUnit5` derive within ONE declaration and need no change at all. So the "actual design
work" is two named functions, not a pervasive rewrite:

```kotlin
context(settings: LibSettingsTMP) fun defaultComposeSettingsTMP(): LibComposeSettingsTMP
context(settings: LibSettingsTMP) fun defaultReposSettingsTMP(): LibReposSettingsTMP
```

Verified equal to the constructor defaults across all 8 combinations of
`withJvm` × `withJs` × `withTestJUnit4`. This is a gain, not a workaround: the rule is now named,
callable and overridable at one place, instead of firing only when an argument is omitted.

A factory (`libTMP(details, settings, withCompose, withAndro, …)`) applies those derivations for
whatever the caller does not supply, and `withCompose` / `withAndro` say whether those scopes exist
at all — the one thing the nested model expressed as `null`.

## Two mechanical traps the DepsKt migration WILL hit

1. **A context parameter does not shadow a receiver, but it does occupy its own NAME.** `addRepos`
   cannot call its context parameter `repos`, because `maven(repos.kotlinx)` refers to a top-level
   DepsKt object of that name; it has to be `reposSettings`. This bites in DepsKt specifically, and
   it recurred inside the prototype without being looked for: `allDefaultSourceSetsForCompose`
   already had a local `compose` (the Gradle `ComposeExtension`), so that had to become `composeExt`.
   Expect one rename per collision; they are silent until they are not.

2. **Two fully-defaulted overloads of the same entry point are ambiguous.** While both models
   coexist, the nested overload must LOSE its default (`details: LibDetails` with no
   `= gradle.extLibDetails`). The default belongs to the sibling form, because that is the one
   build scripts call.

## The two halves are separable — ship the cheap one first

This is the most actionable finding. The copy-dance win (symptom 1) is **pure data shape**: it
reaches build scripts through a plain value and a plain entry-point overload, with no context
parameters at the call site, no language-version change and no Gradle change. Build scripts are
compiled flagless (Gradle pins script language version), and they do not need the flag:

```kotlin
// nested: two statements, root named twice
val settings = gradle.extLibDetails.settings.copy(withJs = false, withLinuxX64 = false)
defaultBuildTemplateForBasicMppLib(gradle.extLibDetails.copy(settings = settings)) { … }

// sibling: one statement
defaultBuildTemplateForBasicMppLib(libTMP { it.copy(withJs = false, withLinuxX64 = false) }) { … }
```

The presence-as-scope win needs context parameters, but only INSIDE the library, behind that
boundary. **A DepsKt branch can therefore ship the data de-nesting alone**, and the half consumers
see is the cheap half.

## The shape of the migration itself

The prototype ended up demonstrating it: every entry point takes the bundle, opens
`context(details, settings)` ONCE at the top, and keeps a four-line compatibility shim for callers
still holding a nested `LibDetails`:

```kotlin
fun Project.defaultBuildTemplateForBasicMppLib(
  lib: LibTMP = gradle.extLibTMP, …
): Unit = context(lib.details, lib.settings) { … }

/** shim: un-nest ONCE at the top, siblings below. No default for `details` (trap 2 above). */
fun Project.defaultBuildTemplateForBasicMppLib(details: LibDetails, …): Unit =
  defaultBuildTemplateForBasicMppLib(lib = details.toTMP(), …)
```

`compose` and `andro` stay narrow, opened at their boundary inside the body — which is precisely
what makes the boundary visible.

The prototype's own distance meter was `toNested()`, which re-nests siblings to feed still-nested
internals. It now has **zero** production callers; it survives only in probes, where the nested
model is the control being compared against. When DepsKt de-nests for real, the control disappears
and both adapters go with it.

## Step 1 as landed (2026-09-15)

`src/main/kotlin/deps/Lib.kt`, branch `lib-denesting`. Data shape only: no entry point moved, no
context parameter used, nothing in the nested model changed. `LibDetails` and friends are untouched
and still the only thing consumers see.

**Names.** The siblings could not reuse `LibDetails`/`LibSettings`/… while those still exist, so
they got distinct short names, chosen to survive step 4 without a second rename:

| nested (unchanged) | sibling |
| --- | --- |
| `LibDetails` minus `settings` | `LibInfo` |
| `LibSettings` minus `compose`/`andro`/`repos` | `LibFlags` |
| `LibComposeSettings` | `LibCompose` |
| `LibAndroSettings` | `LibAndro` |
| `LibReposSettings` | `LibRepos` |
| — | `Lib` (the bundle) |

They are also shorter exactly where they will be typed most: `context(info: LibInfo, flags:
LibFlags, repos: LibRepos)`. Consumers therefore migrate ONCE, not once to a suffix and once away
from it.

**Deferred then DONE in 0.4.29.** The prototype's `sdkCompileMinor` field was left out of step 1,
because adding it to one side only would make `Lib.toNested()` lossy and weaken the equivalence
tests below, which are the only thing holding the migration up in this repo. It landed as its own
additive step: `Vers.AndroSdkCompileMinor = 2` (where a version belongs), plus a `sdkCompileMinor`
field on BOTH `LibAndro` and the nested `LibAndroSettings`, mapped in both adapters — so they stay
total and the equivalence tests keep their meaning. templatefun's `AndroSdkCompileMinor` const is
now a deprecated alias for the `Vers` one; the templates read `andro.sdkCompileMinor`, so the minor
level is per-lib and overridable instead of one const every template shared.

The round-trip fixtures gained a **non-default** `sdkCompileMinor = 7` case on each side. Without
it both sides would have defaulted to 2 and the round trip would have passed even with the field
dropped from an adapter entirely. Validated by deleting one mapping direction: 2 tests fail, and
pass again when restored.

**Derivations take a plain parameter.** `defaultLibCompose(flags)` / `defaultLibRepos(flags)` rather
than `context(flags: LibFlags)`, because DepsKt compiles without `-Xcontext-parameters` and step 1
must not require it. They become context parameters in step 3, which is a signature change inside
the library only.

### What plays the role of KGround's 19 probes

DepsKt has its own JVM test source set, so the claims are plain JUnit5 tests
(`src/test/kotlin/LibDenestingTest.kt`, 10 tests) instead of Gradle probes — they run in seconds and
need no template build:

- both derivations reproduce the nested constructor defaults across the 8 combinations of
  `withJvm` × `withJs` × `withTestJUnit4`, plus both values of `withKotlinxHtml` for repos;
- the derivations demonstrably VARY with their inputs (without this the equality above proves
  nothing — see `derivationsDependOnTheirInputs`);
- round trips are lossless BOTH ways, over compose-absent, andro-present and preview-SDK variants;
- `lib()` reproduces the nested defaults for a default lib, and encodes presence via
  `withCompose`/`withAndro` with an explicitly supplied sibling winning over the switch;
- the `publishVariant` truth table, asserted against both models.

**The harness was validated, not just run.** A planted defect in `defaultLibCompose` failed 2 tests,
and a planted dropped field in `LibAndro.toNested()` failed both round-trip tests; both markers were
reverted and the suite is green.

### Still true after step 1

Nothing consumes the new shape yet. `gradle.extLibDetails` still holds a nested `LibDetails`, and
there is no `extLib`; wiring the bundle into ext storage and the entry points is step 2.


## Step 2 as landed (2026-09-15)

Entry points and ext storage, same branch. Two files: `utils/Utils.kt` and `defaults/Defaults.kt`.

### One stored value, two views

The obvious move would have been a second ext entry holding a `Lib` next to the `LibDetails` one.
That is two representations of one fact, and they drift. Instead `extLib` IS the stored entry (key
`"Lib"`, holding a [Lib]) and `extLibDetails` converts on the way in and out:

```kotlin
var ExtensionAware.extLib: Lib          // the stored value
var ExtensionAware.extLibDetails: LibDetails
  get() = extLib.toNested()
  set(value) { extLib = value.toLib() }
```

A set-then-get therefore returns an EQUAL, not identical, value — which is only safe because the
round trip is lossless, and that is exactly what step 1's tests assert. The two halves hold each
other up. Same shape for `rootExtLib` / `findExtLib`, with the nested spellings kept as views.

### Trap 2 confirmed in practice

`defaultGroupAndVerAndDescription` now has the sibling overload WITH the default
(`lib: Lib = rootExtLib`) and the nested shim WITHOUT one. A no-argument call resolves to the
sibling overload and reads root ext; both overloads reach the same project state (asserted).

### The constraint that decides what step 2 could NOT touch

`build.gradle.kts` still carries the `[[Kotlin Module Build Template]]` region — `addRepos`,
`defaultPOM`, `defaultPublishing`, `defaultBuildTemplateForRootProject` — and that region is both
compiled as part of this build and synced into consumer build scripts.

**DepsKt's own build script compiles against PUBLISHED DepsKt** (`settings.gradle.kts` has
`depsInclude = false`, and the build logs `DepsSettingsPlugin 0.4.24`), so it cannot name `Lib`
until a version carrying `Lib` is published. The template region therefore migrates AFTER a publish,
not before — and `addRepos` is where trap 1 will bite, because its parameter cannot be called
`repos` (that name is a top-level DepsKt object it dereferences as `repos.kotlinx`); `libRepos` is
the spelling to use.

### Tests

`src/test/kotlin/LibExtStorageTest.kt`, 9 tests, driving real Gradle projects via `ProjectBuilder`
(no daemon, no template build). They cover storage round trips through BOTH accessors, the
"only one stored value" claim, hierarchy walking in `findExtLib`, the not-found failure, and the
shim agreeing with the sibling entry point.

Validated the same way as step 1: replacing the `extLibDetails` view with a parallel ext entry
failed exactly the three tests that assert it is a view. Suite green at **21 tests**.


## Where steps 3 and 4 actually live (found while doing step 2)

Step 3 was written as "move the internals to sibling context parameters, deleting `ignoreCompose`,
`ignoreAndroTarget`, `ignoreAndroConfig` and the `withXxx` reads". **None of those exist in DepsKt.**
A search across `src/` and `build.gradle.kts` finds no `ignoreXxx` at all, and the only `withCompose`
/ `withAndro` are the two derived properties on `LibSettings` itself. Every helper the prototype
rewrote — `addRepos` reaching through `settings.repos`, `androDefault` doing `settings.andro!!`, the
entry points opening `context(details, details.settings)` — lives in KGround's `template-logic`, not
here.

So the remaining work splits, and neither half is blocked on design any more:

- **Step 3 is consumer-side**, and the prototype has already done it once. It is a port from
  `LibDetailsTMP`/`LibTMP` to `Lib`/`LibInfo`/`LibFlags`/… on KGround's branch, against a DepsKt
  that now ships the real types. Its one boundary rule stays: build scripts are compiled flagless,
  so context parameters may appear only INSIDE `template-logic`.
- **Step 4 is blocked on consumers**, by definition — the nested types and both adapters go once
  nothing references them.

**The thing that unblocks both is a publish.** DepsKt's own `build.gradle.kts` and every consumer
compile against a published version, so `Lib` does not exist for them until one ships. The
remaining DepsKt-side work — the `[[Kotlin Module Build Template]]` region — is downstream of that
publish, not of more design.


## The template region, migrated (2026-09-15, after publishing 0.4.26)

With 0.4.26 on the portal, `settings.gradle.kts` pins `0.4.26` and the
`[[Kotlin Module Build Template]]` region in `build.gradle.kts` moved to the sibling model —
`defaultBuildTemplateForRootProject`, `addRepos`, `defaultPOM`, `defaultPublishing`, and DepsKt's
own call site. No shims were added: the region is copied wholesale into each consumer build script,
so every copy is self-contained and there is no cross-version caller to shim for.

Verified by the plugin announcing itself — `DepsSettingsPlugin 0.4.26 apply in project DepsKt` —
so the build really resolved the new artifact rather than a cached 0.4.25.

**Trap 1 hit twice, and the second one was NOT on the list.** `addRepos`'s parameter cannot be
`repos` (predicted; it is `libRepos`). But `defaultPOM` collided too, and differently: writing
`with(lib.info)` made `name` and `description` resolve to `LibInfo`'s fields instead of `MavenPom`'s
properties, giving "Unresolved reference ... receiver type mismatch". It stays fully qualified.

Generalise the trap, because the note had it too narrow: **the sibling types put five new names into
scope, and any receiver that already has those names will silently capture them.** `name`,
`description`, `version`, `group` and `repos` are all common Gradle receiver members. Prefer explicit
`lib.info.x` inside a Gradle receiver block; reach for `with(..)` only where the receiver has no
overlapping members.

### The build-script win, at DepsKt's own call site

```kotlin
// before: presence expressed as null, two levels
val details = myLibDetails(name = "DepsKt", ..., settings = LibSettings(withJs = false, compose = null))

// after: presence stated as presence
val myLib = lib(info = myLibInfo(name = "DepsKt", ...), flags = LibFlags(withJs = false), withCompose = false)
```

(Named `myLib`, not `lib`, so the local does not shadow the `lib(..)` factory that builds it.)


## Step 4 is gated on consumers, not on effort (2026-09-15)

Step 4 says "drop the nested types and both adapters once no consumer references them". Measured,
that precondition is nowhere near met: **13 of Marek's other repos plus KGround** reference
`myLibDetails` / `LibSettings(` / `extLibDetails`, and every one of them is pinned to DepsKt
0.4.25. DepsKt is also published publicly, so the nested model is not private API.

So 0.4.27 does the reversible half instead: **the nested model is `@Deprecated` at WARNING level**,
with `ReplaceWith` where the replacement is exact (`LibComposeSettings` → `LibCompose`,
`LibAndroSettings` → `LibAndro`, `LibReposSettings` → `LibRepos`, `extLibDetails` → `extLib`,
`rootExtLibDetails`, `findExtLibDetails`, and the nested `defaultGroupAndVerAndDescription` shim).

`LibDetails` and `LibSettings` get a message but **no `ReplaceWith`**: they do not map onto one
sibling type, so a quick-fix would be a lie. The message names the real move instead —
`lib(info = myLibInfo(..), flags = LibFlags(..))`, or `.toLib()` on a value you already hold.

**The adapters are deliberately NOT deprecated.** `toLib` / `toNested` / `toFlags` / `toSibling`
are how a consumer gets from the old model to the new one; warning on them would fight the
migration they exist to enable.

Nothing breaks: warnings appear only when a build deliberately bumps to 0.4.27. Verified by
compiling a throwaway file with no `@file:Suppress` and reading the warnings back — the texts do
arrive, with the guidance attached. Inside DepsKt the nested model is still named by the adapters
and by the tests that use it as a control, so those files carry `@file:Suppress("DEPRECATION")`:
a measured **zero** deprecation warnings from our own sources.

### What step 4 still needs

1. The 13 repos migrated off `myLibDetails` (the `ReplaceWith` quick-fixes make most of it
   mechanical; `LibDetails`/`LibSettings` are the hand-written part).
2. DONE 2026-09-16: the probes are retired and `probe-logic/` is deleted. The evidence had served
   its purpose — the experiment shipped in 0.4.29 — and the three that used the nested model as a
   control (`probeCopyDance`, `probeAdapterFidelity`, `probePublishVariantAgreement`) were
   duplicates of stricter tests here rather than evidence. So this precondition is closed, and
   step 4 is now gated on items 1 and 3 alone.
3. DepsKt's own `LibDenestingTest` likewise: its round-trip and derivation-equivalence tests are
   defined against the nested model. When it goes, they go, and what replaces them is a smaller
   suite about `Lib` alone.
4. DONE in 0.4.29: `defaultGroupAndVerAndDescription` lost its `lib: Lib = rootExtLib` default.
   The default was dead, not load-bearing — a grep over all 17 local consumer repos found ZERO
   no-arg call sites; even the unmigrated ones pass explicitly, from their own local root build
   template (`defaultGroupAndVerAndDescription(rootExtLibDetails)` or `(it)`). `rootExtLib` and
   the deprecated `rootExtLibDetails` stay as storage, since that template still writes them; only
   the parameter default that reached for ambient state is gone. Direction of travel: data moves
   off exts and onto context parameters; the one ext on `gradle` is a compromise until Gradle
   supports them properly.


## Sequencing

DepsKt is published and consumed (KGround is on 0.4.25), so this is a breaking change to a
public model. It wants its own branch, its own version, and consumers migrated only once the
new shape is proven — not to be entangled with build-logic experiments in a consumer repo.

Suggested order, now that the shape is known:

1. ~~De-nest the data and add the bundle + factory + the two derivation functions. Keep the nested
   types and an adapter, so nothing breaks yet.~~ **DONE** — see "Step 1 as landed" above.
2. ~~Add sibling entry points with nested shims (no default on the shim). Consumers can move one
   build script at a time.~~ **DONE for the library entry points** — see "Step 2 as landed" above.
   The `build.gradle.kts` template region is the remainder, and it is blocked on a publish.
3. Move the internals to sibling context parameters, deleting `ignoreCompose`,
   `ignoreAndroTarget`, `ignoreAndroConfig` and the `withXxx` reads as each one lands. Keep
   `ignoreAndroPublish`. **This happens in KGround, not here** — see the section above.
4. Drop the nested types and both adapters once no consumer references them. **Deprecated in
   0.4.27; the probes that used the nested model as their control are retired (2026-09-16), so
   removal is now gated on the remaining repos and on this repo's own control tests.** See
   "Step 4 is gated on consumers" above.

## Unrelated live bug found on the way

`LibAndroSettings.publishOneVariant` in published **0.4.25** reads
`!publishNoVariants && !publishNoVariants`; the second conjunct should be `!publishAllVariants`. A
lib with `publishVariant = "*"` therefore reports both `publishAllVariants` and `publishOneVariant`,
so `defaultAndroLib` runs both publish paths. Nothing has burned because no KGround module sets
`"*"`. Asserted as a probe in the prototype and fixed in its copy; **still present in DepsKt**. One
character, independent of everything above.


## DepsKt as a multi-project build (2026-09-15)

Phase 1 moved KGround's `template-logic` into this repo as `templatefun`, a subproject of a root
that was *itself* the published library. That is why it had to depend on `project(":")`. With
SourceFun due to move in the same way, the shape was wrong before it had a third occupant, so the
root is now an empty aggregator and everything real is a sibling:

```
DepsKt/            root: aggregates, publishes nothing, has no group
  deps/            the published DepsKt artifact (was ./src + ./build.gradle.kts)
  templatefun/     the reusable build templates, plugin id pl.mareklangiewicz.templatefun
```

The `lib(..)` definition moved to `settings.gradle.kts` as `gradle.extLib`, which is what every
consumer repo already does — DepsKt was the odd one out only because it had nowhere else to put it.

### The trap: a composite build matches on project coordinates, not publication coordinates

This is the part worth reading before doing the same thing to another repo, and both halves of it
were found by running the build, not by reading it.

`defaultPublishing` derived `artifactId` from `project.name`. Under `deps/` that would have
published `pl.mareklangiewicz.deps:deps`, and **nothing would have errored**: every consumer's
`includeBuild("../DepsKt")` substitutes by `group:name`, so `depsInclude = true` would simply have
stopped substituting and silently resolved the published jar instead. `defaultPublishing` therefore
took an `artifactId` parameter (defaulting to `project.name`, so existing copies of the region are
unaffected) and `:deps` pins it to `DepsKt`.

Only the copy in `deps/build.gradle.kts` grew that parameter — **templatefun's `defaultPublishing`
still hardcodes `artifactId = name`**, so any repo whose directory name differs from its published
artifactId cannot use it yet.

That fixed the publication and broke the composite, in the opposite direction:

```
> No matching variant of project ':DepsKt' was found ... - No variants exist.
```

Gradle bound `pl.mareklangiewicz.deps:DepsKt` to the **root** project — which had that group (from
`defaultGroupAndVerAndDescription`) and that name (`rootProject.name`), and no sources. So:

- The root does NOT call `defaultGroupAndVerAndDescription`. An aggregator that publishes nothing
  must not claim a coordinate something else publishes.
- `:deps` and `:templatefun` each set their own group and version. For `:templatefun` this is not
  optional decoration: with only publication coordinates and no `project.group`, substitution did
  not find it at all (`Could not resolve pl.mareklangiewicz.deps:templatefun`). Publication
  coordinates are a fallback; `group:name` is what is actually matched.

Generalised: **in a composite build, a project's identity is `project.group:project.name`.** Keep
those equal to what the project publishes, and reach for an `artifactId` override only where the
directory layout has to differ from the coordinate — then check both sides, because fixing one can
break the other without a word.

Verified by generating the POMs rather than by reading the build scripts:
`pl.mareklangiewicz.deps:DepsKt:0.4.27` with both plugin markers pointing at it, unchanged;
`pl.mareklangiewicz.deps:templatefun:0.4.27` behind the marker for the new plugin id, itself
depending on `DepsKt`. Control: dropping the `artifactId` pin does produce `<artifactId>deps`.

### `Project.projectPath` returned `rootDir`

Found while moving the maintenance tasks. It now returns `projectDir`, which is what the name says;
`rootProjectPath` goes through `rootProject` so its meaning is unchanged. Harmless while this repo
had exactly one project, wrong the moment it did not.


## Phase 2: the KGround cutover, and the three decisions it was blocked on

### Decision 1 — plugin id: `pl.mareklangiewicz.templatefun`

`templatefun` compiled and nothing else: no id, no publishing. It reaches a build script the same
way KGround's local `my-convention` did — a precompiled script plugin
(`templatefun/src/main/kotlin/pl.mareklangiewicz.templatefun.gradle.kts`) whose only job is to put
templatefun and the `Lib` model on that script's compile classpath. It configures nothing on apply,
deliberately: these repos are moving *away* from conventions applied behind a script's back, and
the templates are ordinary functions a script calls when it wants them.

So each KGround build script changed by exactly two lines:

```kotlin
import pl.mareklangiewicz.templatefun.*     // was ...templatelogic.*
plugins { id("pl.mareklangiewicz.templatefun") }   // was id("my-convention")
```

25 build scripts, 5 settings files. The package rename made every missed site a compile error,
which was the point of renaming.

### Decision 2 — cutover order: composite first, publish after

All five `settings.gradle.kts` files flipped `depsInclude` from a hardcoded `false` to
`depsDir.exists()`, so KGround and the four templates build against the local DepsKt. Nothing is
published until the templates are green; then `publishPlugins`, then pin a version.

**The four templates had `depsDir = File(rootDir, "../DepsKt")`, one level too shallow** — they sit
one directory deeper than the root, and the `<~~` adjuster region in each of them says as much
(`~~>"../../DepsKt"<~~`). Nobody had noticed, because with `depsInclude` hardcoded `false` the wrong
path was never evaluated: a dead switch hides a broken value. Fixed to `../../DepsKt`.

### Decision 3 — the probes: kept, in a minimal `probe-logic`

Deleting `template-logic` removed the only module the probes could live in: they assert what a
`.gradle.kts` compiled WITHOUT `-Xcontext-parameters` can reach in a module compiled WITH it, so
they have to be on a build script's classpath, which means an included build. templatefun cannot be
that place — it is published API, and these are branch-local evidence about an experiment.

`KGround/probe-logic/` is the smallest module that can host them: `ProbeFuns.kt`, a build script,
an empty `my-probes` precompiled plugin, and nothing else. **19/19 probes pass** against templatefun
via the composite.

Two things that cost time and will again:

- `includeBuild` alone does NOT put an included build's classes on a build script's classpath. It
  makes its plugins and coordinates *resolvable*. Something must still apply a plugin from it or
  depend on it — hence the otherwise-empty `my-probes`.
- `probe-logic` depends on templatefun only for `AndroSdkCompileMinor`, which `probeSdkFull`
  asserts against. Inlining the number would have removed the dependency and the probe's meaning
  with it: it would keep passing after the templates moved on.

`gate.sh`'s `compile` step is now `:probe-logic:compileKotlin`, which pulls templatefun through the
substitution — so the composite binding is itself the first thing the gate checks.

**One known divergence, deliberately left in.** `id("my-probes")` had to go inside
`kgroundx-experiments`'s `[[Basic MPP Lib Build Imports and Plugs]]` region: plugin classes reach a
build script's compile classpath only through its `plugins { }` block, and that block is inside the
region. So that one file's copy of the region no longer matches the other eleven or the canonical
`.kts.tmpl`. `tryInjectMyTemplatesToProject` will therefore offer to overwrite it — the prompt is
interactive, so nothing is lost silently, but **say no**, or the probes stop compiling. The
divergence disappears when the probes retire; it is the price of keeping them, and it is the one
cost of decision 3 that was not visible when the decision was made.


## DepsKt applies its own templatefun, and :deps becomes :DepsKt (2026-09-16)

Reverses the "declined for now" on applying templatefun to this repo. The reason it was declined —
templatefun's `defaultPublishing` hardcodes `artifactId = name` while `:deps` needed it pinned to
`DepsKt` — turned out to be a problem with the NAME, not with templatefun.

### The structural fact that shaped the whole thing

**A sibling subproject cannot supply a plugin to another subproject's build script.** Build-script
plugins resolve through `pluginManagement` — buildSrc, included builds, repositories — and
`include(":templatefun")` only makes it a project of this build. So "DepsKt applies its own
templatefun" has exactly one meaning: it applies the PUBLISHED one, like any other consumer.

That is not circular, and the root build script's old comment saying it was has been corrected:
published templatefun 0.4.29 depends on published DepsKt 0.4.29, a finished artifact, not on this
build. The circularity only ever applied to the local project.

### The fix: name the project what it publishes

```kotlin
include(":deps")
project(":deps").name = "DepsKt"
```

`deps/build.gradle.kts` therefore drops `defaultPublishing(myLib, artifactId = "DepsKt")` for
templatefun's parameterless one, and the 113-line `[[Kotlin Module Build Template]]` region
(lines 152-264) is deleted outright.

This is what the "composite build matches on project coordinates" section above already prescribed:
*a project's identity is `project.group:project.name`*. Naming the project after its DIRECTORY and
then pinning `artifactId` in `defaultPublishing` stated the same fact twice, in two places that can
drift — and the drift is silent, because a substitution that stops matching just resolves the
published jar instead of erroring.

The alternative was to add an `artifactId` parameter to templatefun's copy and publish 0.4.30 to
get it. Rejected: it parameterizes around a name we chose rather than fixing it, and it burns a
version.

**The cost, stated so nobody rediscovers it:** the project PATH follows the name. This project is
`:DepsKt` while its directory stays `deps/`, and `templatefun/build.gradle.kts` depends on
`project(":DepsKt")`. Under a consumer's composite the path reads `:DepsKt:DepsKt`.

**It also raises the stakes on the root having no group.** Two projects in this build are now named
`DepsKt`; the only thing keeping their identities apart is that the root has no group. Giving the
root one would make them collide exactly, and silently.

### Verified

- `./gradlew build` and `:DepsKt:test` green on `--rerun-tasks`.
- All five publications' coordinates byte-identical to shipped 0.4.29:
  `pl.mareklangiewicz.deps:DepsKt`, `:templatefun`, and the three plugin markers. Both deps markers
  still resolve to `pl.mareklangiewicz.deps:DepsKt:0.4.29`.
- **The substitution control was validated, not assumed.** A green build proves nothing about which
  DepsKt it used, so `dependencyInsight` was read from KGround with `depsInclude` on:
  `pl.mareklangiewicz.deps:DepsKt:0.4.29 -> project ':DepsKt:DepsKt'`. KGround's
  `settings.gradle.kts` was restored afterwards; that flip is not committed.
- The wrong-artifactId failure mode was MEASURED before fixing it, not reasoned about: with
  templatefun's `defaultPublishing` and the project still named `deps`, the POM really did say
  `pl.mareklangiewicz.deps:deps:0.4.29`.

### What this does to KGround's probes

`deps/build.gradle.kts` now reaches templatefun's
`context(info: LibInfo, flags: LibFlags) fun Project.defaultPublishing()` through the flattened
coercion, from a script compiled without `-Xcontext-parameters`:

```kotlin
val tfDefaultPublishing: (LibInfo, LibFlags, Project) -> Unit = Project::defaultPublishing
tfDefaultPublishing(myLib.info, myLib.flags, project)
```

That is the claim probes 7/14/15 assert, and probes 1-3 establish the preconditions for — now
exercised by a production build on every run instead of by branch-local evidence in an experiment
that has shipped. Those ten "category A" probes can retire. Separately, `probeAdapterFidelity`,
`probeCopyDance`, `probeDerivedDefaults` and `probePublishVariantAgreement` are already covered —
more strictly — by `LibDenestingTest`, so they are duplicates rather than evidence.

**Do not lose `probe-logic`'s templatefun dependency when retiring them.** `gate.sh`'s compile step
is `:probe-logic:compileKotlin`, and that dependency is what makes the step exercise the composite
substitution at all.

## Simplification: the composite bonus stops shaping core code; `appId` becomes `id` (2026-09-16)

Two changes, one motivation. `includeBuild("../DepsKt")` is a convenience that has never actually
been switched on (`depsInclude` is literally `false` in all 20 consumer repos), and the previous
session let it dictate a project's NAME. Marek's call: the bonus keeps working as an escape hatch,
but it stops paying for itself in core code.

### The project is `:deps` again; artifactId is pinned

Reverted from the previous section's "name the project what it publishes". The rename bought
exactly one thing — composite substitution matching `pl.mareklangiewicz.deps:DepsKt` — and charged
for it with a project whose path (`:DepsKt`) and directory (`deps/`) disagree, two projects named
`DepsKt` in one build kept apart only by the root having no group, and a `templatefun` dependency
on `project(":DepsKt")`.

So `deps/build.gradle.kts` pins the coordinate again, AFTER calling templatefun's
`defaultPublishing` (both call `coordinates(..)`; the last call wins):

```kotlin
mavenPublishing { coordinates(myLib.info.group, "DepsKt", myLib.info.version.str) }
```

Note what this deliberately does NOT do: it does not add an `artifactId` parameter to templatefun's
`defaultPublishing`. That would have needed a published 0.4.30 before `deps/build.gradle.kts`
(which applies the PUBLISHED templatefun) could use it — a bootstrap step bought for one call site.

**Consequence, stated so nobody re-derives it:** with the project named `deps` again, a consumer's
`includeBuild("../DepsKt")` no longer substitutes the DepsKt artifact — only `templatefun`, whose
project name does match. Gradle degrades to the published jar silently, as it always does when a
substitution stops matching. That is accepted, not overlooked.

### Two dead helpers deleted

`Settings.includeAndSubstituteBuild` (DepsKt `utils/Utils.kt`) and `Project.setMyWeirdSubstitutions`
(templatefun) both existed to wire local composites. Neither had a single call site anywhere in
DepsKt or KGround — checked before deleting, not assumed.

### `LibInfo.appId` -> `LibInfo.id`, defaulting to `namespace`

One generic identity slot instead of an app-only one: an android `applicationId`, a bundle id, a
plugin id are the same kind of thing, and the field should not be named as if only apps have one.
The nested `LibDetails.appId` is untouched — it is deleted in step 4 anyway — and the adapters map
`id <-> appId`, so both directions stay total.

The DEFAULT changes with the name: `namespace`, not `"$namespace.app"`. Evidence that the suffix
was a convention rather than a rule: kokpit667 already overrides it with the comment "without
typical `.app` suffix because I published it like that already". A repo that does want a separate
app id still writes one; it is one string.

`LibDenestingTest.factoryMatchesTheNestedDefaultsForADefaultLibExceptId` asserts this divergence
head-on — the old value, the new value, and then full equality after normalising that one field —
so it is a stated behaviour change, not a loosened test.

### Verified

- `./gradlew build` green, 25 tests.
- All five publication coordinates regenerated from scratch (POMs deleted first) and still
  byte-identical to shipped 0.4.29: `pl.mareklangiewicz.deps:DepsKt`, `:templatefun`, the three
  plugin markers.
