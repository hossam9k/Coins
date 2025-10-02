package org.poc.app.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.poc.app.ui.DesignSystem
import org.poc.app.ui.components.charts.PerformanceChart

/**
 * Design System Chart Dialog Component
 *
 * A reusable dialog component for displaying performance charts with:
 * - Loading state support
 * - Configurable chart data
 * - Consistent design system styling
 * - Accessible dismiss action
 * - Financial color theming
 */

/**
 * Data class representing the state of chart dialog content
 */
data class ChartDialogState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val title: String = "",
)

/**
 * Main Chart Dialog Component
 */
@Composable
fun ChartDialog(
    state: ChartDialogState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    profitColor: Color = MaterialTheme.colorScheme.primary,
    lossColor: Color = MaterialTheme.colorScheme.error,
    dismissButtonText: StringResource? = null,
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(DesignSystem.Sizes.IconLarge),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                PerformanceChart(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(DesignSystem.Spacing.Medium),
                    nodes = state.sparkLine,
                    profitColor = profitColor,
                    lossColor = lossColor,
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
            ) {
                Text(
                    text =
                        if (dismissButtonText != null) {
                            stringResource(dismissButtonText)
                        } else {
                            "Close"
                        },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
    )
}
