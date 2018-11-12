# deps.kt

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.

Usage:
- clone this repo as submodule in your project
    - `yourproject$ git submodule add git@github.com:langara/deps.kt.git`
- make a symbolic link to `buildSrc` dir in your parent project
    - `yourproject$ ln -s deps.kt/buildSrc`
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

    defaultConfig {
        applicationId "pl.elpassion.iot.commander"
        minSdkVersion Vers.androidMinSdk
        targetSdkVersion Vers.androidTargetSdk
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner Vers.androidTestRunnerClass
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
    implementation Deps.androidxAppcompat
    testImplementation Deps.junit
    testImplementation Deps.mockitoKotlin
}
```
    
See bigger full example in [KWSocket repo](https://github.com/langara/KWSocket)


#### Notes
- It's a statically checked Kotlin code, so we should have full IDE support
- If you don't want to use it all, you can still just bookmark one file [github:deps.kt](https://github.com/langara/deps.kt/blob/master/buildSrc/src/main/java/deps.kt) to quickly check current versions of popular libraries
    - I'll try to keep it all updated and to check if they work nicely together - at least in some of my own projects.
- The idea of using `buildSrc` has been taken from this caster.io lesson: [Using Kotlin and buildSrc for build.gradle Autocomplete in Android Studio](https://caster.io/lessons/gradle-dependency-management-using-kotlin-and-buildsrc-for-buildgradle-autocomplete-in-android-studio?autoplay=true)
