package pl.mareklangiewicz.ure

import kotlin.text.RegexOption.*

fun urePackageLine(withNamePrefix: String = "ktPackage") = ureKtKeywordLine("package", withNamePrefix)
fun ureImportLine(withNamePrefix: String = "ktImport") = ureKtKeywordLine("import", withNamePrefix)

fun ureKtKeywordLine(keyword: String, withNamePrefix: String = keyword) =
    ureLineWithContent(
        ureKeywordAndOptArg(
            keyword = ir(keyword),
            arg = ureChain(ureIdent(), chDot).withName(withNamePrefix + "Name")
        )
    ).withName(withNamePrefix + "Line")


private val ureLicenceMarker = (ir("licence") or ir("copyright")).withOptionsEnabled(IGNORE_CASE)

fun ureLicenceComment(licenceMarker: Ure = ureLicenceMarker, withName: String = "ktLicenceComment") = ure {
    1 of ureWhateva()
    1 of licenceMarker
    1 of ureWhateva()
}.commentedOut(traditional = true).withName(withName)

fun ureKtComposeTestOutline() = ure {
    1 of ureLicenceComment().withOptSpacesAround()
    1 of ureWhateva(reluctant = false).withName("ktOtherStuffBeforePackageLine")
    1 of urePackageLine().withOptSpacesAround()
    1 of ureWhateva(reluctant = false).withName("ktRest")
}

fun ureKtOutline(withNamePrefix: String = "ktPart") = ure {
    TODO("NOW")
}


