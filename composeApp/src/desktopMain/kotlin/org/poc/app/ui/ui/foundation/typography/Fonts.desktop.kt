package org.poc.app.shared.ui.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

/**
 * Desktop-specific font family implementation
 * Load platform-specific fonts or return default
 */
@Composable
actual fun getPlatformFontFamily(): FontFamily {
    // On Desktop, you can use system fonts or load custom fonts
    // For now, return default - replace with custom fonts when needed
    return FontFamily.Default

    // Example with Desktop system fonts:
    // You would use Desktop-specific font loading here
    // This could include system fonts or bundled fonts

    // For custom fonts on Desktop:
    // 1. Add font files to desktopMain/resources/fonts/
    // 2. Load them using Font() from resources
    // 3. Create FontFamily with different weights
}