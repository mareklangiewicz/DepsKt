
// region [Custom Basic Root Build Imports and Plugs]

import pl.mareklangiewicz.sourcefun.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.ure.UReplacement.Companion.Literal
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
  plug(plugs.KotlinMulti) apply false
  id("pl.mareklangiewicz.sourcefun") version "0.4.51" // I includeBuild("../..") in settings so version doesn't matter
}

// endregion [Custom Basic Root Build Imports and Plugs]

// The Lib is defined once, in settings.gradle.kts (gradle.extLib), same as every consumer repo.
// The old [[Root Build Template]] region that set rootExtLibDetails is gone with the nested model.
defaultGroupAndVerAndDescription(gradle.extLib)


val extensionsPath get() = rootProjectPath / "sample-lib/src/jvmMain/kotlin/extensions"
val reportsPath get() = buildPath / "awesome-reports"

sourceFun {
  grp = "awesome"
  val processExtensions1ByReg by reg {
    // doNotTrackState("debugging")
    src = extensionsPath / "SpecialExtensions.kt"
    out = extensionsPath
    setTransformFun { transformSpecialExtensionsContent(it) }
  }
  def("processExtensions2WithDefDeprecated", extensionsPath, extensionsPath) {
    if (name != "SpecialExtensions.kt") return@def null
    transformSpecialExtensionsContent(it)
  }
}

tasks.register<SourceFunTask>("fakeReportStuff1JustPrintLn") {
  group = "awesome"
  src = extensionsPath
  out = reportsPath
  setForEachFileFun {
    println("FRS1 Faking report (will NOT create any files in $reportsPath)")
    println("FRS1 <- $first:1") // :1 is for IDE to make it clickable
    println("FRS1 -> $second:1") // no file will be there, unless other task created it
  }
}

tasks.register<SourceUreTask>("fakeReportStuff2UreArrayToXXX") {
  group = "awesome"
  src = extensionsPath
  out = reportsPath
  val ure = ure { +ch('A'); 0..MAX of ch('r'); +ureText("ay") }
  match put ure
  replace put Literal("XXX")
}


@OptIn(DelicateApi::class, NotPortableApi::class)
fun transformSpecialExtensionsContent(content: String): String {
  val regionByteLabel = "Byte Special Extensions"
  val regionGeneratedLabel = "Generated Special Extensions"
  val ureSpecialExtensionsKt = ure {
    val ureRegionByte = ureRegion(
      ureWhateva().withName("template"),
      regionLabel = ureText(regionByteLabel),
      regionLabelName = "rln1"
    )
    val ureRegionGenerated = ureRegion(
      ureWhateva(),
      regionLabel = ureText(regionGeneratedLabel),
      regionLabelName = "rln2" // has to be different than above
    )
    val ureBeforeGenerated = ure {
      +ureWhateva()
      +ureRegionByte
      +ureWhateva()
    }
    +ureBeforeGenerated.withName("before")
    +ureRegionGenerated // no need for name because we will replace it
  }
  val matchResult = ureSpecialExtensionsKt.matchEntireOrThrow(content)
  val before by matchResult
  val template by matchResult
  val generated = listOf("Short", "Int", "Long", "Float", "Double", "Boolean", "Char")
    .joinToString(
      separator = "",
      prefix = "// region $regionGeneratedLabel\n",
      postfix = "// endregion $regionGeneratedLabel",
    ) { template.replace("Byte", it) }
  // Trailing newline, explicitly: the generated block's postfix is "// endregion ..." with nothing
  // after it, so without this the task strips the file's final newline on every run -- a real diff
  // in a tracked file, produced by a task whose whole job is to leave the file idempotent.
  return before + generated + "\n"
}
