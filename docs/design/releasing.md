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
4. Only then bump the deliberately-lagging plugin-id literals: `settings.gradle.kts`,
   `deps/build.gradle.kts`, and both of `sourcefun/sample-sourcefun`'s scripts.

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

- **Nothing tags.** `git tag -l "v*"` is empty; the newest tags in this repo are `0.2.x`, plus a
  literal `X.X.XX` tag someone once created from the README's placeholder. The workflow has never
  run.
- **Central publishing is switched off in the model.** `LibFlags.withCentralPublish` defaults to
  `false` and nothing in this build sets it, so every `mavenPublishing` block here — the exported
  `defaultPublishing`, plus `:sourcefun`'s and `:templatefun`'s own copies — skips
  `publishToMavenCentral(..)`. The publication target is not configured, so tagging alone would not
  produce a Central release.

Measured, not inferred: `pl.mareklangiewicz.deps:templatefun:0.4.53` and
`pl.mareklangiewicz.deps:DepsKt:0.4.53` both 404 on `repo1.maven.org`, while the same coordinate
returns 200 from `plugins.gradle.org/m2/`.

**Consequence worth stating:** these artifacts are resolvable from the Gradle Plugin Portal's Maven
repository and NOT from Maven Central. A consumer that only declares `mavenCentral()` cannot resolve
`pl.mareklangiewicz.deps:DepsKt` — it needs `gradlePluginPortal()`. That is the current bargain, and
it is fine while these are consumed as gradle plugins.

If Central publishing is ever wanted, the tag workflow is not the first step — flipping
`withCentralPublish` is, and then the signing keys have to be real.

## Related

- `lib-details-denesting.md` — the sibling layout, publication coordinates, and the artifactId story.
- `templatefun-off-kotlin-dsl.md` — the other bootstrap that a publish unblocked.
