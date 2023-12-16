@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

data class LibDetails(
    val name: String,
    val group: String,
    val description: String,
    val authorId: String, // unique id in SCM like github
    val authorName: String,
    val authorEmail: String,
    val githubUrl: String,
    val licenceName: String,
    val licenceUrl: String,
    val version: Ver,

    val namespace: String = "$group.${name.lowercase()}", // currently used in andro libs and apps
    val appId: String = namespace, // currently used in andro apps
    val appMainPackage: String = namespace,
    val appMainClass: String = "App_jvmKt", // for compose jvm
    val appMainFun: String = "main", // for native
    val appVerCode: Int = version.code, // currently used in andro apps
    val appVerName: String = version.ver, // currently used in andro apps

    val settings: LibSettings = LibSettings()
) {
    fun withVer(version: Ver) = copy(version = version)
}

// TODO_someday: this impl ignores patches for simplicity.
// Write better impl with patches parsing, but it has to consider weird suffixes and has to be monotonically increasing!
private val Ver.code: Int get() = ver.split(".").run { this[0].toInt() * 10000 + this[1].toInt() } + 1 // zero is incorrect

data class LibSettings(
    val withJvm: Boolean = true,
    val withJvmVer: String? = Vers.JvmDefaultVer.takeIf { withJvm },
    val withJs: Boolean = true,
    val withNativeLinux64: Boolean = false,
    val withKotlinxHtml: Boolean = false,
    val withTestJUnit4: Boolean = false,
    val withTestJUnit5: Boolean = withJvm,
    val withTestUSpekX: Boolean = true,

    val withSonatypeOssPublishing: Boolean = false,

    val compose: LibComposeSettings? = LibComposeSettings(
        withComposeMaterial2 = withJvm,
        withComposeMaterial3 = withJvm,
        withComposeFullAnimation = withJvm,
        withComposeDesktop = withJvm,
        withComposeWebCore = withJs,
        withComposeWebSvg = withJs,
        withComposeTestUiJUnit4 = withTestJUnit4,
        withComposeTestUiJUnit5 = withTestJUnit5,
        withComposeTestWebUtils = withJs,
    ),

    val andro: LibAndroSettings? = null,

    val repos: LibReposSettings = LibReposSettings(
        withKotlinxHtml = withKotlinxHtml,
        withComposeJbDev = compose != null,
        withComposeCompilerAndroidxDev = compose?.withComposeCompilerVer != null
    )
) {
    val withCompose get() = compose != null
    val withAndro get() = andro != null
}

/** In [LibSettings.compose] the defaults are adjusted depending on platforms. */
data class LibComposeSettings(
    val withComposeCompilerVer: Ver? = Vers.ComposeCompiler,
    val withComposeUi: Boolean = true,
    val withComposeFoundation: Boolean = true,
    val withComposeMaterial2: Boolean = true,
    val withComposeMaterial3: Boolean = true,
    val withComposeMaterialIconsExtended: Boolean = false,
    // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended?repo=space-public-compose-dev
    val withComposeFullAnimation: Boolean = true,
    val withComposeDesktop: Boolean = true,
    val withComposeDesktopComponents: Boolean = false,
    // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane?repo=space-public-compose-dev
    val withComposeWebCore: Boolean = false,
    val withComposeWebSvg: Boolean = false,
    val withComposeTestUiJUnit4: Boolean = false,
    val withComposeTestUiJUnit5: Boolean = false,
    // Not yet supported, but let's use this flag to use when I want to experiment with junit5 anyway.
    // (Theoretically JUnit5 should live with JUnit4 peacefully, but I expect issues with Gradle or IDE)
    // https://issuetracker.google.com/issues/127100532?pli=1
    // https://github.com/android/android-test/issues/224
    // https://github.com/JetBrains/compose-multiplatform/issues/2371

    val withComposeTestWebUtils: Boolean = false,
)

data class LibAndroSettings(
    val sdkCompile: Int = Vers.AndroSdkCompile,
    val sdkTarget: Int = Vers.AndroSdkTarget,
    val sdkMin: Int = Vers.AndroSdkMin,
    val withMDC: Boolean = false,
    val withTestRunner: String? = Vers.AndroTestRunner,
    val publishVariant: String = "", // for now only single variant or all variants can be published.
) {
    val publishAllVariants get() = publishVariant == AllVariants
    val publishNoVariants get() = publishVariant == NoVariants
    val publishOneVariant get() = !publishNoVariants && !publishNoVariants
    val AllVariants get() = "*"
    val NoVariants get() = ""
}

data class LibReposSettings(
    val withMavenLocal: Boolean = true,
    val withMavenCentral: Boolean = true,
    val withGradle: Boolean = false,
    val withGoogle: Boolean = true,
    val withKotlinx: Boolean = true,
    val withKotlinxHtml: Boolean = false,
    val withComposeJbDev: Boolean = false,
    val withComposeCompilerAndroidxDev: Boolean = false,
    val withKtorEap: Boolean = false,
    val withJitpack: Boolean = false,
)

fun langaraLibDetails(
    name: String,
    group: String = "pl.mareklangiewicz",
    description: String = "",
    authorId: String = "langara",
    authorName: String = "Marek Langiewicz",
    authorEmail: String = "marek.langiewicz@gmail.com",
    githubUrl: String = "https://github.com/langara",
    licenceName: String = "Apache-2.0",
    licenceUrl: String = "https://opensource.org/licenses/Apache-2.0",
    version: Ver = Ver(0, 0, 1),
    settings: LibSettings = LibSettings(),
) = LibDetails(
    name = name,
    group = group,
    description = description,
    authorId = authorId,
    authorName = authorName,
    authorEmail = authorEmail,
    githubUrl = githubUrl,
    licenceName = licenceName,
    licenceUrl = licenceUrl,
    version = version,
    settings = settings,
)