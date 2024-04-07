import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import pl.mareklangiewicz.deps.Dep
import pl.mareklangiewicz.deps.Ver

// package has to be default - to be available even in buildscript blocks, like: plugins {...}

val repos get() = pl.mareklangiewicz.deps.Repos

val plugs get() = pl.mareklangiewicz.deps.Plugs

val vers get() = pl.mareklangiewicz.deps.Vers

infix fun PluginDependencySpec.ver(v: Ver) = version(v.ver)

fun PluginDependenciesSpec.plug(dep: Dep): PluginDependencySpec {
  check(dep.name.endsWith("plugin")) { "It doesn't look like a gradle plugin: $dep" }
  val v = dep.ver?.ver ?: return id(dep.group)
  return id(dep.group).version(v)
}

fun PluginDependenciesSpec.plugAll(vararg deps: Dep) {
  for (d in deps) plug(d)
}

fun PluginDependenciesSpec.plugDefaultForRoot() {
  plug(plugs.NexusPublish)
  plug(plugs.KotlinMulti).apply(false)
}

fun PluginDependenciesSpec.plugDefaultForMppModule() {
  plugAll(plugs.KotlinMulti, plugs.MavenPublish, plugs.Signing)
}
