package org.poc.app.shared.design.foundation.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.*

/**
 * Brand Font System
 * Define custom font families for consistent brand identity
 */

/**
 * Primary brand font family
 * Using Ubuntu fonts for brand identity
 */
@Composable
internal fun brandFontFamily() = ubuntuFontFamily()

/**
 * Secondary font family for specific use cases
 * Example: monospace for data, numbers, code display
 */
internal val MonospaceFontFamily = FontFamily.Monospace

/**
 * Font weight constants for consistent usage
 */
object PocFontWeights {
    val Light = FontWeight.Light       // 300
    val Regular = FontWeight.Normal    // 400
    val Medium = FontWeight.Medium     // 500
    val SemiBold = FontWeight.SemiBold // 600
    val Bold = FontWeight.Bold         // 700
}

/**
 * Font family resolver based on use case
 * This allows easy switching between different fonts for different purposes
 */
object PocFontFamilies {
    @Composable
    fun primary() = brandFontFamily()

    val secondary = FontFamily.Default
    val monospace = MonospaceFontFamily

    @Composable
    fun display() = brandFontFamily()      // For large display text

    @Composable
    fun body() = brandFontFamily()         // For body text

    val numeric = MonospaceFontFamily // For numbers, prices, amounts, data
}

/**
 * Expect/Actual for platform-specific font loading
 * This will be implemented per platform for custom fonts
 */
@Composable
expect fun getPlatformFontFamily(): FontFamily

/**
 * Ubuntu Font Family Implementation
 * Using the brand's Ubuntu font files
 */
@Composable
fun ubuntuFontFamily() = FontFamily(
    Font(Res.font.Ubuntu_Light, FontWeight.Light),
    Font(Res.font.Ubuntu_Regular, FontWeight.Normal),
    Font(Res.font.Ubuntu_Medium, FontWeight.Medium),
    Font(Res.font.Ubuntu_Bold, FontWeight.Bold),
    Font(Res.font.Ubuntu_Italic, FontWeight.Normal, androidx.compose.ui.text.font.FontStyle.Italic)
)