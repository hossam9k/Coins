package org.poc.app.ui.theme

/**
 * Platform identification enum
 */
enum class Platform {
    ANDROID,
    IOS,
    DESKTOP
}

/**
 * Platform-specific API declarations
 * Provides access to system resources and capabilities
 */
expect object PlatformRuntime {
    fun getCurrentTimeMillis(): Long
    fun getMaxMemory(): Long
    fun getTotalMemory(): Long
    fun getFreeMemory(): Long
    fun getUsedMemory(): Long
    fun forceGarbageCollection()
}

expect object PlatformSystem {
    fun getCurrentPlatform(): Platform
}

@androidx.compose.runtime.Composable
expect fun getCurrentPlatform(): Platform

