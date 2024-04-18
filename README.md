# DepsKt

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.
It's a gradle "convention plugin": https://docs.gradle.org/current/samples/sample_convention_plugins.html

Usage:

- clone this repo outside your project
  - `git clone git@github.com:mareklangiewicz/DepsKt.git`
- include it in your `settings.gradle.kts`
  - `includeBuild("../DepsKt")`
- add the plugin to your build files `build.gradle.kts`
  - `plugins { id("pl.mareklangiewicz.deps") }`
- use `Vers` and `Deps` objects in your build files to get current versions of common libraries
  - it not only contains versions but all groups and names too, so it can be easily used in other build files
  - add your project `dependencies` with syntax like this: `testImplementation(deps.junit5)`

#### Notes

- It's a statically checked Kotlin code, so we should have full IDE support
- If you don't want to use it all, you can still just bookmark one file
  - [github:Deps.kt](https://github.com/mareklangiewicz/DepsKt/blob/master/src/main/kotlin/deps/Deps.kt) to quickly check
    current versions of popular
    libraries
- UPDATE: There is now also another file with a few additional versions that are updated manually:
  - [github:Vers.kt](https://github.com/mareklangiewicz/DepsKt/blob/master/src/main/kotlin/deps/Vers.kt)

#### UPDATE

- Now it is also available on gradle plugin portal:
  - https://plugins.gradle.org/plugin/pl.mareklangiewicz.deps
  - https://plugins.gradle.org/plugin/pl.mareklangiewicz.deps.settings
  - https://plugins.gradle.org/plugin/pl.mareklangiewicz.sourcefun
- There is more experimental stuff added too. Like some typical/"default" build configs etc.
  - (but nothing is configured automatically/implicitly)
- The deps.settings plugin just allows to add it all in settings file,
  so the code is available in all build files.
- The sourcefun plugin is an experiment to allow easy code generation. Example:
  - https://github.com/mareklangiewicz/DepsKt/blob/master/sample-sourcefun/build.gradle.kts
