import pl.mareklangiewicz.sourcefun.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*

plugins {
    id("pl.mareklangiewicz.sourcefun")
}

val extensionsPath get() = rootProjectPath / "sample-lib/src/main/kotlin/extensions"
val reportsPath get() = buildPath / "awesome-reports"

sourceFun {
    grp = "awesome"
    val processExtensions1 by reg {
        doNotTrackState("debugging")
        src = extensionsPath / "SpecialExtensions.kt"
        out = extensionsPath
        setTransformFun { content ->
            val matchResult = ureSpecialExtensionsKt.compile().matchEntire(content)!!
            val before by matchResult
            val template by matchResult
            val types = listOf("Short", "Int", "Long", "Float", "Double", "Boolean", "Char") // except Byte
            val generated = types.map { template.replace("Byte", it) }
                .joinToString(
                    separator = "\n",
                    prefix = "// region $regionGeneratedName",
                    postfix = "// endregion $regionGeneratedName",
                )
            before + generated
        }
    }
}


tasks.register<SourceFunTask>("reportStuff1") {
    group = "awesome"
    src = extensionsPath
    out = reportsPath
    setVisitPathFun { inPath, outPath -> println(inPath); println(outPath) }
}

tasks.register<SourceRegexTask>("reportStuff2") {
    group = "awesome"
    src = extensionsPath
    out = reportsPath
    match.set("Ar*ay")
    replace.set("XXX")
}

val regionByteName = "Byte Special Extensions"
val regionGeneratedName = "Generated Special Extensions"
val ureSpecialExtensionsKt = ure {
    val ureRegionByte = ureRegion(ureWhateva().withName("template"), ir(regionByteName))
    val ureRegionGenerated = ureRegion(ureWhateva(), ir(regionGeneratedName))
    val ureBeforeGenerated = ure {
        1 of ureWhateva()
        1 of ureRegionByte
        1 of ureWhateva()
    }
    1 of ureBeforeGenerated.withName("before")
    1 of ureRegionGenerated // no need for name because we will replace it
}
