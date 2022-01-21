import okio.Path.Companion.toPath
import pl.mareklangiewicz.deps.*

tasks.register("doCheckAndroAppBuildTemplates") {
    group = "inject"
    doLast {
        checkAndroBuildTemplates()
    }
}

tasks.register("doInjectAndroAppBuildTemplate") {
    group = "inject"
    doLast {
        injectAndroAppBuildTemplate("app/build.gradle.kts".toPath())
    }
}

tasks.register("doInjectAndroLibBuildTemplate") {
    group = "inject"
    doLast {
        injectAndroLibBuildTemplate("lib/build.gradle.kts".toPath())
    }
}
