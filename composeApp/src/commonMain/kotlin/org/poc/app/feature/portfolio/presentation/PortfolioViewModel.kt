package org.poc.app.feature.portfolio.presentation

import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.error_unknown
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import org.jetbrains.compose.resources.StringResource
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.util.formatPriceDisplay
import org.poc.app.core.domain.util.toUiText
import org.poc.app.core.presentation.base.MviViewModel
import org.poc.app.core.presentation.base.UiIntent
import org.poc.app.core.presentation.base.UiSideEffect
import org.poc.app.feature.portfolio.domain.GetAllPortfolioCoinsUseCase
import org.poc.app.feature.portfolio.domain.GetCashBalanceUseCase
import org.poc.app.feature.portfolio.domain.GetTotalBalanceUseCase
import org.poc.app.feature.portfolio.domain.InitializeBalanceUseCase
import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import org.poc.app.feature.portfolio.presentation.mapper.PortfolioUiMapper.toUiPortfolioCoinItem

/**
 * MVI Intents for Portfolio feature
 */
sealed interface PortfolioIntent : UiIntent {
    data object LoadPortfolio : PortfolioIntent

    data object RefreshPortfolio : PortfolioIntent

    data class SelectCoin(
        val coinId: String,
    ) : PortfolioIntent

    data object BuyNewCoin : PortfolioIntent

    data object RetryOnError : PortfolioIntent
}

/**
 * MVI Side Effects for Portfolio feature
 */
sealed interface PortfolioSideEffect : UiSideEffect {
    data class ShowError(
        val message: StringResource,
    ) : PortfolioSideEffect

    data class NavigateToCoinDetails(
        val coinId: String,
    ) : PortfolioSideEffect

    data object NavigateToDiscoverCoins : PortfolioSideEffect

    data object ShowRefreshSuccess : PortfolioSideEffect
}

class PortfolioViewModel(
    private val getAllPortfolioCoinsUseCase: GetAllPortfolioCoinsUseCase,
    private val getTotalBalanceUseCase: GetTotalBalanceUseCase,
    private val getCashBalanceUseCase: GetCashBalanceUseCase,
    private val initializeBalanceUseCase: InitializeBalanceUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
) : MviViewModel<PortfolioState, PortfolioIntent, PortfolioSideEffect>(
        initialState = PortfolioState(isLoading = true),
        dispatcherProvider = dispatcherProvider,
        logger = logger,
        analytics = analytics,
    ) {
    companion object {
        private const val TAG = "PortfolioViewModel"
    }

    init {
        // MviViewModel -> BaseViewModel automatically logs screen view
        // Auto-load portfolio on ViewModel creation
        handleIntent(PortfolioIntent.LoadPortfolio)
    }

    override suspend fun processIntent(intent: PortfolioIntent) {
        // MviViewModel automatically logs user actions
        when (intent) {
            is PortfolioIntent.LoadPortfolio -> loadPortfolio()
            is PortfolioIntent.RefreshPortfolio -> refreshPortfolio()
            is PortfolioIntent.SelectCoin -> selectCoin(intent.coinId)
            is PortfolioIntent.BuyNewCoin -> buyNewCoin()
            is PortfolioIntent.RetryOnError -> retryOnError()
        }
    }

    private suspend fun loadPortfolio() {
        updateState { it.copy(isLoading = true, error = null) }

        // Use BaseViewModel's safe execution with operation tracking
        launchSafe(
            operationName = "load_portfolio",
            preventDuplicates = true,
        ) {
            initializeBalanceUseCase()

            combine(
                getAllPortfolioCoinsUseCase(),
                getTotalBalanceUseCase(),
                getCashBalanceUseCase(),
            ) { portfolioCoinsResponse, totalBalanceResult, cashBalance ->
                Triple(portfolioCoinsResponse, totalBalanceResult, cashBalance)
            }.flowOn(dispatcherProvider.default)
                .take(1)
                .collect { (portfolioCoinsResponse, totalBalanceResult, cashBalance) ->
                    when (portfolioCoinsResponse) {
                        is Result.Success -> {
                            handleSuccessState(portfolioCoinsResponse.data, totalBalanceResult, cashBalance)
                        }
                        is Result.Error -> {
                            // Error logging handled by launchSafe
                            handleErrorState(portfolioCoinsResponse.error)
                        }
                    }
                }
        }
    }

    private suspend fun refreshPortfolio() {
        updateState { it.copy(isRefreshing = true) }

        try {
            combine(
                getAllPortfolioCoinsUseCase(),
                getTotalBalanceUseCase(),
                getCashBalanceUseCase(),
            ) { portfolioCoinsResponse, totalBalanceResult, cashBalance ->
                Triple(portfolioCoinsResponse, totalBalanceResult, cashBalance)
            }.flowOn(dispatcherProvider.default)
                .take(1)
                .collect { (portfolioCoinsResponse, totalBalanceResult, cashBalance) ->
                    when (portfolioCoinsResponse) {
                        is Result.Success -> {
                            handleSuccessState(portfolioCoinsResponse.data, totalBalanceResult, cashBalance)
                            emitSideEffect(PortfolioSideEffect.ShowRefreshSuccess)
                        }
                        is Result.Error -> {
                            handleErrorState(portfolioCoinsResponse.error)
                        }
                    }
                }
        } catch (e: Exception) {
            updateState { it.copy(isRefreshing = false) }
            emitSideEffect(PortfolioSideEffect.ShowError(Res.string.error_unknown))
        }
    }

    private suspend fun selectCoin(coinId: String) {
        emitSideEffect(PortfolioSideEffect.NavigateToCoinDetails(coinId))
    }

    private suspend fun buyNewCoin() {
        emitSideEffect(PortfolioSideEffect.NavigateToDiscoverCoins)
    }

    private suspend fun retryOnError() {
        loadPortfolio()
    }

    private suspend fun handleSuccessState(
        portfolioCoins: List<PortfolioCoinModel>,
        totalBalanceResult: Result<Double, DataError>,
        cashBalance: Double,
    ) {
        val portfolioValue =
            when (totalBalanceResult) {
                is Result.Success -> formatPriceDisplay(PreciseDecimal.fromDouble(totalBalanceResult.data))
                is Result.Error -> formatPriceDisplay(PreciseDecimal.ZERO)
            }

        updateState {
            it.copy(
                coins = portfolioCoins.map { coin -> coin.toUiPortfolioCoinItem() },
                portfolioValue = portfolioValue,
                cashBalance = formatPriceDisplay(PreciseDecimal.fromDouble(cashBalance)),
                showBuyButton = portfolioCoins.isNotEmpty(),
                isLoading = false,
                isRefreshing = false,
                error = null,
            )
        }
    }

    private suspend fun handleErrorState(error: DataError) {
        updateState {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                error = error.toUiText(),
            )
        }
        emitSideEffect(PortfolioSideEffect.ShowError(error.toUiText()))
    }

    override suspend fun handleErrorSideEffect(error: Throwable) {
        emitSideEffect(PortfolioSideEffect.ShowError(Res.string.error_unknown))
    }
}
