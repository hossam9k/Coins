package org.poc.app.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme
import org.poc.app.ui.DesignSystem

@Composable
fun TransparentToolbar(
    title: String,
    showBackButton: Boolean = true,
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(DesignSystem.Sizes.AppBarHeight)
            .padding(horizontal = DesignSystem.Spacing.Medium),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button or spacer
            if (showBackButton) {
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.size(DesignSystem.Spacing.MinTouchTarget)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(DesignSystem.Sizes.IconMedium)
                    )
                }
            } else {
                Box(modifier = Modifier.size(DesignSystem.Spacing.MinTouchTarget)) // Empty spacer for alignment
            }

            // Title - centered
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Right side spacer for symmetrical layout
            Box(modifier = Modifier.size(DesignSystem.Spacing.MinTouchTarget))
        }
    }
}