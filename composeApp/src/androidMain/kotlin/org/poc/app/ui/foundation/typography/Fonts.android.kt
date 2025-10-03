package org.poc.app.ui.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

/**
 * Android-specific font family implementation
 * Load platform-specific fonts or return default
 */
@Composable
actual fun getPlatformFontFamily(): FontFamily {
    // On Android, you can use system fonts or load custom fonts
    // For now, return default - replace with custom fonts when needed
    return FontFamily.Default

    // Example with Android system fonts:
    // You would use Android-specific font loading here
    // This could include Google Fonts or custom fonts from assets

    // For custom fonts on Android:
    // 1. Add font files to androidMain/assets/fonts/
    // 2. Load them using Font() from resources
    // 3. Create FontFamily with different weights
}
