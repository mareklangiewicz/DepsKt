plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.sample.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib8)
    testImplementation(Deps.junit5)
    testImplementation(Deps.junit5engine)
}

tasks.test {
    useJUnitPlatform()
}

