package org.poc.app.shared.common.domain

import android.util.Log
import org.poc.app.shared.common.domain.AppConfig

/**
 * Android-specific logger implementation using Android Log
 */
class AndroidLogger(
    private val config: AppConfig
) : Logger {

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.DEBUG)) {
            Log.d(tag, message, throwable)
        }
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.INFO)) {
            Log.i(tag, message, throwable)
        }
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.WARN)) {
            Log.w(tag, message, throwable)
        }
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.ERROR)) {
            Log.e(tag, message, throwable)
        }
    }

    private fun shouldLog(level: LogLevel): Boolean {
        return level.ordinal >= config.logLevel.ordinal
    }
}

/**
 * Android-specific analytics logger with Firebase Crashlytics support
 * Note: Add Firebase dependencies to build.gradle to enable Crashlytics
 */
class AndroidAnalyticsLogger(
    private val config: AppConfig
) : AnalyticsLogger {

    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics logging
            // FirebaseAnalytics.getInstance(context).logEvent(eventName, bundleOf(*parameters.toList().toTypedArray()))

            // For now, log to Android Log
            Log.i("Analytics", "Event: $eventName, Params: $parameters")
        } catch (e: Exception) {
            Log.e("AndroidAnalyticsLogger", "Failed to log event: $eventName", e)
        }
    }

    override fun logError(error: Throwable, additionalData: Map<String, Any>) {
        if (!config.enableCrashReporting) return

        try {
            // Firebase Crashlytics error logging
            // FirebaseCrashlytics.getInstance().apply {
            //     additionalData.forEach { (key, value) ->
            //         setCustomKey(key, value.toString())
            //     }
            //     recordException(error)
            // }

            // For now, log to Android Log
            Log.e("CrashReporting", "Error: ${error.message}, Data: $additionalData", error)
        } catch (e: Exception) {
            Log.e("AndroidAnalyticsLogger", "Failed to log error", e)
        }
    }

    override fun setUserId(userId: String) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics user ID
            // FirebaseAnalytics.getInstance(context).setUserId(userId)

            // Firebase Crashlytics user ID
            // FirebaseCrashlytics.getInstance().setUserId(userId)

            Log.i("Analytics", "User ID set: $userId")
        } catch (e: Exception) {
            Log.e("AndroidAnalyticsLogger", "Failed to set user ID", e)
        }
    }

    override fun setUserProperty(key: String, value: String) {
        if (!config.enableAnalytics) return

        try {
            // Firebase Analytics user property
            // FirebaseAnalytics.getInstance(context).setUserProperty(key, value)

            Log.i("Analytics", "User property set: $key = $value")
        } catch (e: Exception) {
            Log.e("AndroidAnalyticsLogger", "Failed to set user property", e)
        }
    }
}