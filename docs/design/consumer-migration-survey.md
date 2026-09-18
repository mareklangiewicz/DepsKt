# Consumer survey: repos still on the pre-0.4.63 publish model

Status: **survey only, no repo migrated**. Taken 2026-09-18 on myxps, by grepping every
`settings.gradle.kts` under `~/code` that applies `pl.mareklangiewicz.deps.settings`, then
grepping each repo's `*.gradle.kts` for the APIs 0.4.63 removed or changed.

This answers the standing question from three handoffs: *which repos meet the breaking change
when they migrate, and how hard do they meet it?* See
[publish-intent-per-module.md](publish-intent-per-module.md) for what changed and why.

## What "breaking" means here, precisely

0.4.63 does three separable things to a consumer:

1. **`LibFlags.withCentralPublish` and `LibAndro.publishVariant` are gone.** A build script
   that names either fails to COMPILE. This is the hard break.
2. **`defaultBuildTemplateFor*` gained `publish: LibPublish? = null`.** Defaulted, so existing
   calls still compile — but a module that used to inherit publish intent now publishes
   nothing unless it passes a `LibPublish`. Silent behaviour change, not an error.
3. **`defaultPublishingOrNot` errors on either mismatch** — `plugs.VannikPublish` applied with
   no `LibPublish`, or a `LibPublish` with no plugin. Configuration-time failure with a
   message naming the module.

So a repo's tier is set by (1) first, then (3).

## Tier A — hard break: names a removed API (12 repos)

These fail to compile the moment the settings pin moves past 0.4.62. Each needs its
`withCentralPublish` / `publishVariant` call sites rewritten to pass `LibPublish` at the
module. Sorted by last commit, most recent first — that is the order worth migrating in.

| Repo | Pin | Last commit | Removed APIs used |
|---|---|---|---|
| `AbcdK` | 0.4.29 | 2026-09-17 | `withCentralPublish` |
| `TupleK` | 0.4.29 | 2026-09-17 | `withCentralPublish` |
| `SourceFun` | 0.4.25 | 2026-09-16 | `withCentralPublish` |
| `CoEdges` | 0.4.25 | 2026-07-11 | `withCentralPublish` |
| `AreaKim` | 0.4.25 | 2026-07-10 | `withCentralPublish`, `publishVariant` |
| `kokpit667` | 0.4.25 | 2025-09-08 | `withCentralPublish`, `publishVariant` |
| `MyStolenPlaygrounds` | 0.4.25 | 2025-12-03 | `withCentralPublish`, `publishVariant` |
| `RxMock` | 0.4.25 | 2025-04-02 | `withCentralPublish` |
| `SMokK` | 0.4.25 | 2025-04-02 | `withCentralPublish` |
| `KommandLine` | 0.4.25 | 2025-01-08 | `withCentralPublish` |
| `StructuredNotes` | 0.4.25 | 2024-06-10 | `withCentralPublish`, `publishVariant` |
| `kthreelhu` | 0.4.25 | 2023-05-01 | `withCentralPublish` |

Two of these — `StructuredNotes` and `kthreelhu` — declare central publishing but apply
`plugs.VannikPublish` nowhere, so after the compile fix they also hit error path (3), the
publish-without-plugin one. Worth expecting rather than debugging.

`AbcdK` and `TupleK` are the ones whose READMEs say "archived" while the GitHub repos are not
(see the handoff). If they are archived, they leave this list.

`SourceFun` here is the standalone repo; DepsKt has since absorbed it as `:sourcefun`, so the
standalone one may be superseded rather than migrated — check before spending effort on it.

## Tier B — no compile break, but will error at configuration (2 repos)

| Repo | Pin | Last commit | Why |
|---|---|---|---|
| `KotVim` | 0.4.62 | 2026-09-18 | applies `plugs.VannikPublish`, names no removed API |
| `UWidgets` | 0.4.62 | 2026-09-17 | applies `plugs.VannikPublish`, names no removed API |

The nearest repo to the change and the cheapest migration: it compiles fine, then fails at
configuration with the plugin-without-publish message until one `LibPublish` is passed.

## Tier C — unaffected by this change

Publish nothing and name no removed API, so the pin can move freely:
`AutomateK`, `MyDevBridge`, `TixyPlayground`, `dbus-kotlin`.

Composite builds with no version pin, resolving DepsKt through `includeBuild`:
- `uspek-painters/{desk-only,preview-andro,with-andro,with-web}` include `../../DepsKt`, which
  IS the live 0.4.63 checkout — so they already see the new model. All four publish nothing,
  and all are from 2022; whether they configure at all under current AGP/Kotlin is untested.
- `kokpit667/isolated/{isolatedground1,2,3,isolatedkamera}` include `../../DepsKt`, resolving
  to `kokpit667/DepsKt`, which does **not exist**. These builds cannot configure today, for
  reasons that predate this change.
- `android/MyTmpComposeApp009` includes `../../kotlin/deps.kt`, also missing. Same.
- `TmpKotlinCliApp02` pins 0.2.08 and is not a git repo.

## Method, and what this survey does NOT establish

Grep over `~/code` only. A repo Marek has not cloned here is not in this table, and neither is
anything outside `~/code`. Nothing was built: no repo in Tier A or B was configured against
0.4.63 to confirm the predicted failure — the tiers are read off the source, not measured. The
error paths themselves ARE measured, in `samplefun` and `:kgroundx` (see the design note); what
is unmeasured is which line of which repo trips them.

Last-commit dates are local `HEAD`, not the remote's.

## Corrections, from actually migrating four of these

`UWidgets` was listed in Tier A on a `withCentralPublish` hit that turned out to be **inside a
comment** — the surviving prose of the flag's own removal, not a call site. It is Tier B, and it
is migrated. That is the same trap the handoff records for bulk `sed` on build scripts: this
survey's grep did not distinguish code from comment, so treat every Tier A row as "names the
string", not "calls the function", until someone opens the file.

The tiers also proved to be the *smaller* half of the work. What actually decided effort was how
far behind a repo's build scripts were, which this survey never measured:

- `CoEdges`, `AreaKim` and `MyStolenPlaygrounds` were on the pre-de-nesting
  `LibDetails`/`LibSettings` model with hundreds of lines of vendored template regions, so
  migrating meant the templatefun move as well — two or three steps, not one.
- `AreaKim` and `MyStolenPlaygrounds` did not build **at all** before being touched, for AGP 9
  reasons in vendored code that predate 0.4.63 entirely. "Pinned below 0.4.62" said nothing about
  that.
- Two modules could not be migrated at all: since AGP 9, `com.android.application` cannot be
  combined with KMP, and the plugin AGP names as the replacement is a *library* plugin with no
  application counterpart. `AreaKim`'s demo app gave up its android target; `MyStolenPlaygrounds`'
  `playgrounds-app` could not, because it *is* the android app, and is left unmigrated.

So the remaining Tier A rows should be read as a lower bound on effort. The cheap case this survey
imagined — bump the pin, pass a `LibPublish` — was real only for `UWidgets`, the one repo already
on templatefun.
