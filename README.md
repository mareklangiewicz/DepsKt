# deps.kt

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.
It's a gradle "convention plugin": https://docs.gradle.org/current/samples/sample_convention_plugins.html

Usage:
- clone this repo outside your project
    - `git clone git@github.com:langara/deps.kt.git`
- include it in your `settings.gradle.kts`
    - `includeBuild("../deps.kt")`
- add the plugin to your build files `build.gradle.kts`
  - `plugins { id("pl.mareklangiewicz.deps") }`
- use `Vers` and `Deps` objects in your build files to get current versions of common libraries
    - it not only contains versions but all groups and names too, so it can be easily used in other build files
    - add your project `dependencies` with syntax like this: `testImplementation(Deps.junit5)`
    
TODO: links to updated samples    


#### Notes
- It's a statically checked Kotlin code, so we should have full IDE support
- If you don't want to use it all, you can still just bookmark one file [github:deps.kt](https://github.com/langara/deps.kt/blob/master/src/main/kotlin/deps.kt) to quickly check current versions of popular libraries
