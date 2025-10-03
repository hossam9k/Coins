package org.poc.app.ui.foundation.spacing

import androidx.compose.ui.unit.dp

/**
 * Design System Dimensions
 * Consistent spacing and sizing values for the entire app
 */
object PocDimensions {
    // Base spacing unit (8dp system)
    val baseUnit = 8.dp

    // Standard spacing scale
    val spacing = Spacing

    // Component sizes
    val sizes = Sizes

    // Layout dimensions
    val layout = Layout

    object Spacing {
        val none = 0.dp
        val xs = 4.dp // 0.5 * baseUnit
        val sm = 8.dp // 1 * baseUnit
        val md = 16.dp // 2 * baseUnit
        val lg = 24.dp // 3 * baseUnit
        val xl = 32.dp // 4 * baseUnit
        val xxl = 40.dp // 5 * baseUnit
        val xxxl = 48.dp // 6 * baseUnit
    }

    object Sizes {
        // Icon sizes
        val iconXs = 16.dp
        val iconSm = 20.dp
        val iconMd = 24.dp
        val iconLg = 32.dp
        val iconXl = 48.dp

        // Avatar sizes
        val avatarSm = 32.dp
        val avatarMd = 48.dp
        val avatarLg = 64.dp
        val avatarXl = 96.dp

        // Button heights
        val buttonSmall = 32.dp
        val buttonMedium = 40.dp
        val buttonLarge = 48.dp

        // Input field heights
        val inputSmall = 32.dp
        val inputMedium = 40.dp
        val inputLarge = 48.dp

        // Card/Component sizes
        val cardMinHeight = 64.dp
        val listItemHeight = 56.dp
        val appBarHeight = 64.dp
        val bottomBarHeight = 80.dp

        // Thumbnail/Image sizes
        val thumbnailSm = 48.dp
        val thumbnailMd = 72.dp
        val thumbnailLg = 96.dp
        val thumbnailXl = 128.dp
    }

    object Layout {
        // Screen margins
        val screenHorizontal = 16.dp
        val screenVertical = 16.dp

        // Container widths
        val maxContentWidth = 1200.dp
        val cardMaxWidth = 400.dp
        val dialogMaxWidth = 560.dp

        // Minimum touch targets (accessibility)
        val minTouchTarget = 48.dp

        // Corner radius
        val cornerXs = 4.dp
        val cornerSm = 8.dp
        val cornerMd = 12.dp
        val cornerLg = 16.dp
        val cornerXl = 20.dp
        val cornerRound = 100.dp // Fully rounded

        // Elevation (for shadow/blur effects)
        val elevationNone = 0.dp
        val elevationXs = 1.dp
        val elevationSm = 2.dp
        val elevationMd = 4.dp
        val elevationLg = 8.dp
        val elevationXl = 12.dp
        val elevationXxl = 16.dp

        // Border widths
        val borderThin = 1.dp
        val borderMedium = 2.dp
        val borderThick = 4.dp
    }
}

/**
 * Convenience extensions for common dimension patterns
 */
val Int.dpx get() = (this * PocDimensions.baseUnit.value).dp
val Double.dpx get() = (this * PocDimensions.baseUnit.value).dp
