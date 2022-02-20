package pl.mareklangiewicz.sourcefun

import okio.FileSystem.Companion.RESOURCES
import okio.Path.Companion.toPath
import org.gradle.kotlin.dsl.*
import org.gradle.testfixtures.*
import org.gradle.testkit.runner.*
import org.gradle.testkit.runner.TaskOutcome.*
import org.junit.jupiter.api.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.uspek.*
import java.io.*

class SourceFunTests {

    @TestFactory
    fun sourceFunTests() = uspekTestFactory {

        "Example test with ProjectBuilder" o {
            val project = ProjectBuilder.builder().build()!!
            project.pluginManager.apply(SourceFunPlugin::class)
            // TODO_maybe: how to configure my plugin in such test? (so I can assert it creates appropriate tasks)
            project.plugins.any { it is SourceFunPlugin } eq true
        }

        "On create temp project dir" o {
            withTempBuildEnvironment { tempDir, settingsFile, buildFile ->
                onSingleHelloWorld(tempDir, settingsFile, buildFile)
                onSourceFunPlugin(tempDir, settingsFile, buildFile)
            }
        }
    }
}

private fun withTempBuildEnvironment(code: (tempDir: File, settingsFile: File, buildFile: File) -> Unit) {
    withTempDir { tempDir ->
        val settingsFile = File(tempDir, "settings.gradle.kts")
        settingsFile.createNewFile() || error("Can not create file: $settingsFile")
        val buildFile = File(tempDir, "build.gradle.kts")
        buildFile.createNewFile() || error("Can not create file: $buildFile")
        code(tempDir, settingsFile, buildFile)
    }
}

private fun withTempDir(tempDirPrefix: String = "uspek", code: (tempDir: File) -> Unit) {
    lateinit var tempDir: File
    try {
        tempDir = File.createTempFile(tempDirPrefix, null).apply {
            delete() || error("Can not delete temp file: $this")
            mkdir() || error("Can not create dir: $this")
        }
        code(tempDir)
    } finally {
        tempDir.deleteRecursively() || error("Can not delete recursively dir: $tempDir")
    }
}

private fun onSingleHelloWorld(tempDir: File, settingsFile: File, buildFile: File) {
    "On single hello world project" o {
        settingsFile.writeText(
            """
            rootProject.name = "hello-world"
        """.trimIndent()
        )

        "On build file with example tasks" o {
            buildFile.writeText(
                """
                tasks.register("helloWorld") {
                    doLast {
                        println("Hello world!")
                    }
                }
                
                tasks.register("helloFail") {
                    doLast {
                        println("The exception is coming!")
                        throw RuntimeException("helloFail exception")
                    }
                }
            """.trimIndent()
            )

            "On gradle runner with temp dir" o {
                val runner = GradleRunner.create().withProjectDir(tempDir)

                "On task helloWorld" o {
                    runner.withArguments("helloWorld")

                    "On gradle build" o {
                        val result = runner.build()

                        "task helloWorld ends successfully" o { result.task(":helloWorld")?.outcome eq SUCCESS }
                        "output contains hello world message" o { result.output.contains("Hello world!") eq true }
                    }
                }

                "On nonexistent task" o {
                    runner.withArguments("someNonExistentTask")

                    "gradle fails" o { runner.buildAndFail() }
                }

                "On task helloFail" o {
                    runner.withArguments("helloFail")
                    "On gradle failing build" o {
                        val result = runner.buildAndFail()

                        "task helloFail ends with failure" o { result.task(":helloFail")?.outcome eq FAILED }
                        "output contains hello fail message" o { result.output.contains("The exception is coming!") eq true }
                    }
                }
            }
        }
    }
}

private val projectResPath = "sample-sourcefun".toPath()

private fun onSourceFunPlugin(tempDir: File, settingsFile: File, buildFile: File) {
    "On single project with SourceFunPlugin" o {
        settingsFile.writeText(RESOURCES.readUtf8(projectResPath / "settings.gradle.kts"))

        "On build file with explicit SourceFunTasks" o {
            buildFile.writeText(RESOURCES.readUtf8(projectResPath / "build.gradle.kts"))

            "On gradle runner with temp dir" o {
                val runner = GradleRunner.create().withPluginClasspath().withProjectDir(tempDir)

                "On task funTask1" o {
                    runner.withArguments("funTask1")

                    "On gradle build" o {
                        val result = runner.build()

                        "task funTask1 ends with NO_SOURCE" o { result.task(":funTask1")?.outcome eq NO_SOURCE }
                    }
                }
            }
        }
    }
}
