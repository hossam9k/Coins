package org.poc.app.core.config

import org.poc.app.BuildConfig as AndroidBuildConfig

/**
 * Android implementation of BuildConfig
 * Reads from Gradle-generated BuildConfig class based on build variant
 */
actual object BuildConfig {
    actual val environment: String = AndroidBuildConfig.ENVIRONMENT
    actual val isDebug: Boolean = AndroidBuildConfig.IS_DEBUG
    actual val apiBaseUrl: String = AndroidBuildConfig.API_BASE_URL
    actual val apiKey: String = AndroidBuildConfig.COINCAP_API_KEY
    actual val enableAnalytics: Boolean = AndroidBuildConfig.ENABLE_ANALYTICS
    actual val enableCrashReporting: Boolean = AndroidBuildConfig.ENABLE_CRASH_REPORTING
}
