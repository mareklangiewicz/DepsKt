package pl.mareklangiewicz.utils

import okio.*
import okio.Path.Companion.toOkioPath
import org.gradle.api.*
import org.gradle.api.initialization.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import kotlin.reflect.*


// Overloads for setting properties in more typesafe and explicit ways (and fewer parentheses)
// (Property.set usage in gradle kotlin dsl doesn't look great, so we need to fix it with some infix fun)

// The name "provides" looks better than "provide", because it's more declarative/lazy overload of Property.set
infix fun <T> Property<in T>.provides(from: Provider<out T>) = set(from)

// The name "put" looks best because we need something short and different from "set",
// and the property is actually a kind of container we can "put" stuff into.
infix fun <T> Property<in T>.put(value: T) = set(value)

fun Project.rootExt(name: String) = rootProject.extra[name]!!.toString()
fun Project.rootExtOrNull(name: String) = rootProject.extra[name]?.toString()

val Project.rootProjectPath get() = rootProject.rootDir.toOkioPath()
val Settings.rootProjectPath get() = rootProject.projectDir.toOkioPath()

fun SourceTask.addSource(path: Path) = source(path.toFile())


// https://publicobject.com/2021/03/11/includebuild/

fun Settings.includeAndSubstituteBuild(rootProject: Any, substituteModule: String, withProject: String) {
    includeBuild(rootProject) {
        dependencySubstitution {
            substitute(module(substituteModule))
                .using(project(withProject))
        }
    }
}

fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: KCallable<Unit>) {
    val pairs: List<Pair<String, () -> Unit>> = afun.map { it.name to { it.call() } }
    registerAllThatGroupFun(group, *pairs.toTypedArray())
}

fun TaskContainer.registerAllThatGroupFun(group: String, vararg afun: Pair<String, () -> Unit>) {
    for ((name, code) in afun) register(name) { this.group = group; doLast { code() } }
}

