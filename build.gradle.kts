import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Deps.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task("logVersAndDeps") {
    doLast {
        println("vers:"); Vers::class.declaredMemberProperties.forEach { Vers.log(it) }
        println("deps:"); Deps::class.declaredMemberProperties.forEach { Deps.log(it) }
    }
}

inline fun <reified T> T.log(property: KProperty1<T, *>) = println( "\t${property.name}: ${tryGet(property)}")

inline fun <reified T> T.tryGet(prop: KProperty1<T, *>) =
        try { prop.getter.call() }
        catch (e: Exception) { prop.getter.call(this) }
