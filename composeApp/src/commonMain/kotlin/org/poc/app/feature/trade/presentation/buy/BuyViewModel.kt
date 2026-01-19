package org.poc.app.feature.trade.presentation.buy

import kotlinx.coroutines.flow.first
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.util.toUiText
import org.poc.app.core.presentation.base.MviViewModel
import org.poc.app.feature.coins.domain.GetCoinDetailsUseCase
import org.poc.app.feature.portfolio.domain.PortfolioRepository
import org.poc.app.feature.trade.domain.BuyCoinParams
import org.poc.app.feature.trade.domain.BuyCoinUseCase
import org.poc.app.feature.trade.presentation.mapper.TradeUiMapper.toCoin
import org.poc.app.feature.trade.presentation.mapper.TradeUiMapper.toUiTradeCoinItem

class BuyViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val buyCoinUseCase: BuyCoinUseCase,
    private val coinId: String,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
) : MviViewModel<BuyState, BuyIntent, BuySideEffect>(
        initialState = BuyState(isLoading = true),
        dispatcherProvider = dispatcherProvider,
        logger = logger,
        analytics = analytics,
    ) {
    init {
        // Log specific coin opening action
        logUserAction("buy_screen_opened", mapOf("coinId" to coinId))
        // Auto-load coin details when ViewModel is created
        handleIntent(BuyIntent.LoadCoinDetails)
    }

    override suspend fun processIntent(intent: BuyIntent) {
        // MviViewModel automatically logs user actions
        when (intent) {
            is BuyIntent.UpdateAmount -> updateAmount(intent.amount)
            is BuyIntent.SubmitPurchase -> {
                updateState { it.copy(isSubmitting = true, error = null) }
                submitPurchase()
            }
            is BuyIntent.LoadCoinDetails -> {
                updateState { it.copy(isLoading = true, error = null) }
                loadCoinDetails()
            }
            is BuyIntent.ClearError -> clearError()
            is BuyIntent.RetryLoading -> {
                updateState { it.copy(isLoading = true, error = null) }
                retryLoading()
            }
        }
    }

    override suspend fun handleErrorSideEffect(error: Throwable) {
        emitSideEffect(
            BuySideEffect.ShowError(
                message = error.message ?: "An unexpected error occurred",
                details = error.toString(),
            ),
        )
    }

    private suspend fun updateAmount(amount: String) {
        updateState { it.copy(amount = amount) }
    }

    private suspend fun loadCoinDetails() {
        try {
            // Get user's current cash balance
            val balance = portfolioRepository.cashBalanceFlow().first()

            // Get coin details with current price
            when (val coinResponse = getCoinDetailsUseCase(coinId)) {
                is Result.Success -> {
                    updateState { currentState ->
                        currentState.copy(
                            isLoading = false,
                            coin = coinResponse.data.toUiTradeCoinItem(),
                            availableAmount = "Available: $$balance",
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = coinResponse.error.toUiText().toString(),
                            errorDetails = "Failed to load coin details",
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoading = false,
                    error = "Failed to load coin details",
                    errorDetails = e.message,
                )
            }
        }
    }

    private suspend fun submitPurchase() {
        val tradeCoin = currentState.coin
        if (tradeCoin == null) {
            updateState { it.copy(isSubmitting = false) }
            return
        }

        val amount = currentState.amount.toDoubleOrNull()
        if (amount == null) {
            emitSideEffect(BuySideEffect.ShowError("Please enter a valid number"))
            updateState { it.copy(isSubmitting = false) }
            return
        }

        if (amount <= 0) {
            emitSideEffect(BuySideEffect.ShowError("Please enter a valid amount"))
            updateState { it.copy(isSubmitting = false) }
            return
        }

        try {
            val buyCoinResponse =
                buyCoinUseCase(
                    BuyCoinParams(
                        coin = tradeCoin.toCoin(),
                        amountInFiat = PreciseDecimal.fromDouble(amount),
                        price = PreciseDecimal.fromDouble(tradeCoin.price),
                    ),
                )

            when (buyCoinResponse) {
                is Result.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    emitSideEffect(BuySideEffect.ShowSuccess("Purchase completed successfully!"))
                    emitSideEffect(BuySideEffect.NavigateToPortfolio)
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            error = buyCoinResponse.error.toUiText().toString(),
                            errorDetails = "Purchase failed",
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isSubmitting = false,
                    error = "Purchase failed",
                    errorDetails = e.message,
                )
            }
        }
    }

    private suspend fun clearError() {
        updateState { it.copy(error = null, errorDetails = null) }
    }

    private suspend fun retryLoading() {
        loadCoinDetails()
    }

    override fun shouldDeduplicateIntent(intent: BuyIntent): Boolean =
        when (intent) {
            is BuyIntent.LoadCoinDetails,
            is BuyIntent.RetryLoading,
            is BuyIntent.SubmitPurchase,
            -> true
            else -> false
        }
}
