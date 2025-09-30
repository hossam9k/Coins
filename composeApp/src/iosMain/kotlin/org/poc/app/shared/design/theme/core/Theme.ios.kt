package org.poc.app.shared.design.theme.core

import androidx.compose.runtime.Composable

/**
 * iOS-specific implementation for dynamic color support
 * iOS doesn't have Material You dynamic colors like Android 12+
 * Could be extended to support iOS system accent colors in the future
 */
@Composable
actual fun isDynamicColorSupported(): Boolean {
    // iOS doesn't support Material You dynamic colors
    // This could be extended to support iOS system accent colors
    return false
}