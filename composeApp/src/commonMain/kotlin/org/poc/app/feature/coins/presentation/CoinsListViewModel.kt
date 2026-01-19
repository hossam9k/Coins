package org.poc.app.feature.coins.presentation

import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.util.toChartData
import org.poc.app.core.domain.util.toUiText
import org.poc.app.core.presentation.base.MviViewModel
import org.poc.app.core.presentation.base.UiIntent
import org.poc.app.core.presentation.base.UiSideEffect
import org.poc.app.feature.coins.domain.GetCoinPriceHistoryUseCase
import org.poc.app.feature.coins.domain.GetCoinsListUseCase
import org.poc.app.feature.coins.presentation.mapper.CoinsUiMapper.toUiCoinListItem

/**
 * MVI Intents for Coins feature
 */
sealed interface CoinsIntent : UiIntent {
    data object LoadCoins : CoinsIntent

    data object RefreshCoins : CoinsIntent

    data class ShowCoinChart(
        val coinId: String,
    ) : CoinsIntent

    data object DismissChart : CoinsIntent

    data object RetryOnError : CoinsIntent
}

/**
 * MVI Side Effects for Coins feature
 */
sealed interface CoinsSideEffect : UiSideEffect {
    data class ShowError(
        val message: StringResource,
    ) : CoinsSideEffect

    data class NavigateToCoinDetails(
        val coinId: String,
    ) : CoinsSideEffect

    data object ShowRefreshSuccess : CoinsSideEffect
}

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
) : MviViewModel<CoinsState, CoinsIntent, CoinsSideEffect>(
        initialState = CoinsState(),
        dispatcherProvider = dispatcherProvider,
        logger = logger,
        analytics = analytics,
    ) {
    init {
        // MviViewModel -> BaseViewModel automatically logs screen view
        // Auto-load coins on ViewModel creation
        handleIntent(CoinsIntent.LoadCoins)
    }

    override suspend fun processIntent(intent: CoinsIntent) {
        when (intent) {
            is CoinsIntent.LoadCoins -> loadCoins()
            is CoinsIntent.RefreshCoins -> refreshCoins()
            is CoinsIntent.ShowCoinChart -> showCoinChart(intent.coinId)
            is CoinsIntent.DismissChart -> dismissChart()
            is CoinsIntent.RetryOnError -> retryAfterError()
        }
    }

    private suspend fun loadCoins() {
        updateState { it.copy(isLoading = true, error = null) }

        when (val result = getCoinsListUseCase()) {
            is Result.Success -> {
                val coins =
                    result.data.map { coinItem ->
                        coinItem.toUiCoinListItem()
                    }
                updateState {
                    it.copy(
                        coins = coins,
                        isLoading = false,
                        error = null,
                    )
                }
            }
            is Result.Error -> {
                updateState {
                    it.copy(
                        isLoading = false,
                        error = null, // Error details handled via side effects
                    )
                }
                emitSideEffect(CoinsSideEffect.ShowError(result.error.toUiText()))
            }
        }
    }

    private suspend fun refreshCoins() {
        loadCoins()
        emitSideEffect(CoinsSideEffect.ShowRefreshSuccess)
    }

    private suspend fun showCoinChart(coinId: String) {
        updateState {
            it.copy(
                chartState =
                    UiChartState(
                        sparkLine = emptyList(),
                        isLoading = true,
                    ),
            )
        }

        when (val result = getCoinPriceHistoryUseCase(coinId)) {
            is Result.Success -> {
                val coinName =
                    currentState.coins
                        .find { it.id == coinId }
                        ?.name
                        .orEmpty()
                updateState { currentState ->
                    currentState.copy(
                        chartState =
                            UiChartState(
                                sparkLine =
                                    result.data
                                        .sortedBy { it.timestamp }
                                        .map { it.price }
                                        .toChartData(),
                                isLoading = false,
                                coinName = coinName,
                            ),
                    )
                }
            }
            is Result.Error -> {
                updateState { currentState ->
                    currentState.copy(
                        chartState =
                            UiChartState(
                                sparkLine = emptyList(),
                                isLoading = false,
                                coinName = "",
                            ),
                    )
                }
                emitSideEffect(CoinsSideEffect.ShowError(result.error.toUiText()))
            }
        }
    }

    private suspend fun dismissChart() {
        updateState { it.copy(chartState = null) }
    }

    private suspend fun retryAfterError() {
        loadCoins()
    }

    override suspend fun handleErrorSideEffect(error: Throwable) {
        emitSideEffect(
            CoinsSideEffect.ShowError(
                message = Res.string.error_unknown,
            ),
        )
    }

    // Legacy methods for backward compatibility (will be removed after UI migration)
    fun onCoinLongPressed(coinId: String) {
        handleIntent(CoinsIntent.ShowCoinChart(coinId))
    }

    fun onDismissChart() {
        handleIntent(CoinsIntent.DismissChart)
    }
}
