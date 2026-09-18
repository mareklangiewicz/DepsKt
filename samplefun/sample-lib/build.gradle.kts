
// region [[Basic MPP Lib Build Imports and Plugs]]

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plugAll(plugs.KotlinMulti, plugs.VannikPublish)
  plug(plugs.TemplateFun)
}

// endregion [[Basic MPP Lib Build Imports and Plugs]]

// The [[Kotlin Module Build Template]] and [[MPP Module Build Template]] regions used to be copied
// in here. They are gone: this script applies the templatefun plugin instead, which is what every
// repo now does. The Lib comes from gradle.extLib (set in ../settings.gradle.kts), so the only
// argument is the one thing gradle.extLib deliberately cannot carry: this module's publish intent.
// publish = LibPublish() is the opt-in: publications exist (so `publishToMavenLocal` works and this
// build exercises the real path), and toCentral stays false, so no Maven Central task is ever
// registered. Passing nothing here while plugs.VannikPublish is applied is now a configuration
// ERROR rather than a silent publication -- which is the whole point of the redesign.
defaultBuildTemplateForBasicMppLib(publish = LibPublish())
