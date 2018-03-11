# deps.kt

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.

Usage:
- clone this repo (and then pull changes from time to time)
- make a hard link to `deps.kt` file in your repo
    - `ln ~/code/deps.kt/buildSrc/src/main/java/deps.kt ~/code/yourrepo/buildSrc/src/main/java/deps.kt`
    - thanks to hard link you will always commit copy of `deps.kt` when you pull deps.kt repo changes
    - you can check if both `deps.kt` files point to the same inode using `find`:
        - `find ~/code -samefile ~/code/deps.kt/buildSrc/src/main/java/deps.kt`
    - if you are using Intellij Idea or Android Studio, then you should disable "safe write" option in system settings
        - the "safe write" option destroys hardlinks on every save operation
- use `Vers` and `Deps` objects in your build files to get current versions of common libraries
    - it not only contains versions but all groups and names too, so it can be easily used in other build files
    - add your project `dependencies` with syntax like this: `testImplementation(Deps.junit)`
    
    
See the `kotlinsample` directory for kotlin sample project. If you are using traditional gradle (groovy) build
files, you can access the `Deps` and `Vers` objects the same way.

Gradle(groovy) example:

```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

android {
    compileSdkVersion Vers.androidCompileSdk
    buildToolsVersion Vers.androidBuildTools


    defaultConfig {
        applicationId "pl.elpassion.iot.commander"
        minSdkVersion Vers.androidMinSdk
        targetSdkVersion Vers.androidTargetSdk
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation project(':loggers')
    implementation project(':iot-api')
    implementation Deps.kotlinStdlib
    implementation Deps.rxbindingKotlin
    implementation Deps.rxrelay
    implementation Deps.rxlifecycleComponents
    implementation Deps.rxlifecycleKotlin
    implementation Deps.androidSupportAppcompat
    testImplementation Deps.junit
    testImplementation Deps.mockitoKotlin
}
```
    
See bigger full example in [KWSocket repo](https://github.com/langara/KWSocket)


#### Notes
- It's a statically checked Kotlin code, so we should have full IDE support - but current Android Studio can get confused from time to time :)
- If you don't want to use it all, you can still just bookmark one file [github:deps.kt](https://github.com/langara/deps.kt/blob/master/buildSrc/src/main/java/deps.kt) to quickly check current versions of popular libraries
    - I'll try to keep it all updated and to check if they work nicely together - at least in some of my own projects.
- The idea of using `buildSrc` has been taken from this caster.io lesson: [Using Kotlin and buildSrc for build.gradle Autocomplete in Android Studio](https://caster.io/lessons/gradle-dependency-management-using-kotlin-and-buildsrc-for-buildgradle-autocomplete-in-android-studio?autoplay=true)
