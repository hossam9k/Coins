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
import org.koin.compose.viewmodel.koinViewModel
import org.poc.app.shared.design.DesignSystem
import org.poc.app.shared.design.components.lists.MarketListItem
import org.poc.app.shared.design.components.dialogs.ChartDialog
import org.poc.app.shared.design.components.dialogs.ChartDialogState
import org.poc.app.shared.design.theme.LocalSemanticColors
import org.jetbrains.compose.resources.stringResource
import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.*

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinsListViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onDismissChart = { coinsListViewModel.handleIntent(CoinsIntent.DismissChart) },
        onCoinLongPressed = { coinId -> coinsListViewModel.handleIntent(CoinsIntent.ShowCoinChart(coinId)) },
        onCoinClicked = onCoinClicked
    )
}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.chartState != null) {
            ChartDialog(
                state = ChartDialogState(
                    sparkLine = state.chartState.sparkLine,
                    isLoading = state.chartState.isLoading,
                    title = "Chart for ${state.chartState.coinName}"
                ),
                onDismiss = onDismissChart,
                profitColor = LocalSemanticColors.current.success,
                lossColor = LocalSemanticColors.current.error
            )
        }
        CoinsList(
            coins = state.coins,
            onCoinLongPressed = onCoinLongPressed,
            onCoinClicked = onCoinClicked
        )
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
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
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
                    modifier = Modifier.padding(DesignSystem.Spacing.Medium)
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
                    onItemLongPressed = onCoinLongPressed
                )
            }
        }
    }
}


