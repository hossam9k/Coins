package org.poc.app.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes
 * Using sealed interface for better organization
 */
sealed interface NavigationRoute {
    @Serializable
    data object Portfolio : NavigationRoute

    @Serializable
    data object Coins : NavigationRoute

    @Serializable
    data class Buy(
        val coinId: String,
    ) : NavigationRoute

    @Serializable
    data class Sell(
        val coinId: String,
    ) : NavigationRoute

    // Easy to add new routes:
    // @Serializable
    // data object Settings : NavigationRoute

    // @Serializable
    // data class CoinDetails(val coinId: String) : NavigationRoute
}
