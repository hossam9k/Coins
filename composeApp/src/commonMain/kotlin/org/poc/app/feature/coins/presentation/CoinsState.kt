package org.poc.app.feature.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource
import org.poc.app.core.presentation.base.UiState

@Stable
data class CoinsState(
    val coins: List<UiCoinListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val chartState: UiChartState? = null
) : UiState

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
)