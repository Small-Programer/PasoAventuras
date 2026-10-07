package com.example.pasoaventuras.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.ui.theme.Amber800

@Composable
fun ChoyoCoinIcon(
    size: Dp = 18.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            val c = Offset(this.size.width / 2f, this.size.height / 2f)

            // Borde exterior ámbar oscuro
            drawCircle(color = Color(0xFFD97706), radius = r, center = c)
            // Círculo principal dorado brillante
            drawCircle(color = Color(0xFFFFD700), radius = r * 0.85f, center = c)
            // Borde interior en relieve
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = r * 0.65f,
                center = c,
                style = Stroke(width = r * 0.15f)
            )
        }
        Text(
            text = "C",
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF78350F)
        )
    }
}

@Composable
fun ChoyoCoinsBadge(
    amount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChoyoCoinIcon(size = 18.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "%,d".format(amount),
                fontWeight = FontWeight.ExtraBold,
                color = Amber800,
                fontSize = 14.sp
            )
        }
    }
}
