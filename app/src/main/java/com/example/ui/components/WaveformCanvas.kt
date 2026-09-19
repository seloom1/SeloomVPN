package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun WaveformCanvas(
    color: Color,
    isActive: Boolean,
    height: Dp = 24.dp,
    amplitude: Float = 10f,
    frequency: Float = 2.5f,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveTransition")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (isActive) (2f * Math.PI.toFloat()) else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val midY = size.height / 2f
        val path = Path()

        val points = 60
        val step = width / points

        for (i in 0..points) {
            val x = i * step
            val normalizedX = (x / width) * frequency * 2f * Math.PI.toFloat()
            val waveHeight = if (isActive) {
                // Envelope that tapers at the ends
                val envelope = sin((x / width) * Math.PI.toFloat())
                sin(normalizedX + phase) * amplitude * envelope
            } else {
                sin(normalizedX) * 2f
            }
            val y = midY + waveHeight

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw background subtle glow
        drawPath(
            path = path,
            color = color.copy(alpha = 0.35f),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw crisp core line
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
