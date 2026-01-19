package org.poc.app.feature.trade.presentation.buy

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
fun BuyScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel =
        koinViewModel<BuyViewModel>(
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
                    is BuySideEffect.NavigateToPortfolio -> {
                        navigateToPortfolio()
                    }
                    is BuySideEffect.ShowError -> {
                        // Handle error display (could be snackbar, dialog, etc.)
                        println("Buy Error: ${effect.message}")
                    }
                    is BuySideEffect.ShowSuccess -> {
                        // Handle success message (could be snackbar)
                        println("Buy Success: ${effect.message}")
                    }
                }
            }
        }
    }

    BuyScreenContent(
        state = state,
        onAmountChange = { amount -> viewModel.handleIntent(BuyIntent.UpdateAmount(amount)) },
        onSubmitClicked = { viewModel.handleIntent(BuyIntent.SubmitPurchase) },
    )
}

@Composable
private fun BuyScreenContent(
    state: BuyState,
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
        tradeType = TradeType.BUY,
        onAmountChange = onAmountChange,
        onSubmitClicked = onSubmitClicked,
    )
}
