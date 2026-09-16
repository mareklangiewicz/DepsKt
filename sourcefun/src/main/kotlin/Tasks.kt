package pl.mareklangiewicz.sourcefun

import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.text.Regex
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineDispatcher
import okio.Path
import okio.Path.Companion.toOkioPath
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.bad.chkNN
import pl.mareklangiewicz.gradle.ulog.asULog
import pl.mareklangiewicz.io.readUtf8
import pl.mareklangiewicz.io.writeUtf8
import pl.mareklangiewicz.kground.io.*
import pl.mareklangiewicz.kground.plusIfNN
import pl.mareklangiewicz.kommand.CLI
import pl.mareklangiewicz.kommand.ax
import pl.mareklangiewicz.kommand.core.curlDownload
import pl.mareklangiewicz.kommand.getSysCLI
import pl.mareklangiewicz.kommand.git.gitHash
import pl.mareklangiewicz.kommand.kommand
import pl.mareklangiewicz.uctx.uctx
import pl.mareklangiewicz.ulog.ULog
import pl.mareklangiewicz.ure.UReplacement
import pl.mareklangiewicz.ure.core.Ure
import pl.mareklangiewicz.ure.replaceAll
import pl.mareklangiewicz.usubmit.USubmit

fun <T: Task> T.runWithUCtxForTask(action: suspend T.() -> Unit) = runBlocking { uctxForTask { action() } }

fun <T: Task> T.doLastWithUCtxForTask(action: suspend T.() -> Unit) = doLast { runWithUCtxForTask(action) }

fun <T: Task> T.doFirstWithUCtxForTask(action: suspend T.() -> Unit) = doFirst { runWithUCtxForTask(action) }

/** Setting some param explicitly to null means we don't add any (even default) to context. */
@OptIn(NotPortableApi::class)
suspend inline fun <T: Task, R> T.uctxForTask(
  context: CoroutineContext = EmptyCoroutineContext,
  coroutineName: String? = name,
  dispatcher: CoroutineDispatcher? = getSysDispatcherForIO(),
  fs: UFileSys? = getSysUFileSys(),
  cwd: UWorkDir? = fs?.getSysWorkingDir()?.let(::UWorkDir),
  cli: CLI? = getSysCLI(),
  log: ULog? = logger.asULog(alsoPrintLn = true),
  submit: USubmit? = null,
  // TODO_someday_maybe: some awesome configurable default (TUI?) Supervisor/USubmit for gradle?
  // BTW ZenitySupervisor as default USubmit in gradle is bad idea. It has to work on different systems/CIs.
  noinline block: suspend CoroutineScope.() -> R,
): R = uctx(
  context = context plusIfNN dispatcher plusIfNN fs plusIfNN cwd plusIfNN cli plusIfNN log plusIfNN submit,
  name = coroutineName,
  block = block,
)

@UntrackedTask(because = "The taskAction property can not be serializable.")
abstract class SourceFunTask : SourceTask() {

  @get:OutputDirectory
  protected abstract val outputDirProperty: DirectoryProperty

  @get:Internal
  protected abstract val taskActionProperty: Property<(source: FileTree, output: Directory) -> Unit>

  fun setOutput(path: Path) = outputDirProperty.set(path.toFile())

  @DelicateApi("The fun SourceFunTask.setNiceTaskAction is nicer.")
  fun setTaskAction(action: (srcTree: FileTree, outDir: Directory) -> Unit) {
    taskActionProperty.set(action)
    taskActionProperty.finalizeValue()
  }

  @TaskAction
  fun taskAction() = taskActionProperty.get()(source, outputDirProperty.get())
}

fun SourceTask.addSource(path: Path) {
  source(path.toFile())
}

var SourceFunTask.src: Path
  get() = error("src is write only")
  set(value) = setSource(value.toFile())

var SourceFunTask.out: Path
  get() = error("out is write only")
  set(value) = setOutput(value)


@DelicateApi("Try SourceFunTask.setForEachFileFun (it's suspending style with context, and it's using okio paths).")
fun SourceFunTask.setBlockingVisitFun(visit: FileVisitDetails.(outDir: Directory) -> Unit) =
  setTaskAction { srcTree, outDir -> srcTree.visit { it.visit(outDir) } }


/**
 * Files paths are sorted kinda alphabetically ([okio.Path.compareTo]) before calling the action.
 * You should write any output **only** inside outDirRootPath dir.
 */
@OptIn(DelicateApi::class)
fun SourceFunTask.setNiceTaskAction(action: suspend (srcFilesSorted: List<Path>, outDirRootPath: Path) -> Unit) =
  setTaskAction { srcTree, outDir -> runWithUCtxForTask {
    val srcFilesSorted = srcTree.filter { it.isFile }.map { it.toOkioPath(normalize = true) }.sorted()
    val outRootPath = outDir.asFile.toOkioPath()
    action(srcFilesSorted, outRootPath)
  } }


/**
 * @param process Receiver pair: this.first path is absolute input file path,
 * and this.second is **suggested** absolute output path where to write the result.
 * Files paths are sorted kinda alphabetically ([okio.Path.compareTo]) before calling visit on each of them.
 * Suggested output paths are computed by first checking all input files for common part,
 * then constructing analogous paths under [SourceFunTask.out] with that common prefix stripped away.
 * Even if you don't use suggested path, you still should write any output **only** in [SourceFunTask.out] dir.
 */
fun SourceFunTask.setForEachFileFun(process: suspend Pair<Path, Path>.() -> Unit) =
  setNiceTaskAction { srcFilesSorted, outDirRootPath ->
    srcFilesSorted.isEmpty() && return@setNiceTaskAction
    val inCommonDir = srcFilesSorted.map { it.parent }.commonPart().chkNN { "Input files paths have different roots." }
    srcFilesSorted.forEach { inAbsPath ->
      val inRelPath = inAbsPath.relativeTo(inCommonDir)
      val outAbsPath = outDirRootPath / inRelPath
      (inAbsPath to outAbsPath).process()
    }
  }


// TODO_later: use impl from new kground-io when published
tailrec fun Path?.commonPartWith(that: Path?): Path? = when {
  this == that -> this
  this == null || that == null -> null
  segmentsBytes.size > that.segmentsBytes.size -> parent.commonPartWith(that)
  segmentsBytes.size < that.segmentsBytes.size -> commonPartWith(that.parent)
  else -> parent.commonPartWith(that.parent)
}

// TODO_later: use impl from new kground-io when published
fun List<Path?>.commonPart(): Path? = when {
  isEmpty() -> null
  size == 1 -> this[0]
  else -> reduce { path1, path2 -> path1.commonPartWith(path2) }
}

/** Note: this version writes returned string into out (second) path. */
@Deprecated("Use setTransformFun or setForEachFileFun")
fun SourceFunTask.setTransformFileFun(transform: suspend Pair<Path, Path>.() -> String?) = setForEachFileFun {
  transform()?.let { localUFileSys().writeUtf8(second, it, createParentDir = true) }
}

/** Reads in (first) path, calls transform, and writes returned string (if not null) into out (second) path. */
fun SourceFunTask.setTransformFun(transform: suspend Pair<Path, Path>.(String) -> String?) =
  setForEachFileFun {
    val fs = localUFileSys()
    val input = fs.readUtf8(first)
    val output = transform(input)
    output?.let { fs.writeUtf8(second, it, createParentDir = true) }
  }



@UntrackedTask(because = "The taskAction property can not be serializable. Regex probably is not serializable as well.")
abstract class SourceRegexTask : SourceFunTask() {
  @get:Input abstract val match: Property<Regex>
  @get:Input abstract val replace: Property<String>
  @get:Input abstract val alsoPrintLnStuff: Property<Boolean>
  init {
    alsoPrintLnStuff.convention(false)
    setTransformFun {
      if (alsoPrintLnStuff.get()) {
        println("SRegT <- ${this.first}:1") // logging path with :1 is nice because it's then clickable in the IDE
        println("SRegT -> ${this.second}:1") // logging path with :1 is nice because it's then clickable in the IDE
        println("SRegT <> match:${match.get()} replace:${replace.get()}")
      }
      it.replace(match.get(), replace.get())
    }
  }
}

@UntrackedTask(because = "The taskAction property can not be serializable, Ure and UReplacement are not serializable.")
abstract class SourceUreTask : SourceFunTask() {
  @get:Input abstract val match: Property<Ure>
  @get:Input abstract val replace: Property<UReplacement>
  @get:Input abstract val alsoPrintLnStuff: Property<Boolean>
  init {
    alsoPrintLnStuff.convention(false)
    setTransformFun {
      if (alsoPrintLnStuff.get()) {
        println("SUreT <- ${this.first}:1") // logging path with :1 is nice because it's then clickable in the IDE
        println("SUreT -> ${this.second}:1") // logging path with :1 is nice because it's then clickable in the IDE
        println("SUreT <> match:${match.get().compile()} replace:${replace.get().raw}")
      }
      it.replaceAll(match.get(), replace.get())
    }
  }
}


@Deprecated("Better to just use sourceFun and generate needed details manually using kommandline, etc.")
@UntrackedTask(because = "A lot of state, like git version and build time, is external state and can't be tracked.")
abstract class BuildDetailsTask : DefaultTask() {
  @get:OutputDirectory abstract val outputDir: DirectoryProperty
  @OptIn(DelicateApi::class)
  @TaskAction fun execute() = runWithUCtxForTask {
    val time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    val hash = gitHash().ax().single()
    val tags = kommand("git", "tag", "--points-at").ax().joinToString(separator = "\n")
    // val status = gitStatus().ax().joinToString(separator = "\n")
    outputDir.get().run {
      project.mkdir(this)
      file("build.time").asFile.writeText(time)
      file("build.git.commit.hash").asFile.writeText(hash)
      file("build.git.commit.tags").asFile.writeText(tags)
      // file("git.status").asFile.writeText(status)
    }
  }
}

@Deprecated("Better to just use sourceFun and download files manually using kommandline, etc.")
@UntrackedTask(because = "Downloaded file is external state and can't be tracked.")
abstract class DownloadFileTask : DefaultTask() { // TODO_later: nice task for downloading multiple files.
  @get:Input abstract val inputUrl: Property<String>
  @get:OutputFile abstract val outputFile: RegularFileProperty
  @OptIn(DelicateApi::class)
  @TaskAction fun execute() = runWithUCtxForTask { curlDownload(inputUrl.get(), outputFile.get().asFile.toOkioPath()) }
}


