package org.poc.app.core.config

/**
 * Centralized app configuration
 * Easy to change for different environments
 */
data class AppConfiguration(
    val appName: String = "POC App",
    val apiBaseUrl: String = BuildConfig.API_BASE_URL,
    val apiKey: String = BuildConfig.API_KEY,
    val isDebugMode: Boolean = BuildConfig.IS_DEBUG,
    val networkTimeoutMs: Long = 30_000L,
    val cacheExpirationMinutes: Int = 5,
    val maxRetryAttempts: Int = 3,
    val enableAnalytics: Boolean = !BuildConfig.IS_DEBUG,
    val enableCrashReporting: Boolean = !BuildConfig.IS_DEBUG
)

/**
 * Build configuration (would be generated in real app)
 */
object BuildConfig {
    const val IS_DEBUG = true
    const val API_BASE_URL = "https://api.coincap.io/v2"
    const val API_KEY = "" // Add your API key here
}

/**
 * Environment-specific configurations
 */
sealed class Environment(val config: AppConfiguration) {

    object Development : Environment(
        AppConfiguration(
            appName = "POC Dev",
            isDebugMode = true,
            enableAnalytics = false,
            enableCrashReporting = false
        )
    )

    object Staging : Environment(
        AppConfiguration(
            appName = "POC Staging",
            isDebugMode = false,
            enableAnalytics = true,
            enableCrashReporting = true
        )
    )

    object Production : Environment(
        AppConfiguration(
            appName = "POC",
            isDebugMode = false,
            networkTimeoutMs = 15_000L, // Stricter timeout in production
            enableAnalytics = true,
            enableCrashReporting = true
        )
    )
}