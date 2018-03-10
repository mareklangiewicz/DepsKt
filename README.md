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
- use `deps.kt` in your build files to get current versions of common libraries
    - it not only contains versions but all groups and names too, so it can be easily used in other build files
    - add your project `dependencies` with syntax like this: `testImplementation(deps.junit)`
    
    
See the `kotlinsample` directory for kotlin example project


Gradle(groovy) example:

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
