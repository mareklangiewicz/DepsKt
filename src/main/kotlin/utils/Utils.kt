package pl.mareklangiewicz.utils

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import org.gradle.api.*
import org.gradle.api.initialization.*
import org.gradle.api.plugins.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.plugin.use.PluginDependencySpec
import pl.mareklangiewicz.deps.LibDetails
import pl.mareklangiewicz.io.*
import kotlin.properties.*
import kotlin.reflect.*


infix fun PluginDependencySpec.ver(v: CharSequence) = version(v.toString())

// Overloads for setting properties in more typesafe and explicit ways (and fewer parentheses)
// (Property.set usage in gradle kotlin dsl doesn't look great, so we need to fix it with some infix fun)

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

// Wrapper for extra properties to have better map-like DSL in kotlin (in groove they have something similar)
@Suppress("UNCHECKED_CAST")
@JvmInline value class UExt<T>(private val extraProperties: ExtraPropertiesExtension) {
    operator fun get(name: String): T = extraProperties.get(name) as T
    operator fun set(name: String, value: T) { extraProperties.set(name, value) }

}

fun <T> ExtensionAware.ext(): UExt<T> = UExt(extensions.extraProperties)

val ExtensionAware.extString get() = ext<String>()
val Project.rootExtString get() = rootProject.extString
fun Project.rootExtReadFileUtf8(name: String) = SYSTEM.readUtf8(rootExtString[name].toPath())

val Project.projectPath get() = rootDir.toOkioPath()
val Project.rootProjectPath get() = rootProject.projectPath
val Settings.rootProjectPath get() = rootProject.projectDir.toOkioPath()

val Project.buildPath: Path get() = layout.buildDirectory.get().asFile.toOkioPath()

// Kinda hack to attach some lib details to some global project or sth
var ExtensionAware.extLibDetails: LibDetails
    get() = ext<LibDetails>()["LibDetails"]
    set(value) { ext<LibDetails>()["LibDetails"] = value }

var Project.rootExtLibDetails
    get() = rootProject.extLibDetails
    set(value) { rootProject.extLibDetails = value }

// https://publicobject.com/2021/03/11/includebuild/
fun Settings.includeAndSubstituteBuild(rootProject: Any, substituteModule: String, withProject: String) {
    includeBuild(rootProject) {
        it.dependencySubstitution {
            it.substitute(it.module(substituteModule))
                .using(it.project(withProject))
        }
    }
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
