@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

import java.util.*

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
    val version: Ver,
) {
    fun withVer(version: Ver) = copy(version = version)
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
    version: Ver = Ver(0, 0, 1),
) = LibDetails(name, group, description, authorId, authorName, authorEmail, githubUrl, licenceName, licenceUrl, version)