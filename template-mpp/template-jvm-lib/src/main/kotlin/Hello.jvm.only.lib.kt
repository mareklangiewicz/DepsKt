package pl.mareklangiewicz.hello

fun helloFromJvmOnlyLib() = "Hello from JVM Only Lib! (kotlin: ${KotlinVersion.CURRENT})".also { println(it) }
