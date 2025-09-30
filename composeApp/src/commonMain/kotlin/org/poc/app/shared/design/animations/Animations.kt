package org.poc.app.shared.design.animations

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animation theme values for consistent motion design
 * Based on Material Design 3 motion principles
 */
object PocAnimations {

    /**
     * Standard durations for different types of animations
     */
    object Durations {
        const val instant = 0
        const val fast = 150
        const val normal = 300
        const val slow = 500
        const val slower = 800
        const val slowest = 1200

        // Specific use cases
        const val buttonPress = fast
        const val menuOpen = normal
        const val pageTransition = slow
        const val loading = slower
        const val splash = slowest
    }

    /**
     * Easing curves for natural motion
     */
    object Easings {
        val linear = LinearEasing
        val easeIn = EaseIn
        val easeOut = EaseOut
        val easeInOut = EaseInOut

        // Material Design 3 emphasis easings
        val standard = androidx.compose.animation.core.CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
        val emphasized = androidx.compose.animation.core.CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
        val decelerated = androidx.compose.animation.core.CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val accelerated = androidx.compose.animation.core.CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    }

    /**
     * Spring configurations for natural bouncy animations
     */
    object Springs {
        val default = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )

        val gentle = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )

        val bouncy = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )

        val stiff = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    }

    /**
     * Pre-configured animation specs for common use cases
     */
    object Specs {
        // Button interactions
        val buttonPress = tween<Float>(
            durationMillis = Durations.buttonPress,
            easing = Easings.standard
        )

        // UI state changes
        val stateChange = tween<Float>(
            durationMillis = Durations.normal,
            easing = Easings.emphasized
        )

        // Content appearance/disappearance
        val fadeIn = tween<Float>(
            durationMillis = Durations.normal,
            easing = Easings.decelerated
        )

        val fadeOut = tween<Float>(
            durationMillis = Durations.fast,
            easing = Easings.accelerated
        )

        // Page transitions
        val pageEnter = tween<Float>(
            durationMillis = Durations.pageTransition,
            easing = Easings.decelerated
        )

        val pageExit = tween<Float>(
            durationMillis = Durations.normal,
            easing = Easings.accelerated
        )

        // Loading states
        val loadingPulse = tween<Float>(
            durationMillis = Durations.loading,
            easing = Easings.easeInOut
        )

        // Spring animations for interactive elements
        val springyPress = Springs.bouncy
        val gentleSpring = Springs.gentle
    }

    /**
     * Distance-based animation durations
     * Longer distances take more time for natural motion
     */
    fun calculateDuration(distance: Dp): Int {
        return when {
            distance < 100.dp -> Durations.fast
            distance < 300.dp -> Durations.normal
            distance < 500.dp -> Durations.slow
            else -> Durations.slower
        }.coerceAtMost(Durations.slowest)
    }
}

/**
 * Animation configuration that can be modified based on user preferences
 */
data class AnimationConfig(
    val isReducedMotionEnabled: Boolean = false,
    val globalSpeedMultiplier: Float = 1.0f,
    val enableSpringAnimations: Boolean = true
) {
    fun getAdjustedDuration(baseDuration: Int): Int {
        return if (isReducedMotionEnabled) {
            0
        } else {
            (baseDuration * globalSpeedMultiplier).toInt()
        }
    }

    fun <T> getAnimationSpec(baseSpec: AnimationSpec<T>): AnimationSpec<T> {
        return if (isReducedMotionEnabled) {
            tween(durationMillis = 0)
        } else {
            baseSpec
        }
    }
}

/**
 * Composition local for animation configuration
 */
val LocalAnimationConfig = compositionLocalOf { AnimationConfig() }

/**
 * Get animation configuration that respects accessibility settings
 */
@Composable
fun rememberAnimationConfig(
    reducedMotionEnabled: Boolean = false,
    speedMultiplier: Float = 1.0f
): AnimationConfig {
    return AnimationConfig(
        isReducedMotionEnabled = reducedMotionEnabled,
        globalSpeedMultiplier = speedMultiplier,
        enableSpringAnimations = !reducedMotionEnabled
    )
}

/**
 * Animation utilities for common patterns
 */
object AnimationUtils {
    /**
     * Get appropriate animation duration based on content and accessibility
     */
    @Composable
    fun getDuration(baseDuration: Int): Int {
        val config = LocalAnimationConfig.current
        return config.getAdjustedDuration(baseDuration)
    }

    /**
     * Get spring or tween animation based on accessibility preferences
     */
    @Composable
    fun <T> getPreferredSpec(
        springSpec: AnimationSpec<T>,
        tweenSpec: AnimationSpec<T>
    ): AnimationSpec<T> {
        val config = LocalAnimationConfig.current
        return if (config.enableSpringAnimations) {
            config.getAnimationSpec(springSpec)
        } else {
            config.getAnimationSpec(tweenSpec)
        }
    }
}