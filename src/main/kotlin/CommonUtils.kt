import okio.Path.Companion.toOkioPath
import org.gradle.api.*
import org.gradle.api.initialization.*
import org.gradle.api.provider.*
import org.gradle.kotlin.dsl.*


// Overloads for setting properties in more typesafe and explicit ways (and fewer parentheses)
// (Property.set usage in gradle kotlin dsl doesn't look great, so we need to fix it with some infix fun)

// The name "provides" looks better than "provide", because it's more declarative/lazy overload of Property.set
infix fun <T> Property<in T>.provides(from: Provider<out T>) = set(from)

// The name "put" looks best because we need something short and different from "set",
// and the property is actually a kind of container we can "put" stuff into.
infix fun <T> Property<in T>.put(value: T) = set(value)

fun Project.rootExt(name: String) = rootProject.extra[name]!!.toString()
fun Project.rootExtOrNull(name: String) = rootProject.extra[name]?.toString()

val Project.rootOkioPath get() = rootProject.rootDir.toOkioPath()
val Settings.rootOkioPath get() = rootProject.projectDir.toOkioPath()
