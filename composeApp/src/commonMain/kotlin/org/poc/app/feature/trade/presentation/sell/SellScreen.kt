package org.poc.app.feature.trade.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.poc.app.feature.trade.presentation.common.TradeScreen
import org.poc.app.feature.trade.presentation.common.TradeState
import org.poc.app.feature.trade.presentation.common.TradeType

@Composable
fun SellScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel =
        koinViewModel<SellViewModel>(
            parameters = {
                parametersOf(coinId)
            },
        )
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle side effects
    LaunchedEffect(viewModel.sideEffect) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is SellSideEffect.NavigateToPortfolio -> {
                        navigateToPortfolio()
                    }
                    is SellSideEffect.ShowError -> {
                        // Handle error display (could be snackbar, dialog, etc.)
                        println("Sell Error: ${effect.message}")
                    }
                    is SellSideEffect.ShowSuccess -> {
                        // Handle success message (could be snackbar)
                        println("Sell Success: ${effect.message}")
                    }
                }
            }
        }
    }

    SellScreenContent(
        state = state,
        onAmountChange = { amount -> viewModel.handleIntent(SellIntent.UpdateAmount(amount)) },
        onSubmitClicked = { viewModel.handleIntent(SellIntent.SubmitSale) },
    )
}

@Composable
private fun SellScreenContent(
    state: SellState,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit,
) {
    TradeScreen(
        state =
            TradeState(
                isLoading = state.isLoading || state.isSubmitting,
                error = null, // Handle errors via side effects instead
                availableAmount = state.availableAmount,
                amount = state.amount,
                coin = state.coin,
            ),
        tradeType = TradeType.SELL,
        onAmountChange = onAmountChange,
        onSubmitClicked = onSubmitClicked,
    )
}
