package org.poc.app.ui.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import org.poc.app.ui.DesignSystem

/**
 * Enterprise-grade Generic List Item Component
 *
 * Unified component for displaying any type of items with metadata
 * Consolidates duplicate implementations and provides consistent behavior
 *
 * Features:
 * - Consistent design across all item listings
 * - Support for different interaction patterns (click, long-click)
 * - Accessibility support
 * - Performance optimized
 * - Flexible data display
 */

/**
 * Data class for generic list item
 * Abstracts different item types with flexible data structure
 */
data class GenericListItemData(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val primaryValue: String, // Price or amount in fiat
    val secondaryValue: String, // Symbol/amount or percentage change
    val changeValue: String? = null, // Performance change (optional)
    val isPositive: Boolean = true, // For color coding performance
    val subtitle: String? = null, // Additional info (optional)
)

/**
 * Interaction configuration for the list item
 */
data class GenericListItemActions(
    val onClick: ((String) -> Unit)? = null,
    val onLongClick: ((String) -> Unit)? = null,
    val enabled: Boolean = true,
)

/**
 * Main Generic List Item Component
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenericListItem(
    data: GenericListItemData,
    actions: GenericListItemActions = GenericListItemActions(),
    modifier: Modifier = Modifier,
    showPerformance: Boolean = true,
) {
    val clickModifier =
        when {
            actions.onLongClick != null && actions.onClick != null -> {
                modifier.combinedClickable(
                    enabled = actions.enabled,
                    onLongClick = { actions.onLongClick.invoke(data.id) },
                    onClick = { actions.onClick.invoke(data.id) },
                )
            }
            actions.onClick != null -> {
                modifier.clickable(enabled = actions.enabled) {
                    actions.onClick.invoke(data.id)
                }
            }
            else -> modifier
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            clickModifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.Medium),
    ) {
        // Icon
        AsyncImage(
            model = data.iconUrl,
            contentDescription = "${data.name} icon",
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .padding(DesignSystem.Spacing.XSmall)
                    .clip(CircleShape)
                    .size(DesignSystem.Sizes.IconLarge),
        )

        Spacer(modifier = Modifier.width(DesignSystem.Spacing.Medium))

        // Name and symbol/subtitle
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = data.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (data.subtitle != null) {
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.XSmall))
                Text(
                    text = data.subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.XSmall))
                Text(
                    text = data.secondaryValue,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.width(DesignSystem.Spacing.Medium))

        // Primary value and change
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = data.primaryValue,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (showPerformance && data.changeValue != null) {
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.XSmall))
                Text(
                    text = data.changeValue,
                    color =
                        if (data.isPositive) {
                            MaterialTheme.colorScheme.primary // Blue for positive
                        } else {
                            MaterialTheme.colorScheme.error // Red for negative
                        },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else if (!showPerformance && data.subtitle == null) {
                // Show secondary value in right column if no subtitle in left
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.XSmall))
                Text(
                    text = data.secondaryValue,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Convenience function for Market Items (Price/Value data)
 */
@Composable
fun MarketListItem(
    id: String,
    name: String,
    symbol: String,
    iconUrl: String,
    formattedPrice: String,
    formattedChange: String,
    isPositive: Boolean,
    onItemClicked: ((String) -> Unit)? = null,
    onItemLongPressed: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    GenericListItem(
        data =
            GenericListItemData(
                id = id,
                name = name,
                symbol = symbol,
                iconUrl = iconUrl,
                primaryValue = formattedPrice,
                secondaryValue = symbol,
                changeValue = formattedChange,
                isPositive = isPositive,
            ),
        actions =
            GenericListItemActions(
                onClick = onItemClicked,
                onLongClick = onItemLongPressed,
            ),
        modifier = modifier,
        showPerformance = true,
    )
}

/**
 * Convenience function for Holdings Items (Owned items data)
 */
@Composable
fun HoldingsListItem(
    id: String,
    name: String,
    amountText: String,
    valueText: String,
    performanceText: String,
    iconUrl: String,
    isPositive: Boolean,
    onItemClicked: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    GenericListItem(
        data =
            GenericListItemData(
                id = id,
                name = name,
                symbol = "", // Not used for portfolio items
                iconUrl = iconUrl,
                primaryValue = valueText,
                secondaryValue = amountText,
                changeValue = performanceText,
                isPositive = isPositive,
                subtitle = amountText,
            ),
        actions =
            GenericListItemActions(
                onClick = onItemClicked,
            ),
        modifier = modifier,
        showPerformance = true,
    )
}
