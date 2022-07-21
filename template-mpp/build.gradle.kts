import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*

plugins {
    id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin
    kotlin("multiplatform") apply false
}

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                using(module("androidx.compose.compiler:compiler:1.2.1-dev-k1.7.10-27cf0868d10"))
            }
        }
    }
    repositories {
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}


defaultGroupAndVerAndDescription(libs.TemplateMPP)

defaultSonatypeOssStuffFromSystemEnvs()

tasks.registerAllThatGroupFun("inject", ::checkTemplates, ::injectTemplates)

fun checkTemplates() = checkAllKnownRegionsInProject()
fun injectTemplates() = injectAllKnownRegionsInProject()

// region [Root Build Template]

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_key
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See deps.kt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun Project.defaultSonatypeOssStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") {
    ext.addAllFromSystemEnvs(envKeyMatchPrefix)
    defaultSonatypeOssNexusPublishing()
}

fun Project.defaultSonatypeOssNexusPublishing(
    sonatypeStagingProfileId: String = rootExt("sonatypeStagingProfileId"),
    ossrhUsername: String = rootExt("ossrhUsername"),
    ossrhPassword: String = rootExt("ossrhPassword"),
) = nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            stagingProfileId put sonatypeStagingProfileId
            username put ossrhUsername
            password put ossrhPassword
            nexusUrl put uri(repos.sonatypeOssNexus)
            snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
        }
    }
}

// endregion [Root Build Template]