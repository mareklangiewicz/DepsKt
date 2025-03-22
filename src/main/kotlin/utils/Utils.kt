package pl.mareklangiewicz.utils

import kotlin.properties.*
import kotlin.reflect.*
import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import org.gradle.api.*
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.initialization.*
import org.gradle.api.plugins.*
import org.gradle.api.plugins.ExtraPropertiesExtension.UnknownPropertyException
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import pl.mareklangiewicz.deps.*


fun String.toVerIntCode() = split(".").let {
  0 +
    it[0].toVersionPartIntCode() * 10_000 * 10_000 +
    it[1].toVersionPartIntCode() * 10_000 +
    it[2].toVersionPartIntCode() * 1
}

/**
 * We don't want any fancy interpretation of suffixes, because the result has to be monotonically increasing!
 * We also don't want to accept weird suffixes, just accept and ignore reasonable suffixes.
 */
fun String.toVersionPartIntCode(): Int = Regex("""0*(?<VersionPart>\d{1,4})[\w\-]*""").matchEntire(this)
    ?.groups["VersionPart"]?.value?.toInt() ?: error("Can't parse version part: '$this'.")

// Overloads for setting properties in more typesafe and explicit ways (and fewer parentheses)
// (Property.set usage in gradle kotlin dsl doesn't look great, so we need to fix it with some infix fun)
// UPDATE: there is new overridden "=" (assign) operator in new gradle with special kotlin plugin...
// but my infix "provides" and "put" are a bit more explicit, so maybe still better when reading scripts?
// On the other hand, gradle says:
// We do not recommended that plugin authors implement custom setters for properties with these types.
// So maybe I'll deprecate these once gradle lazy assignment becomes stable (it's incubating).
// See also: https://blog.gradle.org/simpler-kotlin-dsl-property-assignment

// The name "provides" looks better than "provide", because it's more declarative/lazy overload of Property.set
infix fun <T> Property<in T>.provides(from: Provider<out T>) = set(from)

// The name "put" looks best because we need something short and different from "set",
// and the property is actually a kind of container we can "put" stuff into.
infix fun <T> Property<in T>.put(value: T) = set(value)

fun <T, R> Provider<T>.providing(compute: (T) -> R) =
  ReadOnlyProperty<Any?, R> { _, _ -> compute(get()) }

// yes, this name is stupid :)
fun <T> Property<T>.properting() = object : ReadWriteProperty<Any?, T> {
  override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}

// Wrapper for extra properties to have better map-like DSL in kotlin (in groovy they have something similar)
@Suppress("UNCHECKED_CAST")
@JvmInline
value class UExt<T>(private val extraProperties: ExtraPropertiesExtension) {
  operator fun get(name: String): T = extraProperties.get(name) as T
  operator fun set(name: String, value: T) {
    extraProperties.set(name, value)
  }
}

fun <T> ExtensionAware.ext(): UExt<T> = UExt(extensions.extraProperties)

val ExtensionAware.extString get() = ext<String>()
val Project.rootExtString get() = rootProject.extString

fun readFileUtf8(fileName: String): String = SYSTEM.read(fileName.toPath()) { readUtf8() }

fun readFileUtf8TryOrNull(fileName: String): String? =
  try {
    readFileUtf8(fileName)
  } catch (e: Exception) {
    null
  }

fun Project.rootExtReadFileUtf8(extPropName: String): String = readFileUtf8(rootExtString[name])

fun Project.rootExtReadFileUtf8TryOrNull(name: String): String? =
  try {
    rootExtReadFileUtf8(name)
  } catch (e: Exception) {
    null
  }

fun Project.extSetFromLazyFile(prop: String, suffix: String = "_LAZY_FILE") {
  val file = findProperty("$prop$suffix")?.toString() ?: error("Missing $prop$suffix property.")
  extString[prop] = readFileUtf8(file)
}

val Project.projectPath get() = rootDir.toOkioPath()
val Project.rootProjectPath get() = rootProject.projectPath
val Settings.rootProjectPath get() = rootProject.projectDir.toOkioPath()

val Project.buildPath: Path get() = layout.buildDirectory.get().asFile.toOkioPath()

// Kinda hack to attach some lib details to some global project or sth
var ExtensionAware.extLibDetails: LibDetails
  get() = ext<LibDetails>()["LibDetails"]
  set(value) {
    ext<LibDetails>()["LibDetails"] = value
  }

var Project.rootExtLibDetails
  get() = rootProject.extLibDetails
  set(value) {
    rootProject.extLibDetails = value
  }

class LibDetailsNotFoundException(msg: String? = null) : RuntimeException(msg)

fun Project.findExtLibDetails(): LibDetails =
  try { extLibDetails } catch (e: UnknownPropertyException) {
    parent?.findExtLibDetails() ?: throw LibDetailsNotFoundException("LibDetails ext not found in project hierarchy.")
  }

// https://publicobject.com/2021/03/11/includebuild/
fun Settings.includeAndSubstituteBuild(rootProject: Any, substituteModule: String, withProject: String) {
  includeBuild(rootProject) {
    it.dependencySubstitution {
      it.substitute(it.module(substituteModule))
        .using(it.project(withProject))
    }
  }
}


fun DependencyHandler.addAll(configuration: String, vararg deps: Dep?) {
  for (dep in deps) if (dep != null) add(configuration, dep)
}

fun DependencyHandler.addAllWithVer(configuration: String, ver: Ver, vararg deps: Dep?) {
  for (dep in deps) if (dep != null) add(configuration, dep.withVer(ver))
}

fun DependencyHandler.addAllWithNoVer(configuration: String, vararg deps: Dep?) {
  for (dep in deps) if (dep != null) add(configuration, dep.withNoVer())
}


fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: KCallable<Unit>) {
  val pairs: List<Pair<String, () -> Unit>> = afun.map { it.name to { it.call() } }
  registerAllThatGroupFun(group, *pairs.toTypedArray())
}

fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: Pair<String, () -> Unit>) {
  for ((name, code) in afun) register(name) { it.group = group; it.doLast { code() } }
}

/**
 * Copy a bunch of environment variables to project extra properties
 * @param envKeyMatchPrefix All variables with this prefix will be copied.
 * @param envKeyReplace Default implementation drops prefix and changes all "_" to ".".
 */
fun ExtraPropertiesExtension.addAllFromSystemEnvs(
  envKeyMatchPrefix: String,
  envKeyReplace: (envKey: String) -> String = { it.removePrefix(envKeyMatchPrefix).replace('_', '.') },
) {
  val envs = System.getenv()
  val keys = envs.keys.filter { it.startsWith(envKeyMatchPrefix) }
  for (key in keys) this[envKeyReplace(key)] = envs[key]
}
