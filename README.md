#deps

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.

Usage:
- clone this repo (and then pull changes from time to time)
- make a hard link to `deps.gradle` file in your repo
    - `ln ~/code/deps/deps.gradle ~/code/yourrepo/`
    - this way you will always commit copy of `deps.gradle` when it changes
- use `deps.gradle` in your build files to get current versions of common libraries
    - it not only contains versions but all groups and names too, so it can be easily used in other gradle build files
    - see an example in [KWSocket build file](https://github.com/langara/KWSocket/blob/master/apps/commander/build.gradle)