package pl.mareklangiewicz.gradle.ulog

import org.gradle.api.Project
import org.gradle.api.Script
import org.gradle.api.Task
import org.slf4j.LoggerFactory
import pl.mareklangiewicz.udata.str
import pl.mareklangiewicz.ulog.ULog
import pl.mareklangiewicz.ulog.ULogLevel
import pl.mareklangiewicz.ulog.ULogLevel.*

private fun defaultLogLine(level: ULogLevel, data: Any?) = "L ${level.symbol} ${data.str(maxLength = 256)}"

/**
 * Note1: gradle logger is also inheriting from org.slf4j.Logger
 * Note2: alsoPrintLn = true is convenient in gradle case, as gradle will log println calls in special "quiet" mode,
 * so it's always logged/displayed (unless level < minLevel in which case we don't even println)
 */
class SLF4JULog(
  val logger: org.slf4j.Logger,
  var minLevel: ULogLevel = INFO,
  val alsoPrintLn: Boolean = false,
  val toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
): ULog {
  override fun invoke(level: ULogLevel, data: Any?) {
    if (level < minLevel) return
    val line = toLogLine(level, data)
    when (level) {
      NONE -> Unit
      BABBLE, VERBOSE -> logger.trace(line)
      DEBUG -> logger.debug(line)
      INFO -> logger.info(line)
      WARN -> logger.warn(line)
      ERROR, ASSERT -> logger.error(line)
    }
    if (alsoPrintLn) println(line)
  }
}

fun org.slf4j.Logger.asULog(
  minLevel: ULogLevel = INFO,
  alsoPrintLn: Boolean = false,
  toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
) = SLF4JULog(this, minLevel, alsoPrintLn, toLogLine)

fun Project.getULogForProject(
  minLevel: ULogLevel = INFO,
  alsoPrintLn: Boolean = false,
  toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
) = logger.asULog(minLevel, alsoPrintLn, toLogLine)

fun Task.getULogForTask(
  minLevel: ULogLevel = INFO,
  alsoPrintLn: Boolean = false,
  toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
) = logger.asULog(minLevel, alsoPrintLn, toLogLine)

fun Script.getULogForScript(
  minLevel: ULogLevel = INFO,
  alsoPrintLn: Boolean = false,
  toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
) = logger.asULog(minLevel, alsoPrintLn, toLogLine)

fun getSLF4JULog(
  loggerName: String,
  minLevel: ULogLevel = INFO,
  alsoPrintLn: Boolean = false,
  toLogLine: (ULogLevel, Any?) -> String = ::defaultLogLine,
) = LoggerFactory.getLogger(loggerName).asULog(minLevel, alsoPrintLn, toLogLine)
