import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import pl.mareklangiewicz.deps.Dep
import pl.mareklangiewicz.deps.Ver

// package has to be default - to be available even in buildscript blocks, like: plugins {...}

@Deprecated("Use DepsNew")
val libsOld = pl.mareklangiewicz.deps.LibsDetails
@Deprecated("Use DepsNew")
val depsOld = pl.mareklangiewicz.deps.DepsOld

val repos = pl.mareklangiewicz.deps.Repos
@Deprecated("Use versNew")
val versOld = pl.mareklangiewicz.deps.VersOld

val plugs get() = pl.mareklangiewicz.deps.Plugs

val versNew get() = pl.mareklangiewicz.deps.VersNew

infix fun PluginDependencySpec.ver(v: Ver) = version(v.ver)

fun PluginDependenciesSpec.plug(dep: Dep): PluginDependencySpec {
    check(dep.name.endsWith("plugin")) { "It doesn't look like a gradle plugin: $dep" }
    val v = dep.ver?.ver ?: return id(dep.group)
    return id(dep.group).version(v)
}

fun PluginDependenciesSpec.plugAll(vararg deps: Dep) { for (d in deps) plug(d) }
