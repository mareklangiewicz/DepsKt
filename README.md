# deps.gradle

This repo allows to easily share current versions of well known libraries for Kotlin/Java/Android between projects.

Usage:
- clone this repo (and then pull changes from time to time)
- make a hard link to `deps.gradle` file in your repo
    - `ln ~/code/deps.gradle/deps.gradle ~/code/yourrepo/`
    - thanks to hard link you will always commit copy of `deps.gradle` when you pull deps.gradle repo changes
    - you can check if both `deps.gradle` files point to the same inode using `find`:
        - `find ~/code -samefile ~/code/deps.gradle/deps.gradle`
- use `deps.gradle` in your build files to get current versions of common libraries
    - it not only contains versions but all groups and names too, so it can be easily used in other gradle build files
    - to be able to use it everywhere: add `apply from: 'deps.gradle'` in `buildscript` of your main `build.gradle` file
    - add your project `dependencies` with syntax like this: `testImplementation deps.junit`
    
    
Example:

Main `build.gradle` file:
```groovy
buildscript {
    apply from: 'deps.gradle'
    //...
}

```

Subproject `build.gradle` file:
```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

android {
    compileSdkVersion vers.androidCompileSdk
    buildToolsVersion vers.androidBuildTools


    defaultConfig {
        applicationId "pl.elpassion.iot.commander"
        minSdkVersion vers.androidMinSdk
        targetSdkVersion vers.androidTargetSdk
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
    implementation deps.kotlinStdlib
    implementation deps.rxbindingKotlin
    implementation deps.rxrelay
    implementation deps.rxlifecycleComponents
    implementation deps.rxlifecycleKotlin
    implementation deps.androidSupportAppcompat
    testImplementation deps.junit
    testImplementation deps.mockitoKotlin
}
```
    
See full example in [KWSocket repo](https://github.com/langara/KWSocket)
