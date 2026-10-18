package com.example.pasoaventuras.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.Mission
import com.example.pasoaventuras.ui.components.SoundManager
import com.example.pasoaventuras.ui.theme.*

@Composable
fun MisionesScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AppState.init()
    }

    var showConfetti by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header idéntico a .dashboard-header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Misiones y Metas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                    Text(
                        text = "Cumple objetivos diarios para ganar ChoyoCoins.",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }

                com.example.pasoaventuras.ui.components.ChoyoCoinsBadge(amount = AppState.coins)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(AppState.missions) { mission ->
                    MissionCardItem(
                        mission = mission,
                        onClaim = {
                            SoundManager.playCoin()
                            AppState.claimMission(mission.id)
                            showConfetti = true
                            Toast.makeText(context, "¡Has reclamado +${mission.reward} ChoyoCoins! 🪙", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Animación de confeti emergente idéntica a triggerConfetti() del HTML
        com.example.pasoaventuras.ui.components.ConfettiOverlay(
            trigger = showConfetti,
            onFinished = { showConfetti = false }
        )
    }
}

@Composable
fun MissionCardItem(mission: Mission, onClaim: () -> Unit) {
    val isCompleted = mission.isComplete

    // Animación de rebote (CSS @keyframes bounceClaim { from: translateY(0); to: translateY(-2px); })
    val infiniteTransition = rememberInfiniteTransition(label = "claimBounce")
    val bounceOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceY"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mission.claimed) Color(0xFFF8FAFC) else if (isCompleted) Color(0xFFF0FDF4) else Color.White
        ),
        border = BorderStroke(
            if (isCompleted && !mission.claimed) 1.5.dp else 1.dp,
            if (isCompleted && !mission.claimed) Emerald500 else Slate200
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top: título, descripción y recompensa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mission.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mission.claimed) Slate400 else Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mission.description,
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }

                // Reward badge (.mission-reward)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+${mission.reward}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Amber800,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de progreso con gradiente primario a esmeralda (.mission-progress-bar-fill)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(mission.progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Indigo500, Emerald500)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom: porcentaje y botón de reclamar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val percent = (mission.progress * 100).toInt()
                Text(
                    text = "$percent% Completado",
                    fontSize = 11.sp,
                    color = Slate500,
                    fontWeight = FontWeight.Bold
                )

                if (mission.claimed) {
                    Text(
                        text = "✓ Reclamada",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald500
                    )
                } else if (isCompleted) {
                    Button(
                        onClick = onClaim,
                        modifier = Modifier
                            .offset(y = bounceOffsetY.dp)
                            .height(34.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald500
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        Text(
                            text = "¡Reclamar +${mission.reward} 🪙!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                } else {
                    val unit = if (mission.id == "m3") "invocaciones" else if (mission.id == "m2") "kcal" else "pasos"
                    Text(
                        text = "${mission.current} / ${mission.goal} $unit",
                        fontSize = 11.sp,
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
