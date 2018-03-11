plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.sample.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib)
    testImplementation(Deps.junit)
}

