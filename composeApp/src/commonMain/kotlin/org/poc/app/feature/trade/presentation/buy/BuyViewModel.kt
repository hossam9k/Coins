package org.poc.app.feature.trade.presentation.buy

import kotlinx.coroutines.flow.first
import org.poc.app.feature.coins.domain.GetCoinDetailsUseCase
import org.poc.app.shared.business.domain.PreciseDecimal
import org.poc.app.shared.business.domain.Result
import org.poc.app.shared.business.domain.DispatcherProvider
import org.poc.app.shared.business.domain.Logger
import org.poc.app.shared.business.domain.AnalyticsLogger
import org.poc.app.shared.business.presentation.mvi.MviViewModel
import org.poc.app.shared.business.util.toUiText
import org.poc.app.feature.portfolio.domain.PortfolioRepository
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
    analytics: AnalyticsLogger
) : MviViewModel<BuyState, BuyIntent, BuySideEffect>(
    initialState = BuyState(isLoading = true),
    dispatcherProvider = dispatcherProvider,
    logger = logger,
    analytics = analytics
) {

    companion object {
        private const val TAG = "BuyViewModel"
    }

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
                details = error.toString()
            )
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
            when (val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
                is Result.Success -> {
                    updateState { currentState ->
                        currentState.copy(
                            isLoading = false,
                            coin = coinResponse.data.toUiTradeCoinItem(),
                            availableAmount = "Available: $${balance.toString()}",
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = coinResponse.error.toUiText().toString(),
                            errorDetails = "Failed to load coin details"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoading = false,
                    error = "Failed to load coin details",
                    errorDetails = e.message
                )
            }
        }
    }

    private suspend fun submitPurchase() {
        val tradeCoin = currentState.coin ?: return
        val amount = currentState.amount.toDoubleOrNull() ?: return

        if (amount <= 0) {
            emitSideEffect(BuySideEffect.ShowError("Please enter a valid amount"))
            return
        }

        try {
            val buyCoinResponse = buyCoinUseCase.buyCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = PreciseDecimal.fromDouble(amount),
                price = PreciseDecimal.fromDouble(tradeCoin.price),
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
                            errorDetails = "Purchase failed"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isSubmitting = false,
                    error = "Purchase failed",
                    errorDetails = e.message
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

    override fun shouldDeduplicateIntent(intent: BuyIntent): Boolean {
        return when (intent) {
            is BuyIntent.LoadCoinDetails,
            is BuyIntent.RetryLoading,
            is BuyIntent.SubmitPurchase -> true
            else -> false
        }
    }

}