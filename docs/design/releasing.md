# How DepsKt is released

Status: **decided, current.** Releases are published from Marek's machine with
`./gradlew publishPlugins`. The Gradle Plugin Portal is the only place these artifacts exist.

There is a second, tag-driven path in this repo that publishes to Maven Central. It has never run,
and it is not dormant by accident — see below. This note exists so nobody re-derives the situation
from the workflow file and concludes the release is half-broken.

## The actual release

```
./gradlew publishPlugins
```

Credentials come from the ENVIRONMENT on Marek's machine. Four plugin ids ship from this one build:

- `pl.mareklangiewicz.deps`
- `pl.mareklangiewicz.deps.settings`
- `pl.mareklangiewicz.templatefun`
- `pl.mareklangiewicz.sourcefun`

**Ask Marek before publishing, every time.** Publishing is irreversible: a version number on the
portal is spent.

Around the publish there is a fixed order, because several literals in this repo may only ever name
a version that is ALREADY published:

1. Bump the two synced sources — `Vers.DepsPlug` and `settings.gradle.kts`'s `extLib` version.
   They point at each other in comments; there is no single source of truth yet.
2. Commit, push, let CI go green.
3. `publishPlugins`.
4. Only then bump the deliberately-lagging `deps.settings` literals: `settings.gradle.kts` and
   `samplefun/settings.gradle.kts`. Those are the only two left: the settings plugin is what
   brings `plugs` into scope, so it cannot name itself through `plugs`. Every other self-applied
   plugin goes through `plugs.TemplateFun` / `plugs.SourceFun`, which carry the version the
   published settings plugin shipped as -- so they follow this bump with nothing to edit.

0.4.63 has an unusually large step 4: `LibPublish` replaces `LibFlags.withCentralPublish` and
`LibAndro.publishVariant`, and `defaultPublishing` changed signature, so this repo's OWN scripts
(`deps/build.gradle.kts` including its `tfDefaultPublishing` arity probe, `templatefun/build.gradle.kts`,
`sourcefun/build.gradle.kts`) must migrate in the same step that bumps their pins — they do not
compile against both models. `samplefun` is already migrated and does not wait, because it
substitutes the local build with `includeBuild("..")`.

Step 4 is also when anything waiting on the new version gets switched on. This repo bootstraps
itself — build scripts here apply the PUBLISHED `deps.settings` and `templatefun` — so a new
`Plugs` entry, or a new parameter on an exported function, is invisible to this repo's own build
scripts until it ships. Both of those happened on 2026-09-16 (0.4.52 and 0.4.53); see
`lib-details-denesting.md` and `templatefun-off-kotlin-dsl.md`.

## Why local, and not CI

Local publishing is faster and convenient enough. Configuring GitHub to release the gradle plugins
is a **maybe-someday**, not a gap: it would mean putting portal credentials in repository secrets
and giving up the "ask before publishing" checkpoint that currently sits in front of every release.
Nothing in the repo is waiting on it.

## The Maven Central path, and why it is inert

`.github/workflows/drelease.yml` triggers on a `v*.*.*` tag and runs `build` followed by
`publishAndReleaseToMavenCentral`, with `KL_SIGNINGINMEMORYKEY*` / `KL_MAVENCENTRAL*` repository
secrets. It is real, and it does nothing, for two independent reasons:

- **Nothing tags.** `git tag -l "v*"` is empty and the newest tags in this repo are `0.2.x`, from
  2023. The workflow has never run. (There was also a literal `X.X.XX` tag, created from the
  README's placeholder; deleted 2026-09-16, locally and on the remote. It pointed at a commit
  reachable from `master`, so nothing went with it.)
- **Central publishing is switched off in the model.** Since 0.4.63 that is `LibPublish.toCentral`,
  which defaults to `false`, and a module that passes no `LibPublish` at all is not published in any
  sense — see `publish-intent-per-module.md`. Before 0.4.63 it was `LibFlags.withCentralPublish`,
  also defaulting to `false`. Either way nothing in this build turns it on, so every
  `mavenPublishing` block here — the exported `defaultPublishing`, plus `:sourcefun`'s and
  `:templatefun`'s own copies — skips `publishToMavenCentral(..)`. The publication target is not
  configured, so tagging alone would not produce a Central release.

Measured, not inferred: `pl.mareklangiewicz.deps:templatefun:0.4.53` and
`pl.mareklangiewicz.deps:DepsKt:0.4.53` both 404 on `repo1.maven.org`, while the same coordinate
returns 200 from `plugins.gradle.org/m2/`.

**Consequence worth stating:** these artifacts are resolvable from the Gradle Plugin Portal's Maven
repository and NOT from Maven Central. A consumer that only declares `mavenCentral()` cannot resolve
`pl.mareklangiewicz.deps:DepsKt` — it needs `gradlePluginPortal()`. That is the current bargain, and
it is fine while these are consumed as gradle plugins.

If Central publishing is ever wanted, the tag workflow is not the first step — passing
`LibPublish(toCentral = true)` to each module that should ship is, and then the signing keys have to
be real. Note the unit changed with 0.4.63: there is no longer a repo-wide switch to flip, because a
repo-wide switch is precisely what leaked six sample apps into a USpek release.

## Related

- `lib-details-denesting.md` — the sibling layout, publication coordinates, and the artifactId story.
- `publish-intent-per-module.md` — why publish intent left `LibFlags` in 0.4.63, and the consumer
  migration that a release unblocks.
- `templatefun-off-kotlin-dsl.md` — the other bootstrap that a publish unblocked.
