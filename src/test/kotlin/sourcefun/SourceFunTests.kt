package pl.mareklangiewicz.sourcefun

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import org.gradle.testfixtures.*
import org.gradle.testkit.runner.*
import org.gradle.testkit.runner.TaskOutcome.*
import org.junit.jupiter.api.*
import pl.mareklangiewicz.bad.chkEq
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.uspek.*

@Disabled // FIXME LATER
class SourceFunTests {

  @TestFactory
  fun sourceFunTests() = uspekTestFactory {
    onExampleWithProjectBuilder()
    onSingleHelloWorldProject()
    onSampleSourceFunProject()
  }
}

private fun onExampleWithProjectBuilder() {
  "On example with ProjectBuilder" o {
    val project = ProjectBuilder.builder().build()
    project.pluginManager.apply(SourceFunPlugin::class.java)
    // TODO_maybe: how to configure my plugin in such test? (so I can assert it creates appropriate tasks)
    project.plugins.any { it is SourceFunPlugin } chkEq true
  }
}

@Suppress("SameParameterValue")
private fun withTempProject(settingsKtsContent: String, buildKtsContent: String, code: (tempDir: Path) -> Unit) =
  SYSTEM.withTempDir("sourceFunTest") { tempDir ->
    writeUtf8(tempDir / "settings.gradle.kts", settingsKtsContent)
    writeUtf8(tempDir / "build.gradle.kts", buildKtsContent)
    code(tempDir)
  }

private fun onSingleHelloWorldProject() {
  "On single hello world project" o {
    val settingsKtsContent = """rootProject.name = "hello-world""""
    val buildKtsContent =
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


    withTempProject(settingsKtsContent, buildKtsContent) { tempDir ->
      "On gradle runner within temp environment" o {

        val runner = GradleRunner.create().withProjectPath(tempDir)
        //.withPluginClasspath() // it's automatically added by java-gradle-plugin

        "On task helloWorld" o {
          runner.withArguments("helloWorld")

          "On gradle build" o {
            val result = runner.build()

            "task helloWorld ends successfully" o { result.task(":helloWorld")?.outcome chkEq SUCCESS }
            "output contains hello world message" o { result.output.contains("Hello world!") chkEq true }
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

            "task helloFail ends with failure" o { result.task(":helloFail")?.outcome chkEq FAILED }
            "output contains hello fail message" o { result.output.contains("The exception is coming!") chkEq true }
          }
        }
      }
    }
  }
}

// FIXME: do not hardcode my local paths
private val sampleSourceFunProjectPath = "/home/marek/code/kotlin/DepsKt/sample-sourcefun".toPath()

private fun onSampleSourceFunProject() {
  "On sample-sourcefun project" o {

    val runner = GradleRunner.create().withProjectPath(sampleSourceFunProjectPath)
    //.withPluginClasspath() // it's automatically added by java-gradle-plugin

    "On gradle tasks command" o {
      runner.withArguments("tasks")
      val result = runner.build()

      "All awesome tasks printed" o {
        val lines = result.output.lines()
        val idx = lines.indexOf("Awesome tasks")
        check(idx > 0)
        lines[idx + 2] chkEq "processExtensions1"
        lines[idx + 3] chkEq "processExtensions2deprecated"
        lines[idx + 4] chkEq "reportStuff1"
        lines[idx + 5] chkEq "reportStuff2"
      }
    }

    "On clean gradle cache and build dir programmatically" o {
      SYSTEM.deleteRecursively(sampleSourceFunProjectPath / ".gradle")
      SYSTEM.deleteRecursively(sampleSourceFunProjectPath / "build")

      "On task processExtensions1" o {
        runner.withArguments("processExtensions1")
        val result = runner.build()

        "task processExtensions1 ends with SUCCESS" o { result.task(":processExtensions1")?.outcome chkEq SUCCESS }

        // TODO_later: mess with generated source code and check if processExtensions1 fixes it.
      }

      "On task reportStuff1" o {
        runner.withArguments("reportStuff1")
        val result = runner.build()

        "task reportStuff1 ends with SUCCESS" o { result.task(":reportStuff1")?.outcome chkEq SUCCESS }
      }

      "On task reportStuff2" o {
        runner.withArguments("reportStuff2")
        val result = runner.build()

        "task reportStuff2 ends with SUCCESS" o { result.task(":reportStuff2")?.outcome chkEq SUCCESS }

        "On generated reports" o {
          val reportsPaths = SYSTEM.list(sampleSourceFunProjectPath / "build/awesome-reports")
          val reportsNames = reportsPaths.map { it.name }

          "generated two files" o { reportsNames chkEq listOf("GenericExtensions.kt", "SpecialExtensions.kt") }

          for (reportPath in reportsPaths) "On report file ${reportPath.name}" o {
            val content = SYSTEM.readUtf8(reportPath)

            "no Array word in it" o { Regex("Array").containsMatchIn(content) chkEq false }
            "some XXX words instead" o { check(Regex("XXX").findAll(content).count() > 2) }
          }
        }
      }
    }
  }
}

private fun GradleRunner.withProjectPath(path: Path) = withProjectDir(path.toFile())
