package org.poc.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

actual object PlatformRuntime {
    actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
    actual fun getMaxMemory(): Long = Runtime.getRuntime().maxMemory()
    actual fun getTotalMemory(): Long = Runtime.getRuntime().totalMemory()
    actual fun getFreeMemory(): Long = Runtime.getRuntime().freeMemory()
    actual fun getUsedMemory(): Long = getTotalMemory() - getFreeMemory()
    actual fun forceGarbageCollection() = Runtime.getRuntime().gc()
}

actual object PlatformSystem {
    actual fun getCurrentPlatform(): Platform = Platform.DESKTOP
}

@Composable
actual fun getCurrentPlatform(): Platform = Platform.DESKTOP
