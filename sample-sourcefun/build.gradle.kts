import pl.mareklangiewicz.sourcefun.*
import okio.Path.Companion.toPath
import pl.mareklangiewicz.utils.*

plugins {
    id("pl.mareklangiewicz.sourcefun")
}

sourceFun {
//    def("funTask1", "fun1Src", "funTempOut") { null }
//    def("funTask2", "fun2Src", "funTempOut") { null }
}

tasks.register<SourceFunTask>("funTask3") {
    src = rootProjectPath / ""
//    out = layout.buildDirectory.asFile.toOkioPath() / "someotherdir"
    setVisitPathFun { inPath, outPath -> println(inPath); println(outPath) }
}

tasks.register<SourceRegexTask>("funRegexTask") {
    src = "regexTempSrc".toPath()
    "regexTempOut".toPath()
    match.set(".*")
    replace.set("XXX")
    doLast {
        println("fjkdslj")
    }
}
