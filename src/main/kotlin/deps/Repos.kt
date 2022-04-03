package pl.mareklangiewicz.deps

object Repos {

    // https://central.sonatype.org/publish/publish-guide/#accessing-repositories
    const val sonatypeOssSnapshotsOld = "https://oss.sonatype.org/content/repositories/snapshots/"
    const val sonatypeOssSnapshotsNew = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    const val sonatypeOssSnapshots = sonatypeOssSnapshotsNew
    const val sonatypeOssNexus = "https://s01.oss.sonatype.org/service/local/"

    const val ktorEap = "https://maven.pkg.jetbrains.space/public/p/ktor/eap"
    const val kotlinxHtml = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven"

    const val composeDesktopDev = "https://maven.pkg.jetbrains.space/public/p/compose/dev"

    const val jitpack = "https://jitpack.io"

    const val kotlinx = "https://kotlin.bintray.com/kotlinx"
}
