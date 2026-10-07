package com.example.pasoaventuras.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiOverlay(
    trigger: Boolean,
    onFinished: () -> Unit = {}
) {
    if (!trigger) return

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFF59E0B),
            Color(0xFF10B981),
            Color(0xFF4F46E5),
            Color(0xFFEC4899),
            Color(0xFF3B82F6),
            Color(0xFFFFD700)
        )
        List(50) {
            ConfettiParticle(
                initialX = 0.5f,
                initialY = 0.4f,
                vx = (Random.nextFloat() - 0.5f) * 16f,
                vy = (Random.nextFloat() - 0.7f) * 18f,
                size = Random.nextFloat() * 10f + 8f,
                color = colors.random()
            )
        }
    }

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "confettiProgress",
        finishedListener = { onFinished() }
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val t = progress * 1.5f
            val x = (p.initialX * w) + (p.vx * t * 35f)
            val y = (p.initialY * h) + (p.vy * t * 35f) + (0.5f * 9.8f * t * t * 25f)
            val alpha = (1f - progress).coerceIn(0f, 1f)

            if (alpha > 0f) {
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(p.size, p.size)
                )
            }
        }
    }
}
