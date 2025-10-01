package org.poc.app.core.config

/**
 * iOS implementation of BuildConfig
 * For iOS, we use compile-time defaults or Info.plist values
 * TODO: Configure build variants in Xcode for production builds
 */
actual object BuildConfig {
    actual val environment: String = "development"
    actual val isDebug: Boolean = true
    actual val apiBaseUrl: String = "https://api.coincap.io/v2"
    actual val apiKey: String = ""
    actual val enableAnalytics: Boolean = false
    actual val enableCrashReporting: Boolean = false
}
