plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.20.0"
    // https://plugins.gradle.org/docs/publish-plugin
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    api("com.squareup.okio:okio:3.0.0")
//    implementation(gradleApi())

//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    // testApi(gradleTestKit()) // this is automatically added by java-gradle-plugin
    testImplementation("pl.mareklangiewicz:uspekx:0.0.21") // TODO: try to use deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2") // TODO: try to use deps.uspek (see comment in settings)
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2") // TODO: try to use deps.uspek (see comment in settings)
    // TODO: check separation between api and engine - so I can do similar in ULog (with separate bridges to CLog etc)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        jvmTarget = "17"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

group = "pl.mareklangiewicz.deps"
version = "0.2.17"


gradlePlugin {
    plugins {
        create("depsPlugin") {
            id = "pl.mareklangiewicz.deps"
            implementationClass = "pl.mareklangiewicz.deps.DepsPlugin"
        }
        create("depsSettingsPlugin") {
            id = "pl.mareklangiewicz.deps.settings"
            implementationClass = "pl.mareklangiewicz.deps.DepsSettingsPlugin"
        }
        create("sourceFunPlugin") {
            id = "pl.mareklangiewicz.sourcefun"
            implementationClass = "pl.mareklangiewicz.sourcefun.SourceFunPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/langara/deps.kt"
    vcsUrl = "https://github.com/langara/deps.kt"
    tags = listOf("bom", "dependencies")
    description = "Updated dependencies for typical java/kotlin/android projects (with IDE support)."

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }

    (plugins) {
        "depsPlugin" { displayName = "Deps.kt plugin" } // id is captured from java-gradle-plugin configuration
        "depsSettingsPlugin" { displayName = "Deps.kt settings plugin" }
        "sourceFunPlugin" { displayName = "SourceFun plugin" }
    }
}
