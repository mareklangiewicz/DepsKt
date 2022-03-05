package pl.mareklangiewicz.io

import okio.*
import okio.IOException
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import java.io.*

@Throws(IOException::class)
fun FileSystem.findAllFiles(path: Path, maxDepth: Int = Int.MAX_VALUE): Sequence<Path> {
    require(maxDepth >= 0)
    val md = metadata(path)
    return when {
        md.isRegularFile -> sequenceOf(path)
        maxDepth < 1 || !md.isDirectory -> emptySequence()
        else -> list(path).asSequence().flatMap { findAllFiles(it, maxDepth - 1) }
    }
}

fun Sequence<Path>.filterExt(ext: String) = filter { it.name.endsWith(".$ext") }

/**
 * @param inputRoot path of input root dir
 * @param outputRoot path of output root dir - can be the same as inputRootDir;
 * nothing is written to file system if it's null;
 * @param process file content transformation; if it returns null - output file is not even touched
 */
fun FileSystem.processEachFile(
    inputRoot: Path,
    outputRoot: Path? = null,
    process: (input: Path, output: Path?, content: String) -> String?
) {
    require(inputRoot.isAbsolute)
    require(outputRoot?.isAbsolute ?: true)
    findAllFiles(inputRoot).forEach { inputPath ->
        val outputPath = if (outputRoot == null) null else outputRoot / inputPath.asRelativeTo(inputRoot)
        processFile(inputPath, outputPath) { content -> process(inputPath, outputPath, content) }
    }
}

fun Path.asRelativeTo(path: Path): Path {
    require(this.isAbsolute)
    require(path.isAbsolute)
    return when {
        this == path -> ".".toPath()
        parent == path -> this.name.toPath()
        parent == null -> error("Can not find $path in $this")
        else -> parent!!.asRelativeTo(path) / name
    }
}

fun FileSystem.readUtf8(file: Path): String = read(file) { readUtf8() }

fun FileSystem.writeUtf8(file: Path, content: String, createParentDir: Boolean = false) {
    if (createParentDir) createDirectories(file.parent!!)
    write(file) { writeUtf8(content) }
}

/**
 * @param inputPath path of input file
 * @param outputPath path of output file - can be the same as inputPath;
 * nothing is written to file system if it's null;
 * @param process file content transformation; if it returns null - outputPath is not even touched
 */
fun FileSystem.processFile(inputPath: Path, outputPath: Path? = null, process: (String) -> String?) {

    val input = readUtf8(inputPath)

    val output = process(input)

    if (outputPath == null) {
        if (output != null) println("Warning: ignoring non-null output because outputPath is null")
        return
    }

    if (output == null) {
        println("Ignoring outputPath because output to write is null")
        return
    }

    createDirectories(outputPath.parent!!)
    writeUtf8(outputPath, output)
}

fun FileSystem.withTempDir(tempDirPrefix: String = "uspek", code: FileSystem.(tempDir: Path) -> Unit) {
    lateinit var tempDir: Path
    try {
        // FIXME: do it Okio way (FileSystem.SYSTEM_TEMPORARY_DIRECTORY)
        tempDir = File.createTempFile(tempDirPrefix, null).apply {
            delete() || error("Can not delete temp file: $this")
            mkdir() || error("Can not create dir: $this")
        }.toOkioPath()
        code(tempDir)
    } finally {
        deleteRecursively(tempDir)
    }
}
