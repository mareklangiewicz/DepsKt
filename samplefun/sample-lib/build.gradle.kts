
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
// repo now does. The Lib comes from gradle.extLib (set in ../settings.gradle.kts), so this call
// takes no arguments at all.
defaultBuildTemplateForBasicMppLib()
