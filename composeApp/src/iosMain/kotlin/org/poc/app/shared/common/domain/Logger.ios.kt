package org.poc.app.shared.common.domain

import platform.Foundation.NSLog

/**
 * iOS-specific logger implementation using NSLog
 */
class IOSLogger(
    private val config: AppConfig
) : Logger {

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.DEBUG)) {
            val logMessage = formatMessage("DEBUG", tag, message, throwable)
            NSLog(logMessage)
        }
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.INFO)) {
            val logMessage = formatMessage("INFO", tag, message, throwable)
            NSLog(logMessage)
        }
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.WARN)) {
            val logMessage = formatMessage("WARN", tag, message, throwable)
            NSLog(logMessage)
        }
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.ERROR)) {
            val logMessage = formatMessage("ERROR", tag, message, throwable)
            NSLog(logMessage)
        }
    }

    private fun shouldLog(level: LogLevel): Boolean {
        return level.ordinal >= config.logLevel.ordinal
    }

    private fun formatMessage(level: String, tag: String, message: String, throwable: Throwable?): String {
        return buildString {
            append("[$level] [$tag] $message")
            if (throwable != null) {
                append(" | Error: ${throwable.message}")
                append(" | Stack: ${throwable.stackTraceToString()}")
            }
        }
    }
}

/**
 * iOS-specific analytics logger
 * Note: Add Firebase SDK or other analytics SDK to iOS project to enable full functionality
 */
class IOSAnalyticsLogger(
    private val config: AppConfig
) : AnalyticsLogger {

    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics for iOS
            // Analytics.logEvent(eventName, parameters: parameters.mapValues { (_, value) ->
            //     when (value) {
            //         is String -> value
            //         is Number -> value
            //         is Boolean -> value
            //         else -> value.toString()
            //     }
            // })

            // For now, log to NSLog
            NSLog("Analytics Event: $eventName, Params: $parameters")
        } catch (e: Exception) {
            NSLog("IOSAnalyticsLogger Error: Failed to log event: $eventName - ${e.message}")
        }
    }

    override fun logError(error: Throwable, additionalData: Map<String, Any>) {
        if (!config.enableCrashReporting) return

        try {
            // Firebase Crashlytics for iOS
            // let crashlytics = Crashlytics.crashlytics()
            // additionalData.forEach { key, value in
            //     crashlytics.setCustomValue(value, forKey: key)
            // }
            // crashlytics.record(error: error)

            // For now, log to NSLog
            NSLog("Crash Report: ${error.message}, Data: $additionalData, Stack: ${error.stackTraceToString()}")
        } catch (e: Exception) {
            NSLog("IOSAnalyticsLogger Error: Failed to log error - ${e.message}")
        }
    }

    override fun setUserId(userId: String) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics user ID for iOS
            // Analytics.setUserID(userId)

            // Firebase Crashlytics user ID for iOS
            // Crashlytics.crashlytics().setUserID(userId)

            NSLog("Analytics: User ID set: $userId")
        } catch (e: Exception) {
            NSLog("IOSAnalyticsLogger Error: Failed to set user ID - ${e.message}")
        }
    }

    override fun setUserProperty(key: String, value: String) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics user property for iOS
            // Analytics.setUserProperty(value, forName: key)

            NSLog("Analytics: User property set: $key = $value")
        } catch (e: Exception) {
            NSLog("IOSAnalyticsLogger Error: Failed to set user property - ${e.message}")
        }
    }
}