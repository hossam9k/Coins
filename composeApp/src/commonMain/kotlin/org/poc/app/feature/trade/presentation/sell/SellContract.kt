package org.poc.app.feature.trade.presentation.sell

import org.poc.app.core.presentation.base.CommonUiState
import org.poc.app.core.presentation.base.UiIntent
import org.poc.app.core.presentation.base.UiSideEffect
import org.poc.app.feature.trade.presentation.common.UiTradeCoinItem

/**
 * MVI Contract for Sell feature
 * Follows enterprise MVI architecture patterns
 */

/**
 * User intents for the Sell screen
 */
sealed interface SellIntent : UiIntent {
    data class UpdateAmount(val amount: String) : SellIntent
    object SubmitSale : SellIntent
    object LoadCoinDetails : SellIntent
    object ClearError : SellIntent
    object RetryLoading : SellIntent
}

/**
 * UI State for Sell screen
 * Implements CommonUiState for consistent loading/error handling
 */
data class SellState(
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: String? = null,
    override val errorDetails: String? = null,
    val availableAmount: String = "",
    val amount: String = "",
    val coin: UiTradeCoinItem? = null,
    val isSubmitting: Boolean = false
) : CommonUiState

/**
 * One-time side effects for Sell screen
 */
sealed interface SellSideEffect : UiSideEffect {
    object NavigateToPortfolio : SellSideEffect
    data class ShowError(
        val message: String,
        val details: String? = null
    ) : SellSideEffect
    data class ShowSuccess(
        val message: String
    ) : SellSideEffect
}