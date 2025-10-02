package org.poc.app.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.poc.app.ui.DesignSystem
import org.poc.app.ui.components.bars.NavigationAwareToolbar

@Composable
fun AppScaffold(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Add theme background
                .statusBarsPadding(), // Handle system bars
    ) {
        // Transparent Toolbar
        NavigationAwareToolbar(
            navController = navController,
            modifier = Modifier.fillMaxWidth(),
        )

        // Content Area
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = DesignSystem.Spacing.Small), // Small spacing between toolbar and content
        ) {
            content()
        }
    }
}
