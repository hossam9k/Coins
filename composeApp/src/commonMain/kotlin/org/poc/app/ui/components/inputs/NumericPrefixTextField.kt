package org.poc.app.ui.components.inputs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.poc.app.ui.DesignSystem

/**
 * Design System Numeric Input Field with Prefix
 *
 * A specialized text field for numeric input with customizable prefix:
 * - Configurable symbol prefix (currency, units, etc.)
 * - Numeric keyboard
 * - Auto-focus capability
 * - Consistent design system styling
 * - Input validation and formatting
 */
@Composable
fun NumericPrefixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    prefixSymbol: String = "$",
    maxValue: Int = 10000,
    autoFocus: Boolean = true,
    enabled: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    val displayText = value.trimStart(prefixSymbol.first())

    BasicTextField(
        value = displayText,
        onValueChange = { newValue ->
            val cleaned = newValue.filter { it.isDigit() || it == '.' }
            val trimmed = cleaned.trimStart('0').takeWhile { it.isDigit() || it == '.' }

            // Validate value
            val numericValue = trimmed.toDoubleOrNull() ?: 0.0
            if (numericValue <= maxValue) {
                onValueChange(trimmed)
            }
        },
        enabled = enabled,
        modifier =
            modifier
                .focusRequester(focusRequester)
                .padding(DesignSystem.Spacing.Medium),
        textStyle =
            TextStyle(
                color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            ),
        keyboardOptions =
            KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
            ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .height(DesignSystem.Sizes.ButtonLarge)
                        .wrapContentWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = prefixSymbol,
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (displayText.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        innerTextField()
                    }
                }
            }
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    )
}

/**
 * Specialized version for centered dollar input
 * Convenient wrapper for currency input
 */
@Composable
fun CurrencyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$",
    maxAmount: Int = 10000,
    autoFocus: Boolean = true,
    enabled: Boolean = true,
) {
    NumericPrefixTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        prefixSymbol = currencySymbol,
        maxValue = maxAmount,
        autoFocus = autoFocus,
        enabled = enabled,
    )
}

/**
 * Specialized version for centered dollar input
 * Maintains backward compatibility
 */
@Composable
fun CenteredDollarTextField(
    amountText: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxAmount: Int = 10000,
    autoFocus: Boolean = true,
    enabled: Boolean = true,
) {
    CurrencyTextField(
        value = amountText,
        onValueChange = onAmountChange,
        modifier = modifier,
        currencySymbol = "$",
        maxAmount = maxAmount,
        autoFocus = autoFocus,
        enabled = enabled,
    )
}
