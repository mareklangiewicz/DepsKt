package pl.mareklangiewicz.ure

import kotlin.text.RegexOption.IGNORE_CASE

fun urePackageLine(withNamePrefix: String = "ktPackage") = ureKtKeywordLine("package", withNamePrefix)
fun ureImportLine(withNamePrefix: String = "ktImport") = ureKtKeywordLine("import", withNamePrefix)

fun ureKtKeywordLine(keyword: String, withNamePrefix: String = keyword) =
    ureLineWithContent(
        ureKeywordAndOptArg(
            keyword = ir(keyword),
            arg = ureChain(ureIdent, dot).withName(withNamePrefix + "Name")
        )
    ).withName(withNamePrefix + "Line")

private val ureLicenceMarker = (ir("licence") or ir("copyright")).withOptionsEnabled(IGNORE_CASE)
fun ureLicenceComment(licenceMarker: Ure = ureLicenceMarker) = ure {
        TODO()

}

fun ureKtOutline(withNamePrefix: String = "ktPart") = ure {
    TODO("NOW")
}


