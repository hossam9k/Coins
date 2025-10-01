package org.poc.app.shared.common.domain

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Desktop-specific logger implementation with file logging support
 */
class DesktopLogger(
    private val config: AppConfig,
    private val logToFile: Boolean = false,
    private val logFilePath: String = "app.log"
) : Logger {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.DEBUG)) {
            log("DEBUG", tag, message, throwable)
        }
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.INFO)) {
            log("INFO", tag, message, throwable)
        }
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.WARN)) {
            log("WARN", tag, message, throwable)
        }
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.ERROR)) {
            log("ERROR", tag, message, throwable)
        }
    }

    private fun shouldLog(level: LogLevel): Boolean {
        return level.ordinal >= config.logLevel.ordinal
    }

    private fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val logMessage = buildString {
            append("$timestamp [$level] [$tag] $message")
            if (throwable != null) {
                append("\n")
                append(getStackTrace(throwable))
            }
        }

        // Console output
        println(logMessage)

        // File output if enabled
        if (logToFile) {
            try {
                File(logFilePath).appendText("$logMessage\n")
            } catch (e: Exception) {
                println("Failed to write to log file: ${e.message}")
            }
        }
    }

    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        return stringWriter.toString()
    }
}

/**
 * Desktop-specific analytics logger (mostly for development/testing)
 */
class DesktopAnalyticsLogger(
    private val config: AppConfig,
    private val logToFile: Boolean = false,
    private val analyticsFilePath: String = "analytics.log"
) : AnalyticsLogger {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        if (!config.enableAnalytics) return

        val timestamp = LocalDateTime.now().format(dateFormatter)
        val logMessage = "$timestamp [EVENT] $eventName: $parameters"

        println(logMessage)

        if (logToFile) {
            try {
                File(analyticsFilePath).appendText("$logMessage\n")
            } catch (e: Exception) {
                println("Failed to write analytics to file: ${e.message}")
            }
        }
    }

    override fun logError(error: Throwable, additionalData: Map<String, Any>) {
        if (!config.enableCrashReporting) return

        val timestamp = LocalDateTime.now().format(dateFormatter)
        val logMessage = buildString {
            append("$timestamp [CRASH] ${error.message}")
            append(" | Data: $additionalData")
            append("\n${error.stackTraceToString()}")
        }

        println(logMessage)

        if (logToFile) {
            try {
                File("crashes.log").appendText("$logMessage\n")
            } catch (e: Exception) {
                println("Failed to write crash to file: ${e.message}")
            }
        }
    }

    override fun setUserId(userId: String) {
        if (!config.enableAnalytics) return

        val timestamp = LocalDateTime.now().format(dateFormatter)
        val logMessage = "$timestamp [USER] ID set: $userId"

        println(logMessage)

        if (logToFile) {
            try {
                File(analyticsFilePath).appendText("$logMessage\n")
            } catch (e: Exception) {
                println("Failed to write user ID to file: ${e.message}")
            }
        }
    }

    override fun setUserProperty(key: String, value: String) {
        if (!config.enableAnalytics) return

        val timestamp = LocalDateTime.now().format(dateFormatter)
        val logMessage = "$timestamp [USER_PROP] $key = $value"

        println(logMessage)

        if (logToFile) {
            try {
                File(analyticsFilePath).appendText("$logMessage\n")
            } catch (e: Exception) {
                println("Failed to write user property to file: ${e.message}")
            }
        }
    }
}