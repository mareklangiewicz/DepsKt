import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

plugins {
    id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin
}

defaultGroupAndVerAndDescription(libs.TemplateMPP)

ext.addAllFromSystemEnvs("MYKOTLIBS_")

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            stagingProfileId put rootExt("sonatypeStagingProfileId")
            username put rootExt("ossrhUsername")
            password put rootExt("ossrhPassword")
            nexusUrl put uri(repos.sonatypeOssNexus)
            snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
        }
    }
}