package org.poc.app.shared.design.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavigationAwareToolbar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if current screen is the start destination (Portfolio)
    val isStartDestination = currentDestination?.hasRoute<Portfolio>() == true

    // Get the title based on current destination
    val title = when {
        currentDestination?.hasRoute<Portfolio>() == true -> "Portfolio"
        currentDestination?.hasRoute<Coins>() == true -> "Discover Coins"
        currentDestination?.hasRoute<Buy>() == true -> "Buy Coin"
        currentDestination?.hasRoute<Sell>() == true -> "Sell Coin"
        currentDestination?.hasRoute<Biometric>() == true -> "Authentication"
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
        modifier = modifier
    )
}

/**
 * Extension function to check if NavController can go back
 */
fun NavHostController.canGoBack(): Boolean {
    return previousBackStackEntry != null
}