package org.poc.app.shared.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Generic semantic colors for any application domain
 * These colors have business meaning and should not be in the core theme
 * Can be used across different app types: e-commerce, social, productivity, etc.
 */
@Immutable
data class SemanticColors(
    val success: Color = Color.Unspecified,    // Success states, confirmations, positive actions
    val error: Color = Color.Unspecified,      // Errors, failures, negative actions
    val warning: Color = Color.Unspecified,    // Warnings, cautions, attention needed
    val info: Color = Color.Unspecified,       // Information, hints, neutral notifications
    val neutral: Color = Color.Unspecified,    // Neutral states, disabled, inactive
)

// Semantic color definitions
private val SuccessGreenColor = Color(0xFF32de84)
private val ErrorRedColor = Color(0xFFD2122E)
private val WarningOrangeColor = Color(0xFFFF9800)
private val InfoBlueColor = Color(0xFF2196F3)
private val NeutralGrayColor = Color(0xFF9E9E9E)

// Dark theme variants (keeping same colors for consistency)
private val DarkSuccessGreenColor = Color(0xFF32de84)
private val DarkErrorRedColor = Color(0xFFD2122E)
private val DarkWarningOrangeColor = Color(0xFFFF9800)
private val DarkInfoBlueColor = Color(0xFF2196F3)
private val DarkNeutralGrayColor = Color(0xFF9E9E9E)

val LightSemanticColors = SemanticColors(
    success = SuccessGreenColor,
    error = ErrorRedColor,
    warning = WarningOrangeColor,
    info = InfoBlueColor,
    neutral = NeutralGrayColor,
)

val DarkSemanticColors = SemanticColors(
    success = DarkSuccessGreenColor,
    error = DarkErrorRedColor,
    warning = DarkWarningOrangeColor,
    info = DarkInfoBlueColor,
    neutral = DarkNeutralGrayColor,
)

val LocalSemanticColors = compositionLocalOf { SemanticColors() }

/**
 * Extension to get semantic colors from composition
 */
@Composable
fun getSemanticColors(): SemanticColors = LocalSemanticColors.current

