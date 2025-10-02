package org.poc.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.poc.app.navigation.NavigationHost
import org.poc.app.ui.layouts.AppScaffold
import org.poc.app.ui.theme.core.PocTheme

/**
 * Clean, simple app entry point
 * All navigation logic moved to NavigationHost
 */
@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    PocTheme {
        AppScaffold(navController = navController) {
            NavigationHost(navController = navController)
        }
    }
}
