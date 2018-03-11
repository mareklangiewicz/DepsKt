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

task("printVersAndDeps") {
    doLast {
        println("vers:")
        Vers::class.memberProperties.forEach { it.print() }
        println("deps:")
        Deps::class.memberProperties.forEach { it.print() }
    }
}

inline fun <reified T> KProperty1<T, *>.print() = println("\t$name: ${getter.call(T::class.objectInstance)}")
