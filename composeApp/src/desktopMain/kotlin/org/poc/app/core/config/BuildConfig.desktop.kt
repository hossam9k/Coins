package org.poc.app.core.config

/**
 * Desktop implementation of BuildConfig
 * For desktop, we use compile-time defaults or environment variables
 */
actual object BuildConfig {
    actual val environment: String = System.getenv("ENVIRONMENT") ?: "development"
    actual val isDebug: Boolean = System.getenv("IS_DEBUG")?.toBoolean() ?: true
    actual val apiBaseUrl: String = System.getenv("API_BASE_URL") ?: "https://api.coincap.io/v2"
    actual val apiKey: String = System.getenv("COINCAP_API_KEY") ?: ""
    actual val enableAnalytics: Boolean = System.getenv("ENABLE_ANALYTICS")?.toBoolean() ?: false
    actual val enableCrashReporting: Boolean = System.getenv("ENABLE_CRASH_REPORTING")?.toBoolean() ?: false
}
