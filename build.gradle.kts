import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(Deps.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

task("logVersAndDeps") {
    doLast {
        println("vers:"); Vers::class.memberProperties.forEach(::log)
        println("deps:"); Deps::class.memberProperties.forEach(::log)
    }
}

inline fun <reified T> log(property: KProperty1<T, *>) = println(
        "\t${property.name}: ${property.getter.call(T::class.objectInstance)}"
)
