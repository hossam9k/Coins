package org.poc.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.poc.app.feature.coins.presentation.CoinsListScreen
import org.poc.app.feature.portfolio.presentation.PortfolioScreen
import org.poc.app.feature.trade.presentation.buy.BuyScreen
import org.poc.app.feature.trade.presentation.sell.SellScreen

/**
 * Centralized navigation host for better organization
 * Easy to extend with new screens
 */
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Portfolio,
        modifier = modifier.fillMaxSize()
    ) {
        // Portfolio feature
        composable<NavigationRoute.Portfolio> {
            PortfolioScreen(
                onCoinItemClicked = { coinId ->
                    navController.navigateTo(NavigationRoute.Sell(coinId))
                },
                onDiscoverCoinsClicked = {
                    navController.navigateTo(NavigationRoute.Coins)
                }
            )
        }

        // Coins list feature
        composable<NavigationRoute.Coins> {
            CoinsListScreen { coinId ->
                navController.navigateTo(NavigationRoute.Buy(coinId))
            }
        }

        // Buy feature
        composable<NavigationRoute.Buy> { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<NavigationRoute.Buy>()
            BuyScreen(
                coinId = route.coinId,
                navigateToPortfolio = {
                    navController.navigateToRoot(NavigationRoute.Portfolio)
                }
            )
        }

        // Sell feature
        composable<NavigationRoute.Sell> { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<NavigationRoute.Sell>()
            SellScreen(
                coinId = route.coinId,
                navigateToPortfolio = {
                    navController.navigateToRoot(NavigationRoute.Portfolio)
                }
            )
        }
    }
}

/**
 * Navigation extensions for cleaner usage
 */
fun NavHostController.navigateTo(route: Any) {
    navigate(route)
}

fun NavHostController.navigateToRoot(route: Any) {
    navigate(route) {
        popUpTo(route) { inclusive = true }
    }
}

fun NavHostController.navigateBack() {
    popBackStack()
}