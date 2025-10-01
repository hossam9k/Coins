package org.poc.app.feature.coins.data.dto

import kotlinx.serialization.Serializable

// Coinranking API response structure
@Serializable
data class CoinsResponseDto(
    val status: String? = null,
    val data: CoinsDataDto
)

@Serializable
data class CoinsDataDto(
    val stats: StatsDto? = null,
    val coins: List<CoinItemDto>
)

@Serializable
data class StatsDto(
    val total: Int? = null,
    val totalCoins: Int? = null
)

@Serializable
data class CoinItemDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String? = null,
    val price: String,
    val rank: Int,
    val change: String? = null,
)