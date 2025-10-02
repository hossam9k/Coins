package org.poc.app.core.config

/**
 * Centralized app configuration
 * Easy to change for different environments
 */
data class AppConfiguration(
    val environment: String = BuildConfig.environment,
    val appName: String =
        when (BuildConfig.environment) {
            "development" -> "POC Dev"
            "staging" -> "POC Staging"
            "production" -> "POC"
            else -> "POC App"
        },
    val apiBaseUrl: String = BuildConfig.apiBaseUrl,
    val apiKey: String = BuildConfig.apiKey,
    val isDebugMode: Boolean = BuildConfig.isDebug,
    val networkTimeoutMs: Long = if (BuildConfig.environment == "production") 15_000L else 30_000L,
    val cacheExpirationMinutes: Int = 5,
    val maxRetryAttempts: Int = 3,
    val enableAnalytics: Boolean = BuildConfig.enableAnalytics,
    val enableCrashReporting: Boolean = BuildConfig.enableCrashReporting,
)

/**
 * Build configuration - expect/actual pattern for multiplatform
 * Actual implementation provided by each platform
 */
expect object BuildConfig {
    val environment: String
    val isDebug: Boolean
    val apiBaseUrl: String
    val apiKey: String
    val enableAnalytics: Boolean
    val enableCrashReporting: Boolean
}

/**
 * Environment-specific configurations
 */
sealed class Environment(
    val config: AppConfiguration,
) {
    object Development : Environment(
        AppConfiguration(
            appName = "POC Dev",
            isDebugMode = true,
            enableAnalytics = false,
            enableCrashReporting = false,
        ),
    )

    object Staging : Environment(
        AppConfiguration(
            appName = "POC Staging",
            isDebugMode = false,
            enableAnalytics = true,
            enableCrashReporting = true,
        ),
    )

    object Production : Environment(
        AppConfiguration(
            appName = "POC",
            isDebugMode = false,
            networkTimeoutMs = 15_000L, // Stricter timeout in production
            enableAnalytics = true,
            enableCrashReporting = true,
        ),
    )
}
