package com.example.pasoaventuras.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.ui.components.SoundManager
import com.example.pasoaventuras.ui.components.StepRing
import com.example.pasoaventuras.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()

    // Timer para la caminata en vivo continua si está activa
    LaunchedEffect(AppState.isWalking) {
        while (AppState.isWalking) {
            delay(1000)
            val randomSteps = (3..6).random()
            AppState.addSteps(randomSteps)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFA78BFA), Color(0xFF4F46E5))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "¡Hola, ${AppState.userName}!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                    Text(
                        text = "Nivel ${AppState.level} • Explorador Pro",
                        fontSize = 12.sp,
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Coin Badge con vector personalizado
            com.example.pasoaventuras.ui.components.ChoyoCoinsBadge(amount = AppState.coins)
        }

        // Anillo Circular de Pasos (Card idéntica a .circle-progress-card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StepRing(
                    progress = AppState.progress,
                    steps = AppState.steps,
                    goal = AppState.dailyGoal
                )
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "${AppState.progressPercent}% Completado",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Choyo Companion Widget (.choyo-companion)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFEFF6FF),
            border = BorderStroke(1.5.dp, Color(0xFF818CF8))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐣", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (AppState.steps >= AppState.dailyGoal)
                        "\"¡FELICIDADES! ¡Meta superada con éxito! Reclama tus premios en Misiones.\" 🏆🎉"
                    else if (AppState.progressPercent < 30)
                        "\"¡Un gran viaje comienza con el primer paso! Sal a estirar las piernas.\" 🐾"
                    else
                        "\"¡Vas con gran energía hoy! Solo te faltan unos pasos para tu recompensa.\" ⚡",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E1B4B),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Matriz de Métricas (.metrics-grid: 3 columnas)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "🔥",
                value = "${AppState.caloriesBurned}",
                label = "kcal quemadas"
            )
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "📍",
                value = "%.1f".format(AppState.distanceKm),
                label = "km recorridos"
            )
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "⏱️",
                value = "${AppState.steps / 100}",
                label = "min activos"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulador de Sensor de Hardware (.sensor-controller)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (AppState.isWalking || AppState.isSensorAvailable) Emerald500 else Slate400)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SENSOR: TYPE_STEP_COUNTER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate600,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = if (AppState.isSensorAvailable) "Sensor Nativo" else "Simulador",
                        fontSize = 11.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones +100, +500, +1000
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            SoundManager.playStep()
                            AppState.addSteps(100)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("+100", color = Slate900, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            SoundManager.playStep()
                            AppState.addSteps(500)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("+500", color = Slate900, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            SoundManager.playStep()
                            AppState.addSteps(1000)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("+1,000", color = Slate900, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón Caminata en Vivo Continua (.toggle-walk-btn)
                Button(
                    onClick = {
                        SoundManager.playClick()
                        AppState.isWalking = !AppState.isWalking
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (AppState.isWalking) Color(0xFFFEE2E2) else Slate100
                    )
                ) {
                    Text(
                        text = if (AppState.isWalking) "🛑 Detener Caminata Activa (+5 pasos/s)" else "🚶‍♂️ Simular Caminata en Vivo: APAGADA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (AppState.isWalking) Color(0xFFDC2626) else Slate700
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MetricTile(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Slate900
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Slate500,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
