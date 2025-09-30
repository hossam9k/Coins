package org.poc.app.shared.design.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

/**
 * iOS-specific font family implementation
 * Load platform-specific fonts or return default
 */
@Composable
actual fun getPlatformFontFamily(): FontFamily {
    // On iOS, you can use system fonts or load custom fonts
    // For now, return default - replace with custom fonts when needed
    return FontFamily.Default

    // Example with iOS system fonts:
    // You would use platform-specific font loading here
    // This requires iOS-specific font APIs

    // For custom fonts on iOS:
    // 1. Add font files to iOS bundle
    // 2. Register fonts in Info.plist
    // 3. Load them using platform APIs
}