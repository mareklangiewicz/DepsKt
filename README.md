# DepsKt

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.
It's a gradle "convention plugin": https://docs.gradle.org/current/samples/sample_convention_plugins.html

#### Usage:

- Add `id("pl.mareklangiewicz.deps.settings")` to your plugins in `settings.gradle.kts`
- Or add `id("pl.mareklangiewicz.deps")` to your plugins in `build.gradle.kts`  (for more local access)
- Example
  ```kotlin
  // in settings.gradle.kts:
  // ...
  plugins {
    // ...
    id("pl.mareklangiewicz.deps.settings") version "X.X.XX"
    // find version here: https://plugins.gradle.org/search?term=mareklangiewicz.deps
    // ...
  }
  ```
- Then just use predefined kotlin objects (and util functions) in your build files.
- Example
  ```kotlin
  plugins {
    plugAll(plugs.KotlinJvm, plugs.VannikPublish)
  }

  dependencies {
    api(Com.SquareUp.Okio.okio)
    testImplementation(kotlin("test"))
    testImplementation(Org.JUnit.Jupiter.junit_jupiter_engine)
    testRuntimeOnly(Org.JUnit.Platform.junit_platform_launcher)
  }

  ```

#### Notes

- It's a statically checked Kotlin code, so you should have full IDE support
- If you don't want to use it all, you can still just bookmark one file
  - [github:Deps.kt](https://github.com/mareklangiewicz/DepsKt/blob/master/src/main/kotlin/deps/Deps.kt) to quickly check
    current versions of popular
    libraries
- There is also another file with a few additional versions that are updated manually:
  - [github:Vers.kt](https://github.com/mareklangiewicz/DepsKt/blob/master/src/main/kotlin/deps/Vers.kt)

