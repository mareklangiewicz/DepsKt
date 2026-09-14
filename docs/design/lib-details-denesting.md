# De-nesting LibDetails / LibSettings

Status: **proposed, not started.** Identified 2026-09-14 while using context parameters in
KGround's `template-logic` (branch `build-logic-context-params`). This is the DepsKt-side
work that experiment ran into; the KGround branch records the symptoms.

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

## The strongest reason: presence becomes scope

`compose: LibComposeSettings?` and `andro: LibAndroSettings?` are nullable, so the nesting
encodes *presence*. That is why consumers see `settings.andro!!`, and very likely why
`defaultBuildTemplateForBasicMppLib` carries four `ignoreCompose` / `ignoreAndroTarget` /
`ignoreAndroConfig` / `ignoreAndroPublish` booleans — runtime `require`s standing in for
"this module has android configured".

As sibling context parameters, a function that needs android declares
`context(andro: LibAndroSettings)` and cannot be called outside an android scope at all.
That is a compile-time check replacing both the `!!`s and, plausibly, the whole `ignoreXxx`
family. This is a bigger prize than the copy-dance boilerplate and should drive the design.

## The hard part — interdependent defaults

The current defaults derive from each other *because* they are nested:

- `LibSettings.compose` computes ten flags from `withJvm` / `withJs` / `withTestJUnit4/5`
- `LibSettings.repos` derives `withKotlinxHtml` from `LibSettings.withKotlinxHtml`
- `withJvmVer` and `withTestJUnit5` derive from `withJvm`

Siblings cannot default off each other — a default argument only sees earlier parameters of
the same declaration. De-nesting therefore means replacing those derivations with explicit
factory functions (e.g. `fun libSettings(withJvm: Boolean = true, …): Triple<…>` or a small
builder returning the whole sibling set). **This is the actual design work**, and the reason
this is not a mechanical refactor.

## Sequencing

DepsKt is published and consumed (KGround is on 0.4.25), so this is a breaking change to a
public model. It wants its own branch, its own version, and consumers migrated only once the
new shape is proven — not to be entangled with build-logic experiments in a consumer repo.
