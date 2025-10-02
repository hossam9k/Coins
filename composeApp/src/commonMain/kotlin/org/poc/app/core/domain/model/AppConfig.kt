package org.poc.app.core.domain.model

/**
 * Application configuration interface for environment-specific settings
 */
interface AppConfig {
    val isDebug: Boolean
    val apiBaseUrl: String
    val apiKey: String
    val databaseName: String
    val enableAnalytics: Boolean
    val enableCrashReporting: Boolean
    val logLevel: LogLevel
}

/**
 * Log levels for controlling logging verbosity
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    NONE,
}

/**
 * Default configuration implementation
 * Note: Use BuildConfig from Gradle for actual values
 */
class DefaultAppConfig(
    override val isDebug: Boolean,
    override val apiBaseUrl: String,
    override val apiKey: String,
    override val databaseName: String = "portfolio_database",
    override val enableAnalytics: Boolean,
    override val enableCrashReporting: Boolean,
    override val logLevel: LogLevel = if (isDebug) LogLevel.DEBUG else LogLevel.ERROR,
) : AppConfig
