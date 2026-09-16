package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.publish.maven.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[Kotlin Module Build Template]]

/**
 * MIGRATED to the sibling model. Was `context(settings: LibSettings)` + `with(settings.repos)` —
 * the design note's own example of "helpers reach through the tree". As a sibling there is nothing
 * to reach through: the repo settings arrive directly.
 *
 * The context parameter is `reposSettings`, NOT `repos`, on purpose. This body calls
 * `maven(repos.kotlinx)`, where `repos` is a top-level DepsKt object; a context parameter named
 * `repos` would take that name and break those calls. Worth remembering alongside probe 1 — a
 * context parameter does not shadow an extension RECEIVER, but it does occupy its own name.
 */
context(reposSettings: LibRepos)
fun RepositoryHandler.addRepos() = with(reposSettings) {
  @Suppress("DEPRECATION")
  if (withMavenLocal) mavenLocal()
  if (withMavenCentral) mavenCentral()
  if (withGradle) gradlePluginPortal()
  if (withGoogle) google()
  if (withKotlinx) maven(repos.kotlinx)
  if (withKotlinxHtml) maven(repos.kotlinxHtml)
  if (withComposeJbDev) maven(repos.composeJbDev)
  if (withKtorEap) maven(repos.ktorEap)
  if (withJitpack) maven(repos.jitpack)
}

// TODO_maybe: doc says it could be now also applied globally instead for each task (and it works for andro too)
//   But it's only for jvm+andro, so probably this is better:
//   https://kotlinlang.org/docs/gradle-compiler-options.html#for-all-kotlin-compilation-tasks
fun TaskCollection<Task>.defaultKotlinCompileOptions(
  apiVer: KotlinVersion = KotlinVersion.KOTLIN_2_1,
  jvmTargetVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
  renderInternalDiagnosticNames: Boolean = false,
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    apiVersion.set(apiVer)
    jvmTargetVer?.let { jvmTarget = JvmTarget.fromTarget(it) }
    if (renderInternalDiagnosticNames) freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    // useful, for example, to suppress some errors when accessing internal code from some library, like:
    // @file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
  }
}

fun KotlinMultiplatformExtension.defaultCompiler(
  kotlinVer: KotlinVersion = KotlinVersion.KOTLIN_2_3,
  jvmVer: Int? = null,
  renderInternalDiagnosticNames: Boolean = false,
) {
  compilerOptions {
    languageVersion.set(kotlinVer)
    apiVersion.set(kotlinVer)
    if (renderInternalDiagnosticNames) freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    freeCompilerArgs.add("-Xcontext-parameters")
    // useful, for example, to suppress some errors when accessing internal code from some library, like:
    // @file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
  }
  jvmVer?.let(::jvmToolchain)
}

fun TaskCollection<Task>.defaultTestsOptions(
  printStandardStreams: Boolean = true,
  printStackTraces: Boolean = true,
  onJvmUseJUnitPlatform: Boolean = true,
) = withType<AbstractTestTask>().configureEach {
  testLogging {
    showStandardStreams = printStandardStreams
    showStackTraces = printStackTraces
  }
  if (onJvmUseJUnitPlatform) (this as? Test)?.useJUnitPlatform()
}

// Provide artifacts information required by Maven Central
context(info: LibInfo)
fun MavenPom.defaultPOM() {
  name put info.name
  description put info.description
  url put info.githubUrl

  licenses {
    license {
      name put info.licenceName
      url put info.licenceUrl
    }
  }
  developers {
    developer {
      id put info.authorId
      name put info.authorName
      email put info.authorEmail
    }
  }
  scm { url put info.githubUrl }
}

/**
 * MIGRATED to the sibling model. The nested version reached through ONE field
 * (`details.settings.withCentralPublish`) for a single flag; as siblings that flag arrives as its
 * own context parameter, so this function names exactly the two things it uses and nothing else.
 *
 * Note it is still `context(..)` and not `with(..)`: [LibInfo] has a `name` too, and the
 * [artifactId] default must resolve to the PROJECT name. See [probeNameIsProjectName].
 *
 * ### Why [artifactId] defaults to the project name, and not to anything in [LibInfo]
 *
 * [LibInfo] is a per-REPO value -- one `gradle.extLib` for the whole build -- while an artifactId is
 * a per-MODULE coordinate. `info.name` is the repo name (KGround's ~12 publishable modules all share
 * it; [defaultPOM] deliberately puts it in the POM `<name>` of every one of them), so defaulting to
 * it would make those twelve modules publish the SAME artifactId, silently overwriting each other.
 * `info.id` is worse: it is a reverse-DNS identity slot (android applicationId / bundle id / plugin
 * id), so it would publish `group:pl.mareklangiewicz.deps.depskt`.
 *
 * `project.name` is the only per-module value in scope, so it is the default. [artifactId] exists
 * because a directory name and a published artifactId can legitimately disagree: `:deps` lives in
 * `./deps` but has always published as `DepsKt`, and `:sourcefun` as `SourceFun`. Before this
 * parameter existed the only way to say so was a SECOND `coordinates(..)` call after this function,
 * relying on last-call-wins -- silent the moment anything reordered it.
 *
 * TODO_someday: the same repo/module split affects the POM, not just the artifactId. `:sourcefun`
 * hand-rolls this whole block because it needs a module-specific POM `name` and `description` too.
 * If a third module ever wants that, the honest fix is to stop patching fields one at a time and
 * introduce a small per-module value -- something like `LibModule(artifactId, name, description)` --
 * as the missing counterpart to the per-repo [LibInfo]. Not worth a new type for one caller yet.
 */
context(info: LibInfo, flags: LibFlags)
fun Project.defaultPublishing(artifactId: String = name) = extensions.configure<MavenPublishBaseExtension> {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (flags.withCentralPublish) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  coordinates(groupId = info.group, artifactId = artifactId, version = info.version.str)
  pom { defaultPOM() }
}

// endregion [[Kotlin Module Build Template]]
