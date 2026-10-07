package com.example.pasoaventuras.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.CardData
import com.example.pasoaventuras.data.CardInstance
import com.example.pasoaventuras.data.CardRepository
import com.example.pasoaventuras.ui.components.ChoyoCoinIcon
import com.example.pasoaventuras.ui.components.ChoyoCoinsBadge
import com.example.pasoaventuras.ui.components.SoundManager
import com.example.pasoaventuras.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun GachaScreen(onGoToAlbum: () -> Unit = {}) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var pullResults by remember { mutableStateOf<List<CardInstance>?>(null) }
    var showRevealModal by remember { mutableStateOf(false) }
    var showAdModal by remember { mutableStateOf(false) }
    var ratesExpanded by remember { mutableStateOf(false) }

    // Animación de pulso/flotación del orbe (@keyframes floatGacha)
    val infiniteTransition = rememberInfiniteTransition(label = "gachaOrbFloat")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )
    val orbOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbY"
    )

    fun executePull(amount: Int) {
        val list = AppState.pullGacha(amount)
        if (list == null) {
            Toast.makeText(context, "¡No tienes suficientes ChoyoCoins! Camina más o mira un video. ❌", Toast.LENGTH_SHORT).show()
            return
        }

        val highestRarity = list.maxOfOrNull { it.card.rarity } ?: 1

        // Sonido según la rareza más alta
        if (highestRarity >= 4) {
            SoundManager.playLegendary()
        } else {
            SoundManager.playCoin()
        }

        pullResults = list
        showRevealModal = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Invocación Choyo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Slate900
                )
                Text(
                    text = "Desbloquea tarjetas místicas para tu álbum.",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            ChoyoCoinsBadge(amount = AppState.coins)
        }

        // 1. Escenario del Gachapón (.gacha-stage)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Invocación Celestial",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Obtén a Choyuma Solar (5★) con probabilidad aumentada",
                    fontSize = 12.sp,
                    color = Color(0xFFC7D2FE),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Orbe Mágico interactivo (.gacha-orb)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(y = orbOffsetY.dp)
                        .scale(orbScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFF59E0B),
                                    Color(0xFF7C2D12)
                                )
                            )
                        )
                        .clickable { executePull(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔮", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Badges de probabilidad rápida
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x26FFFFFF)
                    ) {
                        Text(
                            text = "⭐ 5★ Legendario 1%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF08A),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x26FFFFFF)
                    ) {
                        Text(
                            text = "💜 4★ Épico 6%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE9D5FF),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Botones de Invocación (.gacha-actions: Invocar x1 e Invocar x5)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Invocar x1
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { executePull(1) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Invocar x1",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFFBEB)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChoyoCoinIcon(size = 14.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "50",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Amber800
                            )
                        }
                    }
                }
            }

            // Invocar x5 (-10% descuento)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { executePull(5) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Invocar x5",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFFBEB)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChoyoCoinIcon(size = 14.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "225",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Amber800
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "-10%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald500
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Banner de Video Bonificado AdMob (.ad-banner-box)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAdModal = true },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📺 Ver Video Bonificado (AdMob)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Gana +50 ChoyoCoins gratis al instante",
                        fontSize = 11.sp,
                        color = Color(0xFFECFDF5)
                    )
                }
                Text(text = "🎁", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Tabla de Probabilidades Plegable (.rates-collapsible)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { ratesExpanded = !ratesExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 Tabla de Probabilidades y Rarezas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate600
                    )
                    Text(
                        text = if (ratesExpanded) "▲" else "▼",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }

                if (ratesExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ProbabilityRow("⭐⭐⭐⭐⭐ Legendario", "1.0%")
                        ProbabilityRow("⭐⭐⭐⭐ Épico", "6.0%")
                        ProbabilityRow("⭐⭐⭐ Raro", "13.0%")
                        ProbabilityRow("⭐⭐ Poco Común", "30.0%")
                        ProbabilityRow("⭐ Común", "50.0%")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔄 Cartas duplicadas otorgan +15 ChoyoCoins.",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal de Cinemática de Invocación (.gacha-reveal-box)
    if (showRevealModal && pullResults != null) {
        val results = pullResults!!

        Dialog(onDismissRequest = { showRevealModal = false }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = BorderStroke(1.5.dp, Color(0x4DFFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (results.size == 1) "¡Invocación Exitosa!" else "¡Invocación x5 Completada!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (results.size == 1) "Un nuevo espíritu se ha unido a tu causa" else "Has obtenido 5 cartas místicas para tu inventario.",
                        fontSize = 11.sp,
                        color = Color(0xFFC7D2FE)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (results.size == 1) {
                        val card = results.first().card
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .height(250.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(card.color)),
                            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x66000000),
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = "${card.rarity}★ ${card.element}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(text = card.emoji, fontSize = 64.sp)

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = card.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "★".repeat(card.rarity),
                                        fontSize = 12.sp,
                                        color = Color(0xFFFFD700)
                                    )
                                    Text(
                                        text = card.lore,
                                        fontSize = 9.sp,
                                        color = Color(0xFFE2E8F0),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    } else {
                        // Cuadrícula mini cards x5
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            results.forEach { instance ->
                                val card = instance.card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (card.rarity >= 4) Color(0xFFD97706) else Color(0x33FFFFFF)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = card.emoji, fontSize = 20.sp)
                                        Text(
                                            text = card.name,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${card.rarity}★",
                                            fontSize = 8.sp,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showRevealModal = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500)
                        ) {
                            Text("¡Excelente!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                showRevealModal = false
                                onGoToAlbum()
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Slate700)
                        ) {
                            Text("Ver Álbum", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal de Anuncio Bonificado AdMob Simulado (.admob-modal-box)
    if (showAdModal) {
        var countdown by remember { mutableIntStateOf(5) }
        var isClaimable by remember { mutableStateOf(false) }

        LaunchedEffect(showAdModal) {
            countdown = 5
            isClaimable = false
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            isClaimable = true
        }

        Dialog(onDismissRequest = { if (isClaimable) showAdModal = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Google AdMob • Anuncio Bonificado",
                            fontSize = 11.sp,
                            color = Slate400,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${countdown}s",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Video Sim
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎮", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Descubre Nuevas Aventuras",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Patrocinado por PasoAventuras Partners",
                            fontSize = 11.sp,
                            color = Color(0xFFBAE6FD)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (countdown > 0) "$countdown" else "✓",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    // Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Espera a que termine el video para recibir tus 50 ChoyoCoins",
                            fontSize = 11.sp,
                            color = Slate400,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (isClaimable) {
                                    AppState.coins += 50
                                    SoundManager.playCoin()
                                    showAdModal = false
                                    Toast.makeText(context, "¡Recibiste +50 ChoyoCoins de AdMob! 🎁", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isClaimable,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald500,
                                disabledContainerColor = Slate700
                            )
                        ) {
                            Text(
                                text = if (isClaimable) "¡Reclamar +50 ChoyoCoins!" else "Reproduciendo anuncio (${countdown}s)...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProbabilityRow(label: String, percent: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate700, fontWeight = FontWeight.Medium)
        Text(text = percent, fontSize = 12.sp, color = Slate900, fontWeight = FontWeight.Bold)
    }
}
