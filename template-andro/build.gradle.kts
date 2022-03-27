import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.defaults.*

plugins { id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin }

defaultGroupAndVerAndDescription(libs.TemplateAndro)

defaultSonatypeOssStuffFromSystemEnvs()


private val rootBuild = rootProjectPath / "build.gradle.kts"
private val libBuild = rootProjectPath / "template-andro-lib" / "build.gradle.kts"
private val appBuild = rootProjectPath / "template-andro-app" / "build.gradle.kts"

fun injectTemplates() {
    injectRootBuildTemplate(rootBuild)
    injectKotlinModuleBuildTemplate(libBuild, appBuild)
    injectAndroCommonBuildTemplate(libBuild, appBuild)
    injectAndroLibBuildTemplate(libBuild)
    injectAndroAppBuildTemplate(appBuild)
}

fun checkTemplates() {
    checkRootBuildTemplate(rootBuild)
    checkKotlinModuleBuildTemplates(libBuild, appBuild)
    checkAndroCommonBuildTemplates(libBuild, appBuild)
    checkAndroLibBuildTemplates(libBuild)
    checkAndroAppBuildTemplates(appBuild)
}

tasks.registerAllThatGroupFun("inject", ::checkTemplates, ::injectTemplates)

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

// endregion [Root Build Template]