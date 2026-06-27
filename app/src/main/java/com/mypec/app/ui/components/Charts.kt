package com.mypec.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mypec.app.ui.theme.AccentGradient

@Composable
fun LineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
) {
    if (points.size < 2) {
        ChartEmptyState("Log body weight to see your trend here.", modifier.fillMaxWidth().height(height))
        return
    }
    val line = MaterialTheme.colorScheme.primary
    val fill = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    Canvas(modifier.fillMaxWidth().height(height)) {
        val maxV = points.max()
        val minV = points.min()
        val range = (maxV - minV).takeIf { it > 0f } ?: 1f
        val stepX = size.width / (points.size - 1)
        val mapY = { v: Float -> size.height - ((v - minV) / range) * (size.height * 0.85f) - size.height * 0.075f }

        val path = Path()
        val fillPath = Path()
        points.forEachIndexed { i, v ->
            val x = i * stepX
            val y = mapY(v)
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()
        drawPath(fillPath, brush = Brush.verticalGradient(listOf(fill, androidx.compose.ui.graphics.Color.Transparent)))
        drawPath(path, color = line, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        points.forEachIndexed { i, v ->
            drawCircle(line, radius = 4.dp.toPx(), center = Offset(i * stepX, mapY(v)))
        }
    }
}

@Composable
fun BarChart(
    values: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
) {
    if (values.isEmpty()) {
        ChartEmptyState("Complete workouts to unlock volume charts.", modifier.fillMaxWidth().height(height))
        return
    }
    val barColors = AccentGradient
    val track = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier.fillMaxWidth().height(height)) {
        val maxV = values.maxOf { it.second }.takeIf { it > 0f } ?: 1f
        val slot = size.width / values.size
        val barWidth = slot * 0.55f
        values.forEachIndexed { i, (_, v) ->
            val cx = i * slot + slot / 2
            val barHeight = (v / maxV) * size.height * 0.9f
            drawRoundRect(
                color = track,
                topLeft = Offset(cx - barWidth / 2, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = CornerRadius(barWidth / 2),
            )
            drawRoundRect(
                brush = Brush.verticalGradient(barColors),
                topLeft = Offset(cx - barWidth / 2, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2),
            )
        }
    }
}
