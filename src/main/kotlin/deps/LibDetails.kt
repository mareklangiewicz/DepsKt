@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

import java.util.*

@Deprecated("TODO: inline these to build files of libs themselves")
object LibsDetails {

    val USpek = langaraLibDetails(
        name = "USpek",
        description = "Micro tool for testing with syntax similar to Spek, but shorter.",
        githubUrl = "https://github.com/langara/USpek",
    )

    val SMokK = langaraLibDetails(
        name = "SMokK",
        description = "A bit scary library for mocking suspendable functions in Kotlin :-)",
        githubUrl = "https://github.com/langara/SMokK",
    )

    val RxMock = langaraLibDetails(
        name = "RxMock",
        description = "Tiny library for mocking RxJava calls.",
        githubUrl = "https://github.com/langara/RxMock",
    )

    val UPue = langaraLibDetails(
        name = "UPue",
        description = "Micro Multiplatform Reactive Library.",
        githubUrl = "https://github.com/langara/UPue",
    )

    val AbcdK = langaraLibDetails(
        name = "AbcdK",
        description = "Tiny unions lib for Kotlin.",
        githubUrl = "https://github.com/langara/AbcdK",
    )

    val TupleK = langaraLibDetails(
        name = "TupleK",
        description = "Tiny tuples lib for Kotlin with cool infix syntax.",
        githubUrl = "https://github.com/langara/TupleK",
    )

    val UWidgets = langaraLibDetails(
        name = "UWidgets",
        description = "Micro widgets for Compose Multiplatform",
        githubUrl = "https://github.com/langara/UWidgets",
    )

    val AreaKim = langaraLibDetails(
        name = "AreaKim",
        description = "Kotlin Vim Area for Compose Multiplatform",
        githubUrl = "https://github.com/langara/AreaKim",
    )

    val MyStolenPlaygrounds = langaraLibDetails(
        name = "MyStolenPlaygrounds",
        description = "Collection of Compose related samples, ui tests etc.",
        githubUrl = "https://github.com/langara/MyStolenPlaygrounds",
    )
}

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
    val version: Ver? = null, // FIXME: change it to non nullable after first refactoring (and remove all ?: error(..))
) {
    fun withVer(version: Ver?) = copy(version = version)

    @Deprecated("Use DepsNew")
    fun dep(moduleName: String = name.lowercase(Locale.US), moduleGroup: String = group, moduleVersion: String = version?.ver ?: error("No version for lib $name provided")) =
        "$moduleGroup:$moduleName:$moduleVersion"
}

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
    version: Ver? = null,
) = LibDetails(name, group, description, authorId, authorName, authorEmail, githubUrl, licenceName, licenceUrl, version)