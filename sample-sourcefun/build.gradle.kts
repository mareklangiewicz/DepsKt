import pl.mareklangiewicz.sourcefun.*

plugins {
    id("pl.mareklangiewicz.sourcefun")
}

sourceFun {
    def("funTask1", "fun1Src", "funTempOut") { null }
    def("funTask2", "fun2Src", "funTempOut") { null }
}

tasks.register<SourceFunTask>("funTask3") {
    source("fun3Src")
    outputDir.set(file("funTempOut"))
    visitFile { inFile, outFile -> println(inFile.absolutePath); println(outFile.absolutePath) }
}

tasks.register<SourceRegexTask>("regexExperiment") {
    source("regexTempSrc")
    outputDir.set(file("regexTempOut"))
    match.set(".*")
    replace.set("XXX")
    doLast {
        println("fjkdslj")
    }
}
