import okio.Path.Companion.toPath
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*

plugins { id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin }

// TODO NOW: injecting and checking like in template-android

defaultGroupAndVerAndDescription(libs.TemplateMPP)

defaultSonatypeOssStuffFromSystemEnvs()

fun injectMppTemplates() {
    injectRootBuildTemplate("build.gradle.kts".toPath())
    injectKotlinModuleBuildTemplate("template-mpp-lib/build.gradle.kts".toPath())
    injectMppModuleBuildTemplate("template-mpp-lib/build.gradle.kts".toPath())
}

fun checkMppTemplates() {
    checkRootBuildTemplate("build.gradle.kts".toPath())
    checkKotlinModuleBuildTemplates("template-mpp-lib/build.gradle.kts".toPath())
    checkMppModuleBuildTemplates("template-mpp-lib/build.gradle.kts".toPath())
}

tasks.registerAllThatGroupFun("inject",
    ::checkMppTemplates,
    ::injectMppTemplates,
)

// region Root Build Template

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_key
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See template-mpp/lib/build.gradle.kts
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

// endregion Root Build Template