package org.poc.app.shared.design.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import org.poc.app.shared.business.util.formatFiat

/**
 * Design System Currency Visual Transformation Utility
 *
 * Provides visual formatting for currency input fields by transforming
 * raw numeric input into formatted currency display.
 *
 * Features:
 * - Automatic currency formatting with thousand separators
 * - Maintains cursor position accuracy
 * - Handles empty and invalid input gracefully
 * - Works in both preview and runtime modes
 * - Integrates with design system formatting utilities
 */
private class CurrencyVisualTransformation: VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim()
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        if (originalText.isNumeric().not()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val formattedText = formatFiat(
            amount = originalText.toDouble(),
            showDecimal = false,
        )
        return TransformedText(
            AnnotatedString(formattedText),
            CurrencyOffsetMapping(originalText, formattedText)
        )
    }
}

/**
 * Composable function to create and remember a CurrencyVisualTransformation
 *
 * Automatically handles preview mode by returning VisualTransformation.None
 * to prevent formatting issues in the IDE preview.
 */
@Composable
fun rememberCurrencyVisualTransformation(): VisualTransformation {
    val inspectionMode = LocalInspectionMode.current
    return remember {
        if (inspectionMode) {
            VisualTransformation.None
        } else {
            CurrencyVisualTransformation()
        }
    }
}

/**
 * Extension function to check if a string contains only numeric characters
 */
private fun String.isNumeric(): Boolean {
    return this.all { char -> char.isDigit() }
}