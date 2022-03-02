import pl.mareklangiewicz.sourcefun.*
import pl.mareklangiewicz.utils.*

plugins {
    id("pl.mareklangiewicz.sourcefun")
}

val extensionsPath get() = rootProjectPath / "sample-lib/src/main/kotlin/extensions"
val reportsPath get() = buildPath / "awesome-reports"

sourceFun {
    grp = "awesome"
    val processExtensions1 by reg {
        src = extensionsPath / "SpecialExtensions.kt"
        out = extensionsPath
        setTransformFun { content ->
            println(this)
            null
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
