package org.poc.app.shared.common.domain

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
    VERBOSE, DEBUG, INFO, WARN, ERROR, NONE
}

/**
 * Default configuration implementation
 */
class DefaultAppConfig(
    override val isDebug: Boolean = false,
    override val apiBaseUrl: String = "https://api.coinpaprika.com",
    override val apiKey: String = "",
    override val databaseName: String = "portfolio_database",
    override val enableAnalytics: Boolean = true,
    override val enableCrashReporting: Boolean = true,
    override val logLevel: LogLevel = if (isDebug) LogLevel.DEBUG else LogLevel.ERROR
) : AppConfig

/**
 * Development configuration with enhanced logging and debugging
 */
class DevAppConfig : AppConfig {
    override val isDebug: Boolean = true
    override val apiBaseUrl: String = "https://api.coinpaprika.com"
    override val apiKey: String = ""
    override val databaseName: String = "portfolio_database_dev"
    override val enableAnalytics: Boolean = false
    override val enableCrashReporting: Boolean = false
    override val logLevel: LogLevel = LogLevel.DEBUG
}

/**
 * Production configuration with optimized settings
 */
class ProdAppConfig(apiKey: String) : AppConfig {
    override val isDebug: Boolean = false
    override val apiBaseUrl: String = "https://api.coinpaprika.com"
    override val apiKey: String = apiKey
    override val databaseName: String = "portfolio_database"
    override val enableAnalytics: Boolean = true
    override val enableCrashReporting: Boolean = true
    override val logLevel: LogLevel = LogLevel.ERROR
}