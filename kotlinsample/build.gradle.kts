plugins {
    application
//    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.uspek.MainKt"
}

dependencies {
    implementation(deps.kotlinStdlib)
    testImplementation(deps.junit)
}

