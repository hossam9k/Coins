package org.poc.app.feature.trade.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.poc.app.ui.DesignSystem
import org.poc.app.ui.components.inputs.CenteredDollarTextField
import org.poc.app.ui.foundation.colors.DangerRed
import org.poc.app.ui.foundation.colors.SuccessGreen

@Composable
fun TradeScreen(
    state: TradeState,
    tradeType: TradeType,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(DesignSystem.Spacing.Medium),
        ) {
            // Coin info badge
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = DesignSystem.Shapes.Large,
                        ).padding(
                            horizontal = DesignSystem.Spacing.Medium,
                            vertical = DesignSystem.Spacing.Small,
                        ),
            ) {
                AsyncImage(
                    model = state.coin?.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .padding(DesignSystem.Spacing.XSmall)
                            .clip(CircleShape)
                            .size(DesignSystem.Sizes.IconMedium),
                )
                Spacer(modifier = Modifier.width(DesignSystem.Spacing.Medium))
                Text(
                    text = state.coin?.name ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier =
                        Modifier
                            .padding(DesignSystem.Spacing.XSmall)
                            .testTag("trade_screen_coin_name"),
                )
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.Large))

            // Trade type label
            Text(
                text =
                    when (tradeType) {
                        TradeType.BUY -> "Buy Amount"
                        TradeType.SELL -> "Sell Amount"
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            // Amount input field
            CenteredDollarTextField(
                amountText = state.amount,
                onAmountChange = onAmountChange,
            )

            // Available amount display
            Text(
                text = state.availableAmount,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(DesignSystem.Spacing.Small),
            )

            // Error message
            if (state.error != null) {
                Text(
                    text = stringResource(state.error),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier =
                        Modifier
                            .padding(DesignSystem.Spacing.Small)
                            .testTag("trade_error"),
                )
            }
        }

        // Action button
        Button(
            onClick = onSubmitClicked,
            enabled = !state.isLoading,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        when (tradeType) {
                            TradeType.BUY -> SuccessGreen
                            TradeType.SELL -> DangerRed
                        },
                ),
            contentPadding = PaddingValues(horizontal = DesignSystem.Spacing.XXLarge),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = DesignSystem.Spacing.Large),
        ) {
            Text(
                text =
                    when (tradeType) {
                        TradeType.BUY -> "Buy Now"
                        TradeType.SELL -> "Sell Now"
                    },
                style = MaterialTheme.typography.bodyLarge,
                color =
                    when (tradeType) {
                        TradeType.BUY -> MaterialTheme.colorScheme.onPrimary
                        TradeType.SELL -> MaterialTheme.colorScheme.onError
                    },
            )
        }

        // Simple loading overlay - doesn't hide content
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

enum class TradeType {
    BUY,
    SELL,
}
