package com.alhadi.cmms.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** One slice of a donut chart. */
internal data class ChartSegment(val label: String, val value: Int, val color: Color)

/**
 * A lightweight donut chart drawn purely with Compose Canvas — no third-party library.
 * Shows the total in the centre; empty data renders a soft neutral ring.
 */
@Composable
internal fun DonutChart(
    segments: List<ChartSegment>,
    modifier: Modifier = Modifier,
    centerValue: String,
    centerLabel: String,
    ringThickness: Float = 30f
) {
    val total = segments.sumOf { it.value }.coerceAtLeast(0)
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    val animated by animateFloatAsState(targetValue = if (total > 0) 1f else 0f, animationSpec = tween(700), label = "donut")
    Box(modifier = modifier.size(132.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(132.dp)) {
            val stroke = Stroke(width = ringThickness, cap = StrokeCap.Round)
            val inset = ringThickness / 2f
            val arcSize = Size(size.width - ringThickness, size.height - ringThickness)
            val topLeft = Offset(inset, inset)
            if (total <= 0) {
                drawArc(emptyColor, 0f, 360f, false, topLeft = topLeft, size = arcSize, style = Stroke(width = ringThickness))
            } else {
                var start = -90f
                segments.filter { it.value > 0 }.forEach { seg ->
                    val sweep = seg.value.toFloat() / total * 360f * animated
                    drawArc(seg.color, start, sweep, false, topLeft = topLeft, size = arcSize, style = stroke)
                    start += seg.value.toFloat() / total * 360f
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerValue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(centerLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** A circular progress gauge (0–100%) drawn with Canvas. */
@Composable
internal fun RingGauge(
    percent: Float,
    color: Color,
    centerLabel: String,
    modifier: Modifier = Modifier,
    ringThickness: Float = 26f
) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    val pct = percent.coerceIn(0f, 100f)
    val animated by animateFloatAsState(targetValue = pct, animationSpec = tween(700), label = "gauge")
    Box(modifier = modifier.size(132.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(132.dp)) {
            val inset = ringThickness / 2f
            val arcSize = Size(size.width - ringThickness, size.height - ringThickness)
            val topLeft = Offset(inset, inset)
            drawArc(track, 0f, 360f, false, topLeft = topLeft, size = arcSize, style = Stroke(width = ringThickness))
            drawArc(color, -90f, animated / 100f * 360f, false, topLeft = topLeft, size = arcSize, style = Stroke(width = ringThickness, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${pct.toInt()}%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(centerLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** A horizontal labelled bar meter. */
@Composable
internal fun BarMeter(
    label: String,
    fraction: Float,
    color: Color,
    valueLabel: String,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(targetValue = fraction.coerceIn(0f, 1f), animationSpec = tween(600), label = "bar")
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valueLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}

/** A wrap-friendly legend showing each donut segment with its colour and value. */
@Composable
internal fun ChartLegend(segments: List<ChartSegment>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        segments.forEach { seg ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(seg.color))
                Text(seg.label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                Text(seg.value.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
        if (segments.isEmpty()) {
            Text("لا توجد بيانات", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
