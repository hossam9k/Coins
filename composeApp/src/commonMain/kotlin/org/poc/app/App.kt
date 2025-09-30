package org.poc.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.poc.app.feature.coins.presentation.CoinsListScreen
import org.poc.app.shared.design.components.navigation.Buy
import org.poc.app.shared.design.components.navigation.Coins
import org.poc.app.shared.design.components.navigation.Portfolio
import org.poc.app.shared.design.components.navigation.Sell
import org.poc.app.feature.portfolio.presentation.PortfolioScreen
import org.poc.app.shared.design.theme.core.PocTheme
import org.poc.app.shared.design.theme.AccessibilityProvider
import org.poc.app.shared.design.layouts.AppScaffold
import org.poc.app.feature.trade.presentation.buy.BuyScreen
import org.poc.app.feature.trade.presentation.sell.SellScreen

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()
    // Check if system is in dark theme and test values
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()

    PocTheme(
        darkTheme = isSystemDark,
        dynamicColor = false // Disable dynamic colors to test theme properly
    ) {
        AccessibilityProvider {
            AppScaffold(navController = navController) {
                NavHost(
                    navController = navController,
                    startDestination = Portfolio,
                    modifier = Modifier.fillMaxSize()
                ) {
            composable<Portfolio> {
                PortfolioScreen(
                    onCoinItemClicked = { coinId ->
                        navController.navigate(Sell(coinId))
                    },
                    onDiscoverCoinsClicked = {
                        navController.navigate(Coins)
                    }
                )
            }

            composable<Coins> {
                CoinsListScreen { coinId ->
                    navController.navigate(Buy(coinId))
                }
            }

            composable<Buy> { navBackStackEntry ->
                val coinId: String = navBackStackEntry.toRoute<Buy>().coinId
                BuyScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }
            composable<Sell> { navBackStackEntry ->
                val coinId: String = navBackStackEntry.toRoute<Sell>().coinId
                SellScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }

                }
            }
        }
    }
}