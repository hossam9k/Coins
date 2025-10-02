package org.poc.app.ui.components.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.poc.app.ui.DesignSystem

/**
 * Design System Empty State Component
 *
 * A reusable component for displaying empty states across the application
 * with consistent styling and optional action buttons.
 *
 * Features:
 * - Optional icon display
 * - Configurable message text
 * - Optional action button
 * - Consistent design system integration
 * - Flexible styling options
 * - Responsive layout
 */

/**
 * Data class for empty state content
 */
data class EmptyStateData(
    val icon: ImageVector? = null,
    val message: StringResource,
    val actionButtonText: StringResource? = null,
)

/**
 * Data class for empty state styling
 */
data class EmptyStateStyling(
    val textColor: Color = Color.Unspecified,
    val iconColor: Color = Color.Unspecified,
    val buttonBackgroundColor: Color = Color.Unspecified,
    val buttonContentColor: Color = Color.Unspecified,
)

/**
 * Main Empty State Component
 */
@Composable
fun EmptyState(
    data: EmptyStateData,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    styling: EmptyStateStyling = EmptyStateStyling(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.Large),
    ) {
        // Optional icon
        if (data.icon != null) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = if (styling.iconColor != Color.Unspecified) styling.iconColor else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignSystem.Sizes.IconLarge),
            )
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.Medium))
        }

        // Message text
        Text(
            text = stringResource(data.message),
            color = if (styling.textColor != Color.Unspecified) styling.textColor else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )

        // Optional action button
        if (data.actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.Medium))
            Button(
                onClick = onActionClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (styling.buttonBackgroundColor !=
                                Color.Unspecified
                            ) {
                                styling.buttonBackgroundColor
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                    ),
                contentPadding = PaddingValues(horizontal = DesignSystem.Spacing.XXLarge),
            ) {
                Text(
                    text = stringResource(data.actionButtonText),
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (styling.buttonContentColor !=
                            Color.Unspecified
                        ) {
                            styling.buttonContentColor
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        },
                )
            }
        }
    }
}
