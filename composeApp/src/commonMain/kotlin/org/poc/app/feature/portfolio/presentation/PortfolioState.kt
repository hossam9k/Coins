package org.poc.app.feature.portfolio.presentation

import org.jetbrains.compose.resources.StringResource
import org.poc.app.core.presentation.base.UiState

data class PortfolioState(
    val portfolioValue: String = "",
    val cashBalance: String = "",
    val showBuyButton: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: StringResource? = null,
    val coins: List<UiPortfolioCoinItem> = emptyList(),
) : UiState