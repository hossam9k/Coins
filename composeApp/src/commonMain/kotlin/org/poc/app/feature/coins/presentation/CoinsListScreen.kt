package org.poc.app.feature.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.top_coins_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.poc.app.ui.DesignSystem
import org.poc.app.ui.components.dialogs.ChartDialog
import org.poc.app.ui.components.dialogs.ChartDialogState
import org.poc.app.ui.components.lists.MarketListItem
import org.poc.app.ui.components.states.EmptyState
import org.poc.app.ui.components.states.ErrorState
import org.poc.app.ui.components.states.LoadingState

@Composable
fun CoinsListScreen(onCoinClicked: (String) -> Unit) {
    val coinsListViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onDismissChart = { coinsListViewModel.handleIntent(CoinsIntent.DismissChart) },
        onCoinLongPressed = { coinId -> coinsListViewModel.handleIntent(CoinsIntent.ShowCoinChart(coinId)) },
        onCoinClicked = onCoinClicked,
        onRetry = { coinsListViewModel.handleIntent(CoinsIntent.LoadCoins) },
    )
}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
    onRetry: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            state.isLoading -> {
                LoadingState(message = "Loading coins...")
            }
            state.error != null -> {
                ErrorState(
                    error = stringResource(state.error),
                    onRetry = onRetry,
                )
            }
            state.coins.isEmpty() -> {
                EmptyState(
                    message = "No coins available",
                    actionLabel = "Refresh",
                    onAction = onRetry,
                )
            }
            else -> {
                if (state.chartState != null) {
                    ChartDialog(
                        state =
                            ChartDialogState(
                                sparkLine = state.chartState.sparkLine,
                                isLoading = state.chartState.isLoading,
                                title = "Chart for ${state.chartState.coinName}",
                            ),
                        onDismiss = onDismissChart,
                        profitColor = MaterialTheme.colorScheme.primary, // Your brand blue
                        lossColor = MaterialTheme.colorScheme.error, // Material's red
                    )
                }
                CoinsList(
                    coins = state.coins,
                    onCoinLongPressed = onCoinLongPressed,
                    onCoinClicked = onCoinClicked,
                )
            }
        }
    }
}

@Composable
fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.None),
            contentPadding = PaddingValues(DesignSystem.Spacing.Medium),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.top_coins_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(DesignSystem.Spacing.Medium),
                )
            }
            items(coins) { coin ->
                MarketListItem(
                    id = coin.id,
                    name = coin.name,
                    symbol = coin.symbol,
                    iconUrl = coin.iconUrl,
                    formattedPrice = coin.formattedPrice,
                    formattedChange = coin.formattedChange,
                    isPositive = coin.isPositive,
                    onItemClicked = onCoinClicked,
                    onItemLongPressed = onCoinLongPressed,
                )
            }
        }
    }
}
