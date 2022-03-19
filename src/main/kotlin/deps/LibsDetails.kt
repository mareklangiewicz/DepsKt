@file:Suppress("PackageDirectoryMismatch", "unused")

package pl.mareklangiewicz.deps

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
    fun dep(moduleName: String = name.toLowerCase(), moduleGroup: String = group, moduleVersion: String = version) =
        "$moduleName:$moduleGroup:$moduleVersion"
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

object LibsDetails {
    val uspek = lib(name = "USpek", version = v(0, 0, 22), description = "Micro tool for testing with syntax similar to Spek, but shorter.")
}
