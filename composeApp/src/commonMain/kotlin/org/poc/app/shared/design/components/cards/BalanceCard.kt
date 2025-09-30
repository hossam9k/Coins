package org.poc.app.shared.design.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.poc.app.shared.design.DesignSystem

/**
 * Design System Value Display Card Component
 *
 * A reusable card component for displaying any type of value information
 * with optional action button support.
 *
 * Features:
 * - Primary and secondary value display
 * - Optional action button with custom styling
 * - Flexible layout with centered content alignment
 * - Consistent design system integration
 * - Customizable button colors and text
 */

/**
 * Data class for balance card content
 */
data class BalanceCardData(
    val primaryLabel: StringResource? = null,
    val primaryValue: String,
    val secondaryLabel: StringResource? = null,
    val secondaryValue: String? = null,
    val actionButtonText: StringResource? = null,
    val showActionButton: Boolean = false
)

/**
 * Data class for balance card styling
 */
data class BalanceCardStyling(
    val backgroundColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val buttonBackgroundColor: Color = Color.Unspecified,
    val buttonContentColor: Color = Color.Unspecified,
    val heightFraction: Float = 0.3f
)

/**
 * Main Balance Card Component
 */
@Composable
fun BalanceCard(
    data: BalanceCardData,
    onActionButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    styling: BalanceCardStyling = BalanceCardStyling()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight(styling.heightFraction)
            .fillMaxWidth()
            .background(if (styling.backgroundColor != Color.Unspecified) styling.backgroundColor else MaterialTheme.colorScheme.primaryContainer)
            .padding(DesignSystem.Spacing.Large)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Primary label and value
            if (data.primaryLabel != null) {
                Text(
                    text = stringResource(data.primaryLabel),
                    color = if (styling.contentColor != Color.Unspecified) styling.contentColor else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.Small))
            Text(
                text = data.primaryValue,
                color = if (styling.contentColor != Color.Unspecified) styling.contentColor else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
            )

            // Secondary label and value (optional)
            if (data.secondaryLabel != null && data.secondaryValue != null) {
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.Small))
                Row {
                    Text(
                        text = stringResource(data.secondaryLabel),
                        color = if (styling.contentColor != Color.Unspecified) styling.contentColor else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.XSmall))
                    Text(
                        text = data.secondaryValue,
                        color = if (styling.contentColor != Color.Unspecified) styling.contentColor else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Action button (optional)
            if (data.showActionButton && data.actionButtonText != null && onActionButtonClick != null) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onActionButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (styling.buttonBackgroundColor != Color.Unspecified) styling.buttonBackgroundColor else MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = DesignSystem.Spacing.XXLarge),
                ) {
                    Text(
                        text = stringResource(data.actionButtonText),
                        color = if (styling.buttonContentColor != Color.Unspecified) styling.buttonContentColor else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}