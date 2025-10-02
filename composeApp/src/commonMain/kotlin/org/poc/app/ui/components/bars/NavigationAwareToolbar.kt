package org.poc.app.ui.components.bars

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.poc.app.navigation.NavigationRoute

@Composable
fun NavigationAwareToolbar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if current screen is the start destination (Portfolio)
    val isStartDestination = currentDestination?.hasRoute<NavigationRoute.Portfolio>() == true

    // Get the title based on current destination
    val title =
        when {
            currentDestination?.hasRoute<NavigationRoute.Portfolio>() == true -> "Portfolio"
            currentDestination?.hasRoute<NavigationRoute.Coins>() == true -> "Discover Coins"
            currentDestination?.hasRoute<NavigationRoute.Buy>() == true -> "Buy Coin"
            currentDestination?.hasRoute<NavigationRoute.Sell>() == true -> "Sell Coin"
            else -> ""
        }

    TransparentToolbar(
        title = title,
        showBackButton = !isStartDestination,
        onBackPressed = {
            if (navController.canGoBack()) {
                navController.navigateUp()
            }
        },
        modifier = modifier,
    )
}

/**
 * Extension function to check if NavController can go back
 */
fun NavHostController.canGoBack(): Boolean = previousBackStackEntry != null
