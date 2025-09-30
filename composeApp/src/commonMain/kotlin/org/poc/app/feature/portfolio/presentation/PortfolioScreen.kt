package org.poc.app.feature.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.poc.app.shared.design.DesignSystem
import org.poc.app.shared.design.components.lists.HoldingsListItem
import org.poc.app.shared.design.components.cards.BalanceCard
import org.poc.app.shared.design.components.cards.BalanceCardData
import org.poc.app.shared.design.components.cards.BalanceCardStyling
import org.poc.app.shared.design.components.states.EmptyState
import org.poc.app.shared.design.components.states.EmptyStateData
import org.poc.app.shared.design.components.states.EmptyStateStyling
import org.poc.app.shared.design.theme.LocalSemanticColors
import org.jetbrains.compose.resources.stringResource
import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.*

@Composable
fun PortfolioScreen(
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val state by portfolioViewModel.state.collectAsStateWithLifecycle()

    // Handle side effects
    LaunchedEffect(portfolioViewModel) {
        portfolioViewModel.sideEffect.collect { effect ->
            when (effect) {
                is PortfolioSideEffect.NavigateToCoinDetails -> {
                    onCoinItemClicked(effect.coinId)
                }
                is PortfolioSideEffect.NavigateToDiscoverCoins -> {
                    onDiscoverCoinsClicked()
                }
                is PortfolioSideEffect.ShowError -> {
                    // Handle error display (could be snackbar, dialog, etc.)
                    // For now, we just log it
                    println("Portfolio Error: ${effect.message}")
                }
                is PortfolioSideEffect.ShowRefreshSuccess -> {
                    // Handle refresh success (could be snackbar)
                    println("Portfolio refreshed successfully")
                }
            }
        }
    }

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = LocalSemanticColors.current.success,
                modifier = Modifier.size(DesignSystem.Sizes.IconLarge)
            )
        }
    } else {
        PortfolioContent(
            state = state,
            onCoinItemClick = { coinId ->
                portfolioViewModel.handleIntent(PortfolioIntent.SelectCoin(coinId))
            },
            onDiscoverCoinsClick = {
                portfolioViewModel.handleIntent(PortfolioIntent.BuyNewCoin)
            }
        )
    }
}

@Composable
fun PortfolioContent(
    state: PortfolioState,
    onCoinItemClick: (String) -> Unit,
    onDiscoverCoinsClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        BalanceCard(
            data = BalanceCardData(
                primaryLabel = Res.string.total_value_label,
                primaryValue = state.portfolioValue,
                secondaryLabel = Res.string.cash_balance_label,
                secondaryValue = state.cashBalance,
                actionButtonText = Res.string.buy_coin_button,
                showActionButton = state.showBuyButton
            ),
            onActionButtonClick = onDiscoverCoinsClick,
            styling = BalanceCardStyling(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                buttonBackgroundColor = LocalSemanticColors.current.success,
                buttonContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        PortfolioCoinsList(
            coins = state.coins,
            onCoinItemClicked = onCoinItemClick,
            onDiscoverCoinsClicked = onDiscoverCoinsClick
        )
    }
}


@Composable
private fun PortfolioCoinsList(
    coins: List<UiPortfolioCoinItem>,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = DesignSystem.Spacing.Large, topEnd = DesignSystem.Spacing.Large))
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (coins.isEmpty()) {
            EmptyState(
                data = EmptyStateData(
                    message = Res.string.no_coins_message,
                    actionButtonText = Res.string.discover_coins_button
                ),
                onActionClick = onDiscoverCoinsClicked,
                styling = EmptyStateStyling(
                    textColor = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.primary,
                    buttonBackgroundColor = LocalSemanticColors.current.success,
                    buttonContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            return@Box
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(Res.string.owned_coins_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(DesignSystem.Spacing.Medium)
                )
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.Small))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.Small)
                ) {
                    items(coins) { coin ->
                        HoldingsListItem(
                            id = coin.id,
                            name = coin.name,
                            amountText = coin.amountInUnitText,
                            valueText = coin.amountInFiatText,
                            performanceText = coin.performancePercentText,
                            iconUrl = coin.iconUrl,
                            isPositive = coin.isPositive,
                            onItemClicked = onCoinItemClicked
                        )
                    }
                }
            }
        }
    }
}


