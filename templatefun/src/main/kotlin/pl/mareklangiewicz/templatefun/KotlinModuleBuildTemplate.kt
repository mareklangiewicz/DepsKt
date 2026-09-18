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
fun MavenPom.defaultPOM(pomName: String? = null, pomDescription: String? = null) {
  name put (pomName ?: info.name)
  description put (pomDescription ?: info.description)
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
 * Configures publishing for ONE module. Reached only through a [LibPublish] scope, which is the
 * whole point: a module that is not published never opens the scope, so this cannot run for it.
 *
 * ### Why [LibPublish] and not a flag on [LibFlags]
 *
 * It used to be `context(info: LibInfo, flags: LibFlags)`, reading `flags.withCentralPublish`, and
 * it was called from every `defaultBuildTemplateFor*Lib` behind
 * `if (plugins.hasPlugin("com.vanniktech.maven.publish"))`. Both halves of that were wrong:
 *
 * - [LibFlags] is the object a module clones for PLATFORM reasons
 *   (`gradle.extLib.copy(flags = flags.copy(withJs = false))`), so cloning platform flags cloned
 *   publish intent. USpek's six sample apps inherited `withCentralPublish = true` this way and were
 *   one green build away from six permanent Maven Central coordinates.
 * - `hasPlugin` infers intent from the `plugins {}` block, which in these repos is region-marked
 *   boilerplate. Intent was being read from the most copy-pasted line in the file.
 *
 * Now the caller passes a [LibPublish] or does not. See `docs/design/publish-intent-per-module.md`.
 *
 * ### Why [LibPublish.artifactId] defaults to the project name
 *
 * [LibInfo] is a per-REPO value -- one `gradle.extLib` for the whole build -- while an artifactId is
 * a per-MODULE coordinate. `info.name` is the repo name (KGround's publishable modules all share
 * it; [defaultPOM] deliberately puts it in the POM `<name>` of every one of them), so defaulting to
 * it would make those modules publish the SAME artifactId, silently overwriting each other.
 * `info.id` is worse: it is a reverse-DNS identity slot (android applicationId / bundle id / plugin
 * id), so it would publish `group:pl.mareklangiewicz.deps.depskt`.
 *
 * `project.name` is the only per-module value in scope, so it is the default. The override exists
 * because a directory name and a published artifactId can legitimately disagree: `:deps` lives in
 * `./deps` but has always published as `DepsKt`, and `:sourcefun` as `SourceFun`.
 *
 * [LibPublish.pomName] and [LibPublish.pomDescription] are the other half of that split, and they
 * retire `:sourcefun`'s hand-rolled copy of this whole function.
 */
context(info: LibInfo, publish: LibPublish)
fun Project.defaultPublishing() = extensions.configure<MavenPublishBaseExtension> {
  propertiesTryOverride("signingInMemoryKey", "signingInMemoryKeyPassword", "mavenCentralPassword")
  if (publish.toCentral) publishToMavenCentral(automaticRelease = false)
  signAllPublications()
  signAllPublicationsFixSignatoryIfFound()
  coordinates(
    groupId = info.group,
    artifactId = publish.artifactId ?: name,
    version = info.version.str,
  )
  pom { defaultPOM(publish.pomName, publish.pomDescription) }
}

/**
 * The one place that decides whether a module publishes, shared by every `defaultBuildTemplateFor*`
 * entry point.
 *
 * Both mismatches are errors, and deliberately so. The migration to [LibPublish] makes every
 * publishable module opt in explicitly; a module that forgets would otherwise go quiet and ship
 * NOTHING on the next release, which is the failure mode this whole redesign exists to prevent.
 * So: the vanniktech plugin no longer GRANTS a publication, it is REQUIRED BY one.
 */
context(info: LibInfo)
fun Project.defaultPublishingOrNot(publish: LibPublish?, moduleKind: String) {
  val hasPlugin = plugins.hasPlugin("com.vanniktech.maven.publish")
  when {
    publish != null && !hasPlugin -> error(
      "$moduleKind $name: publish = LibPublish(..) was given, but the " +
        "com.vanniktech.maven.publish plugin is not applied. Add plugs.VannikPublish to plugins {}."
    )
    publish == null && hasPlugin -> error(
      "$moduleKind $name: the com.vanniktech.maven.publish plugin is applied, but no " +
        "publish = LibPublish(..) was given, so nothing would be published. Pass one, or drop " +
        "plugs.VannikPublish from plugins {}."
    )
    publish != null -> context(publish) { defaultPublishing() }
    else -> logger.info("$moduleKind $name: not published.")
  }
}

// endregion [[Kotlin Module Build Template]]
