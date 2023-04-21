import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*

plugins {
    plug(plugs.NexusPublish)
    plug(plugs.AndroLib) apply false
    plug(plugs.AndroApp) apply false
    plug(plugs.KotlinAndro) apply false
}

defaultBuildTemplateForRootProject(
    langaraLibDetails(
        name = "TemplateAndro",
        description = "Template for android projects.",
        githubUrl = "https://github.com/langara/deps.kt/template-andro",
        version = Ver(0, 0, 6)
    )
)

// region [Root Build Template]

fun Project.defaultBuildTemplateForRootProject(ossLibDetails: LibDetails? = null) {

    ossLibDetails?.let {
        rootExtLibDetails = it
        defaultGroupAndVerAndDescription(it)
        defaultSonatypeOssStuffFromSystemEnvs()
    }

    // kinda workaround for kinda issue with kotlin native
    // https://youtrack.jetbrains.com/issue/KT-48410/Sync-failed.-Could-not-determine-the-dependencies-of-task-commonizeNativeDistribution.#focus=Comments-27-5144160.0-0
    repositories { mavenCentral() }

    tasks.registerAllThatGroupFun("inject", ::checkTemplates, ::injectTemplates)
}

fun checkTemplates() = checkAllKnownRegionsInProject(projectPath)
fun injectTemplates() = injectAllKnownRegionsInProject(projectPath)

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile (or MYKOTLIBS_signing_key with whole signing key)
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See DepsKt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun Project.defaultSonatypeOssStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") {
    ext.addAllFromSystemEnvs(envKeyMatchPrefix)
    defaultSonatypeOssNexusPublishing()
}

fun Project.defaultSonatypeOssNexusPublishing(
    sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
    ossrhUsername: String = rootExtString["ossrhUsername"],
    ossrhPassword: String = rootExtString["ossrhPassword"],
) {
    nexusPublishing {
        this.repositories {
            sonatype {  // only for users registered in Sonatype after 24 Feb 2021
                stagingProfileId put sonatypeStagingProfileId
                username put ossrhUsername
                password put ossrhPassword
                nexusUrl put uri(repos.sonatypeOssNexus)
                snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
            }
        }
    }
}

// endregion [Root Build Template]