package org.poc.app.feature.trade.presentation.buy

import org.poc.app.shared.business.presentation.mvi.CommonUiState
import org.poc.app.shared.business.presentation.mvi.UiIntent
import org.poc.app.shared.business.presentation.mvi.UiSideEffect
import org.poc.app.feature.trade.presentation.common.UiTradeCoinItem

/**
 * MVI Contract for Buy feature
 * Follows enterprise MVI architecture patterns
 */

/**
 * User intents for the Buy screen
 */
sealed interface BuyIntent : UiIntent {
    data class UpdateAmount(val amount: String) : BuyIntent
    object SubmitPurchase : BuyIntent
    object LoadCoinDetails : BuyIntent
    object ClearError : BuyIntent
    object RetryLoading : BuyIntent
}

/**
 * UI State for Buy screen
 * Implements CommonUiState for consistent loading/error handling
 */
data class BuyState(
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
 * One-time side effects for Buy screen
 */
sealed interface BuySideEffect : UiSideEffect {
    object NavigateToPortfolio : BuySideEffect
    data class ShowError(
        val message: String,
        val details: String? = null
    ) : BuySideEffect
    data class ShowSuccess(
        val message: String
    ) : BuySideEffect
}