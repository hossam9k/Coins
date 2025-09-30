package org.poc.app.feature.trade.presentation.sell

import kotlinx.coroutines.flow.first
import org.poc.app.feature.coins.domain.GetCoinDetailsUseCase
import org.poc.app.shared.business.domain.PreciseDecimal
import org.poc.app.shared.business.domain.Result
import org.poc.app.shared.business.domain.DispatcherProvider
import org.poc.app.shared.business.domain.Logger
import org.poc.app.shared.business.domain.AnalyticsLogger
import org.poc.app.shared.business.presentation.mvi.MviViewModel
import org.poc.app.shared.business.util.formatFiatPrecise
import org.poc.app.shared.business.util.toUiText
import org.poc.app.feature.portfolio.domain.PortfolioRepository
import org.poc.app.feature.trade.domain.SellCoinUseCase
import org.poc.app.feature.trade.presentation.mapper.TradeUiMapper.toCoin
import org.poc.app.feature.trade.presentation.mapper.TradeUiMapper.toUiTradeCoinItem

class SellViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val sellCoinUseCase: SellCoinUseCase,
    private val coinId: String,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger
) : MviViewModel<SellState, SellIntent, SellSideEffect>(
    initialState = SellState(isLoading = true),
    dispatcherProvider = dispatcherProvider,
    logger = logger,
    analytics = analytics
) {

    companion object {
        private const val TAG = "SellViewModel"
    }

    init {
        // Log specific coin opening action
        logUserAction("sell_screen_opened", mapOf("coinId" to coinId))
        // Auto-load coin details when ViewModel is created
        handleIntent(SellIntent.LoadCoinDetails)
    }

    override suspend fun processIntent(intent: SellIntent) {
        when (intent) {
            is SellIntent.UpdateAmount -> updateAmount(intent.amount)
            is SellIntent.SubmitSale -> {
                updateState { it.copy(isSubmitting = true, error = null) }
                submitSale()
            }
            is SellIntent.LoadCoinDetails -> {
                updateState { it.copy(isLoading = true, error = null) }
                loadCoinDetails()
            }
            is SellIntent.ClearError -> clearError()
            is SellIntent.RetryLoading -> {
                updateState { it.copy(isLoading = true, error = null) }
                retryLoading()
            }
        }
    }


    override suspend fun handleErrorSideEffect(error: Throwable) {
        emitSideEffect(
            SellSideEffect.ShowError(
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
            // Get user's portfolio coin data
            when (val portfolioCoinResponse = portfolioRepository.getPortfolioCoinFlow(coinId).first()) {
                is Result.Success -> {
                    portfolioCoinResponse.data?.ownedAmountInUnit?.let { ownedAmount ->
                        getCoinDetails(ownedAmount.toDouble())
                    }
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = portfolioCoinResponse.error.toUiText().toString(),
                            errorDetails = "Failed to load portfolio data"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoading = false,
                    error = "Failed to load portfolio data",
                    errorDetails = e.message
                )
            }
        }
    }

    private suspend fun getCoinDetails(ownedAmountInUnit: Double) {
        try {
            when (val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
                is Result.Success -> {
                    val availableAmountInFiat = PreciseDecimal.fromDouble(ownedAmountInUnit) * coinResponse.data.price
                    updateState { currentState ->
                        currentState.copy(
                            isLoading = false,
                            coin = coinResponse.data.toUiTradeCoinItem(),
                            availableAmount = "Available: ${formatFiatPrecise(availableAmountInFiat)}",
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

    private suspend fun submitSale() {
        val tradeCoin = currentState.coin ?: return
        val amount = currentState.amount.toDoubleOrNull() ?: return

        if (amount <= 0) {
            emitSideEffect(SellSideEffect.ShowError("Please enter a valid amount"))
            return
        }

        try {
            val sellCoinResponse = sellCoinUseCase.sellCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = PreciseDecimal.fromDouble(amount),
                price = PreciseDecimal.fromDouble(tradeCoin.price)
            )

            when (sellCoinResponse) {
                is Result.Success -> {
                    updateState { it.copy(isSubmitting = false) }
                    emitSideEffect(SellSideEffect.ShowSuccess("Sale completed successfully!"))
                    emitSideEffect(SellSideEffect.NavigateToPortfolio)
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            error = sellCoinResponse.error.toUiText().toString(),
                            errorDetails = "Sale failed"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isSubmitting = false,
                    error = "Sale failed",
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

    override fun shouldDeduplicateIntent(intent: SellIntent): Boolean {
        return when (intent) {
            is SellIntent.LoadCoinDetails,
            is SellIntent.RetryLoading,
            is SellIntent.SubmitSale -> true
            else -> false
        }
    }

}