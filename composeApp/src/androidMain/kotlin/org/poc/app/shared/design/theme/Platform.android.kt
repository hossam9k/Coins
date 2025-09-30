package org.poc.app.shared.design.theme

import androidx.compose.runtime.Composable

actual object PlatformRuntime {
    actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
    actual fun getMaxMemory(): Long = Runtime.getRuntime().maxMemory()
    actual fun getTotalMemory(): Long = Runtime.getRuntime().totalMemory()
    actual fun getFreeMemory(): Long = Runtime.getRuntime().freeMemory()
    actual fun getUsedMemory(): Long = getTotalMemory() - getFreeMemory()
    actual fun forceGarbageCollection() = Runtime.getRuntime().gc()
}

actual object PlatformSystem {
    actual fun getCurrentPlatform(): Platform = Platform.ANDROID
}

@Composable
actual fun getCurrentPlatform(): Platform = Platform.ANDROID