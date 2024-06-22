package pl.mareklangiewicz.deps

import java.net.URI

object Repos {

  val sonatypeOssSnapshotsOld = URI("https://oss.sonatype.org/content/repositories/snapshots/")
  val sonatypeOssSnapshotsNew = URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")

  /** https://central.sonatype.org/publish/publish-guide/#accessing-repositories */
  val sonatypeOssSnapshots = sonatypeOssSnapshotsNew

  val sonatypeOssNexus = URI("https://s01.oss.sonatype.org/service/local/")

  val ktorEap = URI("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
  val kotlinxHtml = URI("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")

  val composeJbDev = URI("https://maven.pkg.jetbrains.space/public/p/compose/dev")

  @Deprecated("Use compose compiler provided with kotlin itself.")
  val composeCompilerJbDev = composeJbDev // this just reminds that jb compose compilers are in the same place

  @Deprecated("Use compose compiler provided with kotlin itself.")
  val composeCompilerAxDev = URI("https://androidx.dev/storage/compose-compiler/repository/")

  val jitpack = URI("https://jitpack.io")

  val kotlinx = URI("https://kotlin.bintray.com/kotlinx")

  val kotlinxSpaceDev = URI("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
}
