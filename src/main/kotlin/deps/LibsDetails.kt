@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

import pl.mareklangiewicz.defaults.*
import java.util.*

object LibsDetails {

    val USpek = lib(
        name = "USpek",
        version = v(0, 0, 25),
        description = "Micro tool for testing with syntax similar to Spek, but shorter.",
        githubUrl = "https://github.com/langara/USpek",
    )

    val SMokK = lib(
        name = "SMokK",
        version = v(0, 0, 5),
        description = "A bit scary library for mocking suspendable functions in Kotlin :-)",
        githubUrl = "https://github.com/langara/SMokK",
    )

    val RxMock = lib(
        name = "RxMock",
        version = v(0, 0, 3),
        description = "Tiny library for mocking RxJava calls.",
        githubUrl = "https://github.com/langara/RxMock",
    )

    val KommandLine = lib(
        name = "KommandLine",
        version = v(0, 0, 7),
        description = "Kotlin DSL for popular CLI commands.",
        githubUrl = "https://github.com/langara/KommandLine",
    )

    val UPue = lib(
        name = "UPue",
        version = v(0, 0, 10),
        description = "Micro Multiplatform Reactive Library.",
        githubUrl = "https://github.com/langara/UPue",
    )

    val AbcdK = lib(
        name = "AbcdK",
        version = v(0, 0, 7),
        description = "Tiny unions lib for Kotlin.",
        githubUrl = "https://github.com/langara/AbcdK",
    )

    val TupleK = lib(
        name = "TupleK",
        version = v(0, 0, 6),
        description = "Tiny tuples lib for Kotlin with cool infix syntax.",
        githubUrl = "https://github.com/langara/TupleK",
    )

    val UWidgets = lib(
        name = "UWidgets",
        version = v(0, 0, 1),
        description = "Micro widgets for Compose Multiplatform",
        githubUrl = "https://github.com/langara/UWidgets",
    )

    val AreaKim = lib(
        name = "AreaKim",
        version = v(0, 0, 1),
        description = "Kotlin Vim Area for Compose Multiplatform",
        githubUrl = "https://github.com/langara/AreaKim",
    )

    val MyStolenPlaygrounds = lib(
        name = "MyStolenPlaygrounds",
        version = v(0, 0, 1),
        description = "Collection of Compose related samples, ui tests etc.",
        githubUrl = "https://github.com/langara/MyStolenPlaygrounds",
    )

    val Kthreelhu = lib(
        name = "Kthreelhu",
        version = v(0, 0, 2),
        description = "Dirty experiments with Kotlin + Three.js + Compose",
        githubUrl = "https://github.com/langara/Kthreelhu",
    )

    val TemplateMPP = lib(
        name = "TemplateMPP",
        version = v(0, 0, 2),
        description = "Template for multi platform projects.",
        githubUrl = "https://github.com/langara/deps.kt/template-mpp",
    )

    val TemplateAndro = lib(
        name = "TemplateAndro",
        version = v(0, 0, 4),
        description = "Template for android projects.",
        githubUrl = "https://github.com/langara/deps.kt/template-andro",
    )

    val Unknown = lib(
        name = "Unknown",
        version = v(0, 0, 1),
        description = "TODO: Define details.",
        githubUrl = "https://github.com/langara",
    )
}

data class LibDetails(
    val name: String,
    val group: String,
    val version: String,
    val description: String,
    val authorId: String, // unique id in SCM like github
    val authorName: String,
    val authorEmail: String,
    val githubUrl: String,
    val licenceName: String,
    val licenceUrl: String,
) {
    fun dep(moduleName: String = name.lowercase(Locale.US), moduleGroup: String = group, moduleVersion: String = version) =
        "$moduleGroup:$moduleName:$moduleVersion"
}

private fun lib(
    name: String,
    group: String = "pl.mareklangiewicz",
    version: String = v(0, 0, 1),
    description: String = "",
    authorId: String = "langara",
    authorName: String = "Marek Langiewicz",
    authorEmail: String = "marek.langiewicz@gmail.com",
    githubUrl: String = "https://github.com/langara",
    licenceName: String = "Apache-2.0",
    licenceUrl: String = "https://opensource.org/licenses/Apache-2.0",
) = LibDetails(name, group, version, description, authorId, authorName, authorEmail, githubUrl, licenceName, licenceUrl)