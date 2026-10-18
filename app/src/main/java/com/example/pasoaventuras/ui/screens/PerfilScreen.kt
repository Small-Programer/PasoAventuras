package com.example.pasoaventuras.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.CardRepository
import com.example.pasoaventuras.ui.theme.*

@Composable
fun PerfilScreen(onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var goalSliderValue by remember { mutableFloatStateOf(AppState.dailyGoal.toFloat()) }
    var audioEffectsEnabled by remember { mutableStateOf(true) }

    val avatars = listOf("👑", "🐾", "🐣", "🥋", "⭐", "🐉", "🧙‍♂️")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // 1. Tarjeta de Perfil Principal (.profile-card)
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular con botón de editar (.profile-avatar-wrap)
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .padding(bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF818CF8), Color(0xFF4F46E5))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = AppState.userAvatar, fontSize = 42.sp)
                    }

                    // Badge de edición verde (.avatar-edit-badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Emerald500)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                val currentIdx = avatars.indexOf(AppState.userAvatar)
                                val nextIdx = (currentIdx + 1) % avatars.size
                                AppState.userAvatar = avatars[nextIdx]
                                Toast.makeText(context, "Avatar cambiado a ${AppState.userAvatar}", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✏️", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppState.userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Slate900
                )

                Text(
                    text = AppState.userEmail,
                    fontSize = 12.sp,
                    color = Slate500,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                // Badge de Nivel (.profile-level-badge)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE0E7FF)
                ) {
                    Text(
                        text = "Nivel ${AppState.level} • Explorador Legendario",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3730A3),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barra de Progreso XP (.xp-bar-container)
                val currentXP = AppState.xp % 1000
                val xpPercent = (currentXP / 1000f).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { xpPercent },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = Indigo500,
                    trackColor = Color(0xFFF1F5F9),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$currentXP / 1000 XP para el Nivel ${AppState.level + 1}",
                    fontSize = 11.sp,
                    color = Slate500,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Estadísticas Globales Históricas (3 columnas como en HTML)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "🏆",
                value = "%,d".format(AppState.totalSteps),
                label = "Pasos Totales"
            )
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "🃏",
                value = "${AppState.ownedCards.size}/${CardRepository.allCards.size}",
                label = "Cartas Colección"
            )
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = "🔥",
                value = "${AppState.streak} días",
                label = "Racha Activa"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfica de Historial Semanal de Pasos (Estilo Bienestar Digital)
        StepHistoryChart(
            history = AppState.dailyStepsHistory,
            dailyGoal = AppState.dailyGoal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Banner Freemium PasoAventuras Pro (.pro-banner)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            border = BorderStroke(1.dp, Color(0xFF6366F1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PasoAventuras Pro",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFD700)
                    ) {
                        Text(
                            text = "MEMBRESÍA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF451A03),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Multiplicador x2 en todas las ChoyoCoins obtenidas.\n• Exportación de Reporte Médico de Actividad en PDF.\n• 1 Invocación Gratuita diaria en el Gachapón.",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "📄 Reporte de Salud exportado con éxito (Simulado)", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500)
                ) {
                    Text(
                        text = "📄 Descargar Reporte de Salud (PDF Simulado)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Ajustes (.settings-list)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Slider de Meta Diaria (.setting-box)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "META DIARIA DE PASOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate500,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${goalSliderValue.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")} pasos",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                        Text(
                            text = "Recomendado por OMS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald500
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = goalSliderValue,
                        onValueChange = {
                            goalSliderValue = it
                            AppState.dailyGoal = it.toInt()
                        },
                        valueRange = 3000f..25000f,
                        steps = 43,
                        colors = SliderDefaults.colors(
                            thumbColor = Indigo500,
                            activeTrackColor = Indigo500,
                            inactiveTrackColor = Slate200
                        )
                    )
                }
            }

            // Efectos de Audio (.setting-box con switch)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Efectos de Audio",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Sonidos para monedas, gacha y logros",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }

                    Switch(
                        checked = audioEffectsEnabled,
                        onCheckedChange = { audioEffectsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Indigo500
                        )
                    )
                }
            }

            // Botón Cerrar Sesión
            Button(
                onClick = {
                    com.example.pasoaventuras.data.FirebaseManager.logout {
                        Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Slate100)
            ) {
                Text(
                    text = "🚪 Cerrar Sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

data class DayStepData(
    val dayLabel: String,
    val dateKey: String,
    val steps: Int,
    val isToday: Boolean
)

@Composable
fun StepHistoryChart(
    history: Map<String, Int>,
    dailyGoal: Int
) {
    val cal = java.util.Calendar.getInstance()
    val todayKey = "%04d-%02d-%02d".format(
        cal.get(java.util.Calendar.YEAR),
        cal.get(java.util.Calendar.MONTH) + 1,
        cal.get(java.util.Calendar.DAY_OF_MONTH)
    )

    val dayNames = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    val past7Days = remember(history, todayKey) {
        val list = mutableListOf<DayStepData>()
        val tempCal = java.util.Calendar.getInstance()
        // Ir hacia atrás 6 días para mostrar de hace 6 días hasta hoy (7 días en total)
        tempCal.add(java.util.Calendar.DAY_OF_YEAR, -6)
        for (i in 0..6) {
            val key = "%04d-%02d-%02d".format(
                tempCal.get(java.util.Calendar.YEAR),
                tempCal.get(java.util.Calendar.MONTH) + 1,
                tempCal.get(java.util.Calendar.DAY_OF_MONTH)
            )
            val dayOfWeek = tempCal.get(java.util.Calendar.DAY_OF_WEEK) - 1
            val stepCount = history[key] ?: 0
            list.add(
                DayStepData(
                    dayLabel = dayNames[dayOfWeek],
                    dateKey = key,
                    steps = stepCount,
                    isToday = key == todayKey
                )
            )
            tempCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val maxSteps = maxOf(dailyGoal, past7Days.maxOfOrNull { it.steps } ?: 1, 1000)
    val totalWeeklySteps = past7Days.sumOf { it.steps }
    val avgSteps = totalWeeklySteps / 7

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HISTORIAL DE ACTIVIDAD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate500,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Últimos 7 días",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = "Prom: %,d / día".format(avgSteps),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Indigo700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gráfica de barras
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                past7Days.forEach { day ->
                    val barHeightRatio = (day.steps.toFloat() / maxSteps).coerceIn(0.04f, 1f)
                    val goalReached = day.steps >= dailyGoal

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Cantidad arriba si es mayor a 0
                        if (day.steps > 0) {
                            Text(
                                text = if (day.steps >= 1000) "%.1fk".format(day.steps / 1000f) else "${day.steps}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (day.isToday) Indigo700 else Slate400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Barra
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(barHeightRatio)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    when {
                                        goalReached -> Color(0xFF10B981) // Verde meta cumplida
                                        day.isToday -> Indigo500         // Índigo hoy
                                        day.steps > 0 -> Color(0xFF818CF8) // Lavanda pasos dados
                                        else -> Slate200                  // Gris vacío
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Etiqueta del día
                        Text(
                            text = day.dayLabel,
                            fontSize = 11.sp,
                            fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (day.isToday) Indigo500 else Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Leyenda informativa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Meta superada", fontSize = 10.sp, color = Slate500)

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Indigo500)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Hoy", fontSize = 10.sp, color = Slate500)
            }
        }
    }
}
