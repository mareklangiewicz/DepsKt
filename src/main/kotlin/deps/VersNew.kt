package pl.mareklangiewicz.deps

object VersNew {

    val JvmDefaultVer = "17" // I had terrible issues with "16" (andro compose project)


    val Gradle5 = Ver("5.6.4", 0)
    val Gradle6 = Ver("6.8.3", 0)
    val Gradle7 = Ver("7.6.1", 0)
    val Gradle8 = Ver("8.1", 0)

    /**
     * Gradle - just a reference - not so useful in typical usecases
     * - [gradle releases](https://gradle.org/releases/)
     * - [gradle versions](https://services.gradle.org/versions)
     * - [gradle versions current](https://services.gradle.org/versions/current)
     * - [gradle versions rel candidate](https://services.gradle.org/versions/release-candidate)
     */
    val Gradle = Gradle8


    /**
     * - [dashboards](https://developer.android.com/about/dashboards/index.html)
     * - [build numbers](https://source.android.com/setup/start/build-numbers)
     */
    val AndroSdkMin = 26
    val AndroSdkCompile = 33
    val AndroSdkTarget = AndroSdkCompile

    /**
     * - [releases](https://developer.android.com/tools/releases/build-tools)
     */
    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    val AndroBuildTools = Ver("34.0.0", 0)

    /**
     * - [revisions](https://developer.android.com/topic/libraries/support-library/revisions.html)
     */
    @Deprecated("Use androidx")
    val AndroSupportLibrary = Ver("28.0.0", 0)

}