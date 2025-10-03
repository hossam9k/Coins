package org.poc.app.ui.theme

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object PlatformRuntime {
    actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

    @OptIn(ExperimentalForeignApi::class)
    actual fun getMaxMemory(): Long = 512 * 1024 * 1024L // Estimated for iOS

    @OptIn(ExperimentalForeignApi::class)
    actual fun getTotalMemory(): Long = 256 * 1024 * 1024L // Estimated for iOS

    actual fun getFreeMemory(): Long = getMaxMemory() - getUsedMemory()

    actual fun getUsedMemory(): Long = getTotalMemory() / 2 // Estimated

    actual fun forceGarbageCollection() {
        // iOS handles memory management automatically
    }
}

actual object PlatformSystem {
    actual fun getCurrentPlatform(): Platform = Platform.IOS
}

@Composable
actual fun getCurrentPlatform(): Platform = Platform.IOS
