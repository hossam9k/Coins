package org.poc.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import org.poc.app.ui.animations.PocAnimations

/**
 * Centralized navigation host for better organization
 * Easy to extend with new screens
 */
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Portfolio,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(PocAnimations.Durations.NORMAL),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(PocAnimations.Durations.NORMAL),
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(PocAnimations.Durations.NORMAL),
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(PocAnimations.Durations.NORMAL),
            )
        },
    ) {
        // Portfolio feature
        composable<NavigationRoute.Portfolio>(
            enterTransition = { fadeIn(animationSpec = tween(PocAnimations.Durations.NORMAL)) },
            exitTransition = { fadeOut(animationSpec = tween(PocAnimations.Durations.NORMAL)) },
        ) {
            PortfolioScreen(
                onCoinItemClicked = { coinId ->
                    navController.navigateTo(NavigationRoute.Sell(coinId))
                },
                onDiscoverCoinsClicked = {
                    navController.navigateTo(NavigationRoute.Coins)
                },
            )
        }

        // Coins list feature
        composable<NavigationRoute.Coins>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                ) + fadeIn(tween(PocAnimations.Durations.FAST))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                ) + fadeOut(tween(PocAnimations.Durations.FAST))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                )
            },
        ) {
            CoinsListScreen { coinId ->
                navController.navigateTo(NavigationRoute.Buy(coinId))
            }
        }

        // Buy feature
        composable<NavigationRoute.Buy>(
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    initialOffsetX = { it },
                ) + fadeIn(tween(PocAnimations.Durations.FAST))
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    targetOffsetX = { -it / 3 },
                ) + fadeOut(tween(PocAnimations.Durations.FAST))
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    initialOffsetX = { -it / 3 },
                ) + fadeIn(tween(PocAnimations.Durations.FAST))
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    targetOffsetX = { it },
                ) + fadeOut(tween(PocAnimations.Durations.FAST))
            },
        ) { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<NavigationRoute.Buy>()
            BuyScreen(
                coinId = route.coinId,
                navigateToPortfolio = {
                    navController.navigateToRoot(NavigationRoute.Portfolio)
                },
            )
        }

        // Sell feature
        composable<NavigationRoute.Sell>(
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    initialOffsetX = { it },
                ) + fadeIn(tween(PocAnimations.Durations.FAST))
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    targetOffsetX = { -it / 3 },
                ) + fadeOut(tween(PocAnimations.Durations.FAST))
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    initialOffsetX = { -it / 3 },
                ) + fadeIn(tween(PocAnimations.Durations.FAST))
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(PocAnimations.Durations.NORMAL),
                    targetOffsetX = { it },
                ) + fadeOut(tween(PocAnimations.Durations.FAST))
            },
        ) { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<NavigationRoute.Sell>()
            SellScreen(
                coinId = route.coinId,
                navigateToPortfolio = {
                    navController.navigateToRoot(NavigationRoute.Portfolio)
                },
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
