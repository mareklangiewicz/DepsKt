# Getting `:templatefun` off `kotlin-dsl`

Status: **DONE (2026-09-16).** `:templatefun` no longer applies `kotlin-dsl`. Both warnings are
gone and all three siblings are compiled by one Kotlin at `vers.Kotlin`. What follows is kept as the
record of WHY, because the cause is not what the error list looks like; the verification actually
performed is at the end.

## Why bother

`kotlin-dsl` pins the project that applies it to the Kotlin version **embedded in Gradle** — 2.4.0
for Gradle 9.7.1. `:deps` and `:sourcefun` compile at `Vers.Kotlin`, currently 2.4.20. So one of the
three siblings is built by a different compiler than the other two, and has been since templatefun
arrived. That is the real problem; the warning is only how it became visible.

It became visible when `:sourcefun` landed and made *two* subprojects declare a versioned Kotlin
plugin, which raises:

> The Kotlin Gradle plugin was loaded multiple times in different subprojects, which is not supported
> and may break the build.

Gradle's own prescribed remedy is to declare it once in the parent — the root now has
`plugins { plug(plugs.KotlinJvm) apply false }` — and that is what puts 2.4.20 into `:templatefun`'s
resolution scope, where `kotlin-dsl` starts objecting instead:

> The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `2.4.0` that might work
> differently than in the requested version `2.4.20`.

**Measured:** the two are mutually exclusive while `kotlin-dsl` is in the build. Declaring the version
in `pluginManagement { plugins { … } }` instead of the root build script does **not** help — it just
brings the multiple-load warning back, because that check is about the plugin being *applied* in two
subprojects, not about where the version string came from. There is no suppression property for
either warning (searched the 9.7.1 distribution jars).

## What `kotlin-dsl` was actually providing

This is the part worth reading, because it is not what it looks like.

Removing `kotlin-dsl` produces **62 compile errors** across `KotlinModuleBuildTemplate.kt`,
`MppBuildTemplates.kt`, `RawLibBuildTemplates.kt` and `AndroBuildTemplates.kt` — `Unresolved
reference 'compilerOptions' / 'testLogging' / 'license' / 'developer'`, and a long run of
`NamedDomainObjectContainer<KotlinSourceSet>.commonMain … is inapplicable because of a receiver type
mismatch`. It looks like a missing dependency or a huge porting job. It is neither.

Ruled out, each by measurement, so nobody repeats them:

- **Not a missing dependency.** Dumping `compileClasspath` with and without `kotlin-dsl` and diffing
  it gives a single-line difference: `kotlin-reflect-2.4.0.jar`. The Gradle API and all four
  `gradle-kotlin-dsl-*.jar` are present either way, and the affected files already
  `import org.gradle.kotlin.dsl.*`.
- **Not a duplicate Gradle API.** `gradle-api-9.3.0-rc02.jar` sits on the classpath next to
  `gradle-api-9.7.1.jar` and looks like a collision. `dependencyInsight` says it is
  `com.android.tools.build:gradle-api` — AGP's own artifact, which merely shares a file name.
  templatefun genuinely needs it.
- **Not the context-parameter flags.** Dropping `-Xexplicit-context-arguments`: still 62.
- **Not the language/API version.** Pinning `apiVersion`/`languageVersion` to `KOTLIN_2_4`: still 62.

The actual cause is visible in the error list once you notice that *every* unresolved name is the
receiver inside an `Action<T>` lambda — `configureEach { }`, `pom { }`, `testLogging { }`,
`licenses { license { } }`. Gradle annotates `Action<T>` with `@HasImplicitReceiver`, and it is the
**`sam-with-receiver` Kotlin compiler plugin** that honours it and turns those parameters into
lambdas *with receiver*. `kotlin-dsl` applies that compiler plugin; nothing else does. Likewise
`property = value` assignment on Gradle `Property<T>` comes from the **`assignment`** compiler plugin
via `@SupportsKotlinAssignmentOverloading`.

Adding sam-with-receiver alone: **62 → 4** errors, the remaining four all
`'val' cannot be reassigned` / `Assignment type mismatch … 'Property<JvmTarget>' was expected`.
Adding the assignment plugin too: **green.**

## The verified recipe

In `templatefun/build.gradle.kts`:

```kotlin
plugins {
  plugAll(plugs.KotlinJvmNoVer, plugs.GradlePublish, plugs.VannikPublish)
  id("org.jetbrains.kotlin.plugin.sam.with.receiver") version "2.4.20"
  id("org.jetbrains.kotlin.plugin.assignment") version "2.4.20"
}

dependencies {
  // ... unchanged, plus:
  implementation(gradleKotlinDsl())
}

// What `kotlin-dsl` was really providing.
samWithReceiver { annotation("org.gradle.api.HasImplicitReceiver") }
assignment { annotation("org.gradle.api.SupportsKotlinAssignmentOverloading") }
```

Those two hardcoded `"2.4.20"` literals want to be `vers.Kotlin`, which a `plugins {}` block cannot
see. Resolved by a configuration-time `check` in the script body — see item 5 below.

Plus: delete `templatefun/src/main/kotlin/pl.mareklangiewicz.templatefun.gradle.kts`, add a
`TemplateFunPlugin : Plugin<Project>` whose `apply` is the same one line
(`context(project) { testContextParameters() }`, so build output is unchanged), and register it
explicitly, because `kotlin-dsl` used to derive the id from the precompiled script's FILE NAME:

```kotlin
gradlePlugin {
  plugins {
    create("templateFunPlugin") {
      id = "pl.mareklangiewicz.templatefun"
      implementationClass = "pl.mareklangiewicz.templatefun.TemplateFunPlugin"
      // displayName / description / tags as before
    }
  }
}
```

**What is lost, and why it is fine.** The precompiled `.gradle.kts` proved one thing by existing —
that a script compiled WITHOUT `-Xcontext-parameters` can call into sources compiled WITH it. That
claim is now carried by real production build scripts instead: `deps/build.gradle.kts` reaches
`Project::defaultPublishing` through the flattened coercion, and the sample's
`sample-lib/build.gradle.kts` calls `defaultBuildTemplateForBasicMppLib()`. Both are ordinary,
flagless `.gradle.kts` files. Say so in the commit rather than dropping it silently.

## What was verified

All five open items from the original handoff, on `templatefun/build.gradle.kts` as it stands:

1. **Both warnings gone.** Neither "Unsupported Kotlin plugin version" nor "loaded multiple times"
   appears while configuring the whole build. The root keeps `plug(plugs.KotlinJvm) apply false`
   and templatefun now takes `plugs.KotlinJvmNoVer` from it, exactly like its two siblings.
2. **`./gradlew build` at the root is green** (3m31s), including `:sourcefun:test`, and
   `git status` is clean afterwards.
3. **The end-to-end check that matters.** `sourcefun/sample-sourcefun` builds green, and its log
   shows `> Task :DepsKt:templatefun:compileKotlin` — the composite really did substitute the
   LOCAL project, so it is the locally built, kotlin-dsl-free plugin with the explicitly registered
   id that applied. (`Successfully used context parameters in project: sample-lib` alone proves
   nothing here: the published plugin prints the same line.)
4. **`publishToMavenLocal` coordinates are unchanged.** Marker
   `pl.mareklangiewicz.templatefun:pl.mareklangiewicz.templatefun.gradle.plugin` still points at
   `pl.mareklangiewicz.deps:templatefun`, and `org.gradle.jvm.version` is still **23**. Diffing the
   locally built `.module` against the one published on the portal gives exactly ONE difference:
   `api`/`runtime` now declare `org.jetbrains.kotlin:kotlin-stdlib:2.4.20`. That is the Kotlin JVM
   plugin doing what it does for a normal project — `kotlin-dsl` used to supply Gradle's embedded
   stdlib instead, which is never published. Both siblings already declare it, so this is
   convergence, not drift.
5. **The two version literals: decided.** They stay literal — a `plugins {}` block cannot read
   `vers.Kotlin` — but the script body immediately `check`s them against it, so drift fails the
   build with an explanation instead of silently reintroducing the skew. Validated as a control:
   setting the literal to `2.4.19` makes configuration FAIL with that message.

   **Superseded the same day — there are no literals any more.** `Plugs.KotlinSamWithReceiver` and
   `Plugs.KotlinAssignment` were added, both `.withVer(vers.Kotlin)`, and `templatefun/build.gradle.kts`
   now says:

   ```kotlin
   plugAll(plugs.KotlinSamWithReceiver, plugs.KotlinAssignment)
   ```

   So the Kotlin version is stated in exactly one place, `Vers.kt`, and the `check` is gone with the
   literals it guarded. These two need NO root `apply false`: that exists to stop the Kotlin Gradle
   plugin being loaded by two subprojects, and these are applied only here.

   It took a publish to get there, as every bootstrap in these notes does — this script reads `plugs`
   from the PUBLISHED `deps.settings`, so the entries were invisible until they shipped. Verified
   against 0.4.52 rather than assumed (`Unresolved reference 'KotlinSamWithReceiver'`), then landed
   on 0.4.53.

   A green build is itself the proof that both plugins are still applied: without sam-with-receiver
   these sources do not compile (that is this whole note), and `samWithReceiver { }` / `assignment { }`
   would not resolve. `buildEnvironment` confirms both at **2.4.20**, i.e. `vers.Kotlin`.

**Still not done:** the 17 deprecation warnings in `MppBuildTemplates.kt` / `RawLibBuildTemplates.kt`
(JetBrains retiring the `compose.*` DSL accessors) are untouched and unrelated — they were there
before this change and are a separate migration.

## Related

- `lib-details-denesting.md`, "SourceFun moves in" — where the sibling layout and the root
  `apply false` are recorded.
- The comment above `plugins {}` in `templatefun/build.gradle.kts` points here.
