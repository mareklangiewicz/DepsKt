# `:templatefun` off the compose plugin's dependency accessors

Status: **DONE (2026-09-16).** The 46 `compose.dependencies.*` deprecation warnings are gone, and
the resolved dependency graph of a real compose build is **byte-identical** before and after.

## What the warnings were

`MppBuildTemplates.kt` (21) and `RawLibBuildTemplates.kt` (25) read every Compose Multiplatform
coordinate off the compose gradle plugin's `compose.dependencies.*` accessors. JetBrains deprecated
them: *"Specify dependency directly."*

Specifying them directly is exactly what this repo is for. Every one of those coordinates is already
in the generated `Deps.kt`, refreshed daily, and `AndroBuildTemplates.kt` has always done it that way
for the androidx side (`AndroidX.Compose.Material.material`). The repo did it both ways; only the
andro half was doing what JetBrains now asks for.

(The count of 17 in an earlier handoff was wrong — it came from a truncated log tail.)

## The trap: these artifacts are NOT on one version

The obvious move is to mirror `defaultComposeAndroDeps`, which aligns androidx to one
`Vers.ComposeAndro`. **That would break the build.** Compose Multiplatform does not release in
lockstep, and the plugin says so itself — `org.jetbrains.compose.ComposeBuildConfig` inside
compose-gradle-plugin 1.12.0-beta01 holds two constants:

```
composeVersion          = "1.12.0-beta01"
composeMaterial3Version = "1.9.0"
```

Checked against the repository rather than inferred: `org.jetbrains.compose.material3:material3:1.12.0`
is a **404**; `:1.9.0` resolves. Aligning everything to one version would have produced an
unresolvable material3 — and only at the moment something actually resolved it.

This is also why the accessors return coordinates *without* versions: the plugin supplies them
per-artifact, not by appending its own version.

## The shape: one seam, `ComposeJb`

`ComposeJbDeps.kt` holds an internal `ComposeJb` object with one property per artifact, so the 45
call sites changed from `compose.dependencies.runtime` to `ComposeJb.runtime` and nothing else moved.
The version policy lives in exactly one line:

```kotlin
private val Dep.jb: Dep get() = withVer(verLastBeta)
```

Per artifact, newest beta-or-better. Deliberately **not** `verLast`: that is whatever is newest in the
catalog — currently `1.13.0-alpha01` for most of these — and a build template must not drag every
consumer onto an alpha.

`materialIconsExtended` needs no special case: JetBrains pinned it at 1.7.3 and stopped updating it
(the old accessor had the version baked into the string), and the catalog has only that version.

### Two things deliberately left alone

- **`compose.desktop.currentOs` is still an accessor.** It is not deprecated, and it is the one that
  does real work — picking `desktop-jvm-linux-x64` / `-macos-arm64` / … at configuration time, so it
  has no single coordinate to state. JetBrains deprecated the pass-throughs and kept this.
- **`uiUtil` is spelled out by hand.** `org.jetbrains.compose.ui:ui-util` is published (1.12.0
  resolves on Maven Central) but absent from the generated `Deps.kt`. That region is downloaded
  wholesale from an upstream dataset and overwritten by `updateGeneratedDeps`, and `[[Deps Selected]]`
  — the manual region — holds only typealiases. Its version follows `ui` so the two cannot drift.

Removing the accessors also made three `@OptIn(ExperimentalComposeLibrary::class)` annotations
unnecessary; they were only there for the experimental accessors. 46 warnings -> 0.

## Verified against a real compose build

The unit that matters: **nothing in DepsKt exercises the compose path** (`withCompose = false` in both
its own settings and the sample's). KGround's `template-full` does — `enableCompose = true`, a real
compose MPP lib calling `defaultBuildTemplateForFullMppLib()`.

Method: copy `template-full` to a scratch dir, resolve its dependencies against **published 0.4.53**
(un-migrated) and against a **maven-local 0.4.54** (migrated), and diff.

- `jvmCompileClasspath` (63 lines), `jsCompileClasspath` (57), `jvmTestCompileClasspath` (63):
  **identical**, including `material3` at 1.9.0 while everything else is at 1.12.0.
- With `withComposeMaterialIconsExtended` / `withComposeDesktopComponents` / `withComposeTestUi` /
  `withComposeTestUiJUnit4` forced on — all default false, so the stock template never covers them —
  also **identical**, and `material-icons-extended` lands on **1.7.3** with no special case.
- `:template-full-lib:build` green on both.

**The control, because an identical diff proves nothing on its own:** a deliberately-broken build
(material3 taking `verLast` instead of `verLastBeta`) published as 0.4.55 makes the diff show
`material3:1.13.0-alpha01`. The comparison can see a difference; it just did not find one. That
0.4.55 was then deleted from `~/.m2` so a wrong build cannot poison a later local resolution.

That the local plugin was really in use is not assumed either: `buildEnvironment` shows
`pl.mareklangiewicz.deps:templatefun:0.4.54`, a version that exists nowhere but maven local.

### Pre-existing red, unrelated

`template-full`'s full build fails at `:kotlinStoreYarnLock` (a copied `kotlin-js-store`) and at
`checkComposeUiTestConfigurationForJs` ("no executable binary is declared" for the js target,
CMP-4906). **Both fail identically on the un-migrated 0.4.53**, so neither is caused by this change.
With those two excluded the build is green on both versions.

## What this changes for consumers

Versions now come from DepsKt's daily catalog instead of the applied compose plugin's pins. On
`template-full` they happen to coincide exactly, because it applies `plugs.ComposeJbStable` and the
catalog agrees. A consumer pinning an OLDER compose plugin would no longer get that plugin's matching
artifact versions — it would get DepsKt's. That is this repo's whole premise, but it is a real
semantic change and is why it was tested against a real build rather than reasoned about.

## Related

- `templatefun-off-kotlin-dsl.md` — the other templatefun cleanup landed the same day.
- `releasing.md` — why a local `publishToMavenLocal` is how this got tested.
