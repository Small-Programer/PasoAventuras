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
import com.example.pasoaventuras.ui.components.StepRing
import com.example.pasoaventuras.ui.theme.*

@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()

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
                        text = "¡Hola, ${AppState.userName.ifEmpty { "Aventurero" }}!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                    Text(
                        text = "Nivel ${AppState.level} • ${AppState.levelTitle}",
                        fontSize = 12.sp,
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            com.example.pasoaventuras.ui.components.ChoyoCoinsBadge(amount = AppState.coins)
        }

        // Anillo Circular de Pasos
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

        // Choyo Companion Widget
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
                        "\"¡Un gran viaje comienza con el primer paso! Sal a caminar un poco.\" 🐾"
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

        // Matriz de Métricas
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

        // Alerta si va en vehículo
        if (AppState.isInVehicle) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFEF2F2)
                ),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🚗", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo vehículo detectado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "Velocidad: ${AppState.currentSpeedKmh.toInt()} km/h. El conteo de pasos está pausado para evitar pasos falsos.",
                            fontSize = 11.sp,
                            color = Color(0xFFB91C1C),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Estado del Podómetro físico y GPS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (AppState.isSensorAvailable) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
            ),
            border = BorderStroke(
                1.dp,
                if (AppState.isSensorAvailable) Color(0xFF86EFAC) else Color(0xFFFDE68A)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Fila Podómetro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (AppState.isSensorAvailable) Color(0xFF22C55E) else Color(0xFFF59E0B)
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        val title = when {
                            !AppState.isSensorAvailable -> "Podómetro no disponible"
                            AppState.hasHardwarePedometer -> "Podómetro por hardware activo"
                            else -> "Podómetro por acelerómetro (Calibrado)"
                        }
                        val desc = when {
                            !AppState.isSensorAvailable -> "No se detectó sensor de movimiento o faltan permisos"
                            AppState.hasHardwarePedometer -> "Sensor físico dedicado de pasos funcionando"
                            else -> "Acelerómetro calibrado con filtro dinámico y anti-rebote"
                        }
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (AppState.isSensorAvailable) Color(0xFF166534) else Color(0xFF92400E)
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = if (AppState.isSensorAvailable) Color(0xFF15803D) else Color(0xFFD97706),
                            lineHeight = 14.sp
                        )

                        if (!AppState.isSensorAvailable) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { AppState.onRequestPermissions?.invoke() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Activar sensor", fontSize = 11.sp, color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = { AppState.onOpenAppSettings?.invoke() },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Ajustes", fontSize = 11.sp, color = Color(0xFF92400E))
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(if (AppState.isSensorAvailable) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                )

                // Fila GPS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (AppState.isGpsActive) Color(0xFF0EA5E9) else Color(0xFF94A3B8)
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (AppState.isGpsActive) "GPS activo (Validación de distancia)" else "GPS conectando / inactivo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (AppState.isGpsActive) Color(0xFF0369A1) else Color(0xFF475569)
                        )
                        Text(
                            text = if (AppState.isGpsActive)
                                "Distancia GPS: ${String.format(java.util.Locale.US, "%.2f", AppState.gpsDistanceKm)} km · Velocidad: ${AppState.currentSpeedKmh.toInt()} km/h"
                            else
                                "Permite mayor precisión y detección de transporte",
                            fontSize = 11.sp,
                            color = if (AppState.isGpsActive) Color(0xFF0284C7) else Color(0xFF64748B),
                            lineHeight = 14.sp
                        )
                    }
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
