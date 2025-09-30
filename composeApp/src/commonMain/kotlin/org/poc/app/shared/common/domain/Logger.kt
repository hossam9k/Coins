package org.poc.app.shared.common.domain

/**
 * Cross-platform logging interface for consistent logging across all platforms
 */
interface Logger {
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Default logger implementation using println (can be enhanced with platform-specific logging)
 */
class DefaultLogger : Logger {
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        println("DEBUG [$tag]: $message")
        throwable?.printStackTrace()
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        println("INFO [$tag]: $message")
        throwable?.printStackTrace()
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        println("WARN [$tag]: $message")
        throwable?.printStackTrace()
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        println("ERROR [$tag]: $message")
        throwable?.printStackTrace()
    }
}

/**
 * Analytics and crash reporting interface
 */
interface AnalyticsLogger {
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap())
    fun logError(error: Throwable, additionalData: Map<String, Any> = emptyMap())
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
}

/**
 * No-op implementation for testing or when analytics is disabled
 */
class NoOpAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(eventName: String, parameters: Map<String, Any>) {}
    override fun logError(error: Throwable, additionalData: Map<String, Any>) {}
    override fun setUserId(userId: String) {}
    override fun setUserProperty(key: String, value: String) {}
}