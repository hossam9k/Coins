package org.poc.app.ui

import androidx.compose.ui.unit.Dp
import org.poc.app.ui.foundation.shapes.PocShapes
import org.poc.app.ui.foundation.spacing.PocDimensions

/**
 * POC Design System
 *
 * Central access point for all design system components and tokens.
 * This provides a clean API for consuming design system elements.
 *
 * Usage Examples:
 * ```kotlin
 * import org.poc.app.shared.ui.DesignSystem
 *
 * // Use colors in your composables
 * Card(
 *     colors = CardDefaults.cardColors(
 *         containerColor = DesignSystem.Colors.Primary
 *     )
 * )
 *
 * // Use typography
 * Text(
 *     text = "Welcome",
 *     style = DesignSystem.Typography.HeadlineLarge
 * )
 *
 * // Use spacing
 * Column(
 *     modifier = Modifier.padding(DesignSystem.Spacing.Medium)
 * )
 * ```
 */
object DesignSystem {
    /**
     * Spacing system - consistent spacing values
     */
    object Spacing {
        val None: Dp = PocDimensions.spacing.none
        val XSmall: Dp = PocDimensions.spacing.xs
        val Small: Dp = PocDimensions.spacing.sm
        val Medium: Dp = PocDimensions.spacing.md
        val Large: Dp = PocDimensions.spacing.lg
        val XLarge: Dp = PocDimensions.spacing.xl
        val XXLarge: Dp = PocDimensions.spacing.xxl
        val XXXLarge: Dp = PocDimensions.spacing.xxxl

        // Layout-specific spacing
        val ScreenHorizontal = PocDimensions.layout.screenHorizontal
        val ScreenVertical = PocDimensions.layout.screenVertical
        val MinTouchTarget = PocDimensions.layout.minTouchTarget
    }

    /**
     * Size system - component and element sizes
     */
    object Sizes {
        // Icons
        val IconXSmall = PocDimensions.sizes.iconXs
        val IconSmall = PocDimensions.sizes.iconSm
        val IconMedium = PocDimensions.sizes.iconMd
        val IconLarge = PocDimensions.sizes.iconLg
        val IconXLarge = PocDimensions.sizes.iconXl

        // Buttons
        val ButtonSmall = PocDimensions.sizes.buttonSmall
        val ButtonMedium = PocDimensions.sizes.buttonMedium
        val ButtonLarge = PocDimensions.sizes.buttonLarge

        // Avatars
        val AvatarSmall = PocDimensions.sizes.avatarSm
        val AvatarMedium = PocDimensions.sizes.avatarMd
        val AvatarLarge = PocDimensions.sizes.avatarLg
        val AvatarXLarge = PocDimensions.sizes.avatarXl

        // Layout components
        val AppBarHeight = PocDimensions.sizes.appBarHeight
        val ListItemHeight = PocDimensions.sizes.listItemHeight
    }

    /**
     * Shape system - corner radius and component shapes
     */
    object Shapes {
        private val shapes = PocShapes

        val Small = shapes.small
        val Medium = shapes.medium
        val Large = shapes.large
        val ExtraLarge = shapes.extraLarge
    }

    /**
     * Animation system - consistent motion design
     */
    object Animations {
        // You can add animation constants here
        const val FAST_DURATION = 150
        const val MEDIUM_DURATION = 300
        const val SLOW_DURATION = 500

        // Add easing curves, spring configs, etc.
    }
}
