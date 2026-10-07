package com.example.pasoaventuras.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChoyoMascot(
    size: Dp = 110.dp,
    animate: Boolean = true,
    modifier: Modifier = Modifier
) {
    val floatAnim = if (animate) {
        val infiniteTransition = rememberInfiniteTransition(label = "choyoFloat")
        val translateY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -10f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "translateY"
        )
        translateY
    } else 0f

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatAnim.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            fun sx(x: Float) = (x / 100f) * w
            fun sy(y: Float) = (y / 100f) * h

            // 1. Orejas (círculos rosados)
            drawCircle(
                color = Color(0xFFF472B6),
                radius = sx(10f),
                center = Offset(sx(20f), sy(30f))
            )
            drawCircle(
                color = Color(0xFFF472B6),
                radius = sx(10f),
                center = Offset(sx(80f), sy(30f))
            )

            // 2. Cabeza principal amarilla con borde dorado
            drawCircle(
                color = Color(0xFFFDE047),
                radius = sx(46f),
                center = Offset(sx(50f), sy(50f))
            )
            drawCircle(
                color = Color(0xFFCA8A04),
                radius = sx(46f),
                center = Offset(sx(50f), sy(50f)),
                style = Stroke(width = sx(3f))
            )

            // 3. Mechón / cresta superior
            val crestPath = Path().apply {
                moveTo(sx(50f), sy(12f))
                lineTo(sx(55f), sy(22f))
                lineTo(sx(45f), sy(22f))
                close()
            }
            drawPath(path = crestPath, color = Color(0xFFF59E0B))

            // 4. Mejillas sonrosadas
            drawOval(
                color = Color(0xFFFCA5A5),
                topLeft = Offset(sx(23f), sy(51f)),
                size = Size(sx(10f), sy(6f))
            )
            drawOval(
                color = Color(0xFFFCA5A5),
                topLeft = Offset(sx(67f), sy(51f)),
                size = Size(sx(10f), sy(6f))
            )

            // 5. Ojos negros grandes
            drawCircle(
                color = Color(0xFF0F172A),
                radius = sx(6f),
                center = Offset(sx(36f), sy(46f))
            )
            drawCircle(
                color = Color(0xFF0F172A),
                radius = sx(6f),
                center = Offset(sx(64f), sy(46f))
            )

            // 6. Brillos blancos en los ojos
            drawCircle(
                color = Color.White,
                radius = sx(2.5f),
                center = Offset(sx(38f), sy(44f))
            )
            drawCircle(
                color = Color.White,
                radius = sx(2.5f),
                center = Offset(sx(66f), sy(44f))
            )

            // 7. Sonrisa feliz curva
            val smilePath = Path().apply {
                moveTo(sx(42f), sy(58f))
                quadraticTo(sx(50f), sy(64f), sx(58f), sy(58f))
            }
            drawPath(
                path = smilePath,
                color = Color(0xFF0F172A),
                style = Stroke(width = sx(3.5f), cap = StrokeCap.Round)
            )
        }
    }
}
