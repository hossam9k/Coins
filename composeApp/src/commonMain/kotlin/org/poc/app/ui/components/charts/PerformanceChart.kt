package org.poc.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Design System Performance Chart Component
 *
 * A reusable chart component for displaying financial performance data
 * with automatic profit/loss color coding and smooth line rendering.
 *
 * Features:
 * - Automatic scaling to fit data range
 * - Profit/loss color indication based on trend direction
 * - Smooth path rendering with stroke styling
 * - Responsive to container size
 * - Null-safe data handling
 */
@Composable
fun PerformanceChart(
    modifier: Modifier = Modifier,
    nodes: List<Double>,
    profitColor: Color,
    lossColor: Color,
) {
    if (nodes.isEmpty()) return

    val max = nodes.maxOrNull() ?: return
    val min = nodes.minOrNull() ?: return
    val lineColor = if (nodes.last() > nodes.first()) profitColor else lossColor

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val path = Path()
        nodes.forEachIndexed { index, value ->
            val x = index * (size.width / (nodes.size - 1))
            val y = size.height * (1 - ((value - min) / (max - min)).toFloat())

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}