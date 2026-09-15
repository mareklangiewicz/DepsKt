# De-nesting LibDetails / LibSettings

Status: **step 1 landed in DepsKt** (branch `lib-denesting`, `src/main/kotlin/deps/Lib.kt`); steps 2-4 not started.
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

**Deliberately deferred.** The prototype's `sdkCompileMinor` field is NOT here. Adding it would make
`Lib.toNested()` lossy and weaken the equivalence tests below, which are the only thing holding the
migration up in this repo. It is additive and belongs in its own step, together with a
`Vers.AndroSdkCompileMinor` const (the prototype's `AndroSdkCompileMinorTMP = 2`).

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


## Sequencing

DepsKt is published and consumed (KGround is on 0.4.25), so this is a breaking change to a
public model. It wants its own branch, its own version, and consumers migrated only once the
new shape is proven — not to be entangled with build-logic experiments in a consumer repo.

Suggested order, now that the shape is known:

1. ~~De-nest the data and add the bundle + factory + the two derivation functions. Keep the nested
   types and an adapter, so nothing breaks yet.~~ **DONE** — see "Step 1 as landed" above.
2. Add sibling entry points with nested shims (no default on the shim). Consumers can move one
   build script at a time.
3. Move the internals to sibling context parameters, deleting `ignoreCompose`,
   `ignoreAndroTarget`, `ignoreAndroConfig` and the `withXxx` reads as each one lands. Keep
   `ignoreAndroPublish`.
4. Drop the nested types and both adapters once no consumer references them.

## Unrelated live bug found on the way

`LibAndroSettings.publishOneVariant` in published **0.4.25** reads
`!publishNoVariants && !publishNoVariants`; the second conjunct should be `!publishAllVariants`. A
lib with `publishVariant = "*"` therefore reports both `publishAllVariants` and `publishOneVariant`,
so `defaultAndroLib` runs both publish paths. Nothing has burned because no KGround module sets
`"*"`. Asserted as a probe in the prototype and fixed in its copy; **still present in DepsKt**. One
character, independent of everything above.
