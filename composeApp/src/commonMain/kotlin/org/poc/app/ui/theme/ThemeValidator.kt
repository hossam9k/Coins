package org.poc.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * WCAG-compliant theme validation system
 * Ensures accessibility standards and design consistency
 */
object ThemeValidator {

    /**
     * Validate complete color scheme for WCAG compliance
     */
    fun validateColorScheme(colorScheme: ColorScheme): ColorSchemeValidationResult {
        val results = mutableListOf<ValidationIssue>()

        // Primary color contrast validation
        validateContrast(colorScheme.primary, colorScheme.onPrimary, "Primary")?.let {
            results.add(it)
        }

        // Secondary color contrast validation
        validateContrast(colorScheme.secondary, colorScheme.onSecondary, "Secondary")?.let {
            results.add(it)
        }

        // Surface contrast validation
        validateContrast(colorScheme.surface, colorScheme.onSurface, "Surface")?.let {
            results.add(it)
        }

        // Background contrast validation
        validateContrast(colorScheme.background, colorScheme.onBackground, "Background")?.let {
            results.add(it)
        }

        // Error color contrast validation
        validateContrast(colorScheme.error, colorScheme.onError, "Error")?.let {
            results.add(it)
        }

        return ColorSchemeValidationResult(
            isValid = results.isEmpty(),
            issues = results,
            wcagLevel = determineWcagLevel(results)
        )
    }

    /**
     * Validate individual color contrast ratio
     */
    fun validateColorContrast(foreground: Color, background: Color): ContrastResult {
        val ratio = calculateContrastRatio(foreground, background)

        return ContrastResult(
            ratio = ratio,
            wcagAA = ratio >= 4.5f,
            wcagAAA = ratio >= 7.0f,
            wcagAALarge = ratio >= 3.0f,
            recommendation = getContrastRecommendation(ratio)
        )
    }

    /**
     * Validate touch target sizes for accessibility
     */
    fun validateTouchTargets(components: List<ComponentSpec>): TouchTargetValidationResult {
        val issues = mutableListOf<TouchTargetIssue>()

        components.forEach { component ->
            when {
                component.size < WCAG_MIN_TOUCH_TARGET -> {
                    issues.add(
                        TouchTargetIssue(
                            componentName = component.name,
                            currentSize = component.size,
                            minimumSize = WCAG_MIN_TOUCH_TARGET,
                            severity = ValidationSeverity.ERROR
                        )
                    )
                }
                component.size < WCAG_RECOMMENDED_TOUCH_TARGET -> {
                    issues.add(
                        TouchTargetIssue(
                            componentName = component.name,
                            currentSize = component.size,
                            minimumSize = WCAG_RECOMMENDED_TOUCH_TARGET,
                            severity = ValidationSeverity.WARNING
                        )
                    )
                }
            }
        }

        return TouchTargetValidationResult(
            isValid = issues.none { it.severity == ValidationSeverity.ERROR },
            issues = issues
        )
    }

    /**
     * Generate comprehensive accessibility report
     */
    fun generateAccessibilityReport(
        colorScheme: ColorScheme,
        components: List<ComponentSpec>,
        fonts: List<FontSpec>
    ): AccessibilityReport {
        val colorValidation = validateColorScheme(colorScheme)
        val touchTargetValidation = validateTouchTargets(components)
        val fontValidation = validateFonts(fonts)

        return AccessibilityReport(
            colorSchemeValidation = colorValidation,
            touchTargetValidation = touchTargetValidation,
            fontValidation = fontValidation,
            overallScore = calculateOverallScore(colorValidation, touchTargetValidation, fontValidation),
            recommendations = generateRecommendations(colorValidation, touchTargetValidation, fontValidation)
        )
    }

    /**
     * Validate typography for readability
     */
    fun validateFonts(fonts: List<FontSpec>): FontValidationResult {
        val issues = mutableListOf<FontIssue>()

        fonts.forEach { font ->
            // Check minimum font size
            if (font.size.value < MIN_FONT_SIZE_SP) {
                issues.add(
                    FontIssue(
                        fontName = font.name,
                        issue = "Font size ${font.size} is below minimum readable size",
                        severity = ValidationSeverity.WARNING
                    )
                )
            }

            // Check line height ratio
            val lineHeightRatio = font.lineHeight.value / font.size.value
            if (lineHeightRatio < MIN_LINE_HEIGHT_RATIO) {
                issues.add(
                    FontIssue(
                        fontName = font.name,
                        issue = "Line height ratio $lineHeightRatio is below recommended minimum",
                        severity = ValidationSeverity.WARNING
                    )
                )
            }
        }

        return FontValidationResult(
            isValid = issues.none { it.severity == ValidationSeverity.ERROR },
            issues = issues
        )
    }

    // Private helper functions
    private fun calculateContrastRatio(color1: Color, color2: Color): Float {
        val luminance1 = color1.luminance()
        val luminance2 = color2.luminance()

        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)

        return (lighter + 0.05f) / (darker + 0.05f)
    }

    private fun validateContrast(background: Color, foreground: Color, contextName: String): ValidationIssue? {
        val contrast = validateColorContrast(foreground, background)

        return if (!contrast.wcagAA) {
            ValidationIssue(
                type = ValidationIssueType.COLOR_CONTRAST,
                message = "$contextName contrast ratio ${contrast.ratio} fails WCAG AA (4.5:1)",
                severity = ValidationSeverity.ERROR,
                recommendation = contrast.recommendation
            )
        } else null
    }

    private fun getContrastRecommendation(ratio: Float): String {
        return when {
            ratio >= 7.0f -> "Excellent contrast (WCAG AAA)"
            ratio >= 4.5f -> "Good contrast (WCAG AA)"
            ratio >= 3.0f -> "Acceptable for large text only (WCAG AA Large)"
            else -> "Poor contrast - increase color difference"
        }
    }

    private fun determineWcagLevel(issues: List<ValidationIssue>): WcagLevel {
        return when {
            issues.any { it.severity == ValidationSeverity.ERROR } -> WcagLevel.FAIL
            issues.any { it.severity == ValidationSeverity.WARNING } -> WcagLevel.AA
            else -> WcagLevel.AAA
        }
    }

    private fun calculateOverallScore(
        colorValidation: ColorSchemeValidationResult,
        touchTargetValidation: TouchTargetValidationResult,
        fontValidation: FontValidationResult
    ): Int {
        var score = 100

        // Deduct points for issues
        colorValidation.issues.forEach { issue ->
            score -= when (issue.severity) {
                ValidationSeverity.ERROR -> 20
                ValidationSeverity.WARNING -> 10
                ValidationSeverity.INFO -> 5
            }
        }

        touchTargetValidation.issues.forEach { issue ->
            score -= when (issue.severity) {
                ValidationSeverity.ERROR -> 15
                ValidationSeverity.WARNING -> 8
                ValidationSeverity.INFO -> 3
            }
        }

        fontValidation.issues.forEach { issue ->
            score -= when (issue.severity) {
                ValidationSeverity.ERROR -> 10
                ValidationSeverity.WARNING -> 5
                ValidationSeverity.INFO -> 2
            }
        }

        return maxOf(0, score)
    }

    private fun generateRecommendations(
        colorValidation: ColorSchemeValidationResult,
        touchTargetValidation: TouchTargetValidationResult,
        fontValidation: FontValidationResult
    ): List<String> {
        val recommendations = mutableListOf<String>()

        if (!colorValidation.isValid) {
            recommendations.add("Improve color contrast ratios to meet WCAG AA standards")
        }

        if (!touchTargetValidation.isValid) {
            recommendations.add("Increase touch target sizes to minimum 48dp")
        }

        if (!fontValidation.isValid) {
            recommendations.add("Review typography for minimum font sizes and line heights")
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Theme meets accessibility standards - consider WCAG AAA for maximum accessibility")
        }

        return recommendations
    }

    // Constants
    private val WCAG_MIN_TOUCH_TARGET = 44.dp
    private val WCAG_RECOMMENDED_TOUCH_TARGET = 48.dp
    private const val MIN_FONT_SIZE_SP = 12f
    private const val MIN_LINE_HEIGHT_RATIO = 1.2f
}

// Data classes for validation results
data class ContrastResult(
    val ratio: Float,
    val wcagAA: Boolean,
    val wcagAAA: Boolean,
    val wcagAALarge: Boolean,
    val recommendation: String
)

data class ColorSchemeValidationResult(
    val isValid: Boolean,
    val issues: List<ValidationIssue>,
    val wcagLevel: WcagLevel
)

data class TouchTargetValidationResult(
    val isValid: Boolean,
    val issues: List<TouchTargetIssue>
)

data class FontValidationResult(
    val isValid: Boolean,
    val issues: List<FontIssue>
)

data class AccessibilityReport(
    val colorSchemeValidation: ColorSchemeValidationResult,
    val touchTargetValidation: TouchTargetValidationResult,
    val fontValidation: FontValidationResult,
    val overallScore: Int, // 0-100
    val recommendations: List<String>
)

data class ValidationIssue(
    val type: ValidationIssueType,
    val message: String,
    val severity: ValidationSeverity,
    val recommendation: String
)

data class TouchTargetIssue(
    val componentName: String,
    val currentSize: Dp,
    val minimumSize: Dp,
    val severity: ValidationSeverity
)

data class FontIssue(
    val fontName: String,
    val issue: String,
    val severity: ValidationSeverity
)

data class ComponentSpec(
    val name: String,
    val size: Dp
)

data class FontSpec(
    val name: String,
    val size: androidx.compose.ui.unit.TextUnit,
    val lineHeight: androidx.compose.ui.unit.TextUnit
)

enum class ValidationIssueType {
    COLOR_CONTRAST,
    TOUCH_TARGET_SIZE,
    FONT_SIZE,
    LINE_HEIGHT
}

enum class ValidationSeverity {
    ERROR,   // Must fix
    WARNING, // Should fix
    INFO     // Nice to fix
}

enum class WcagLevel {
    FAIL,   // Does not meet WCAG standards
    AA,     // Meets WCAG AA
    AAA     // Meets WCAG AAA
}