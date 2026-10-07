package com.example.pasoaventuras.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.CardData
import com.example.pasoaventuras.data.CardRepository
import com.example.pasoaventuras.ui.components.ChoyoCoinsBadge
import com.example.pasoaventuras.ui.components.SoundManager
import com.example.pasoaventuras.ui.theme.*

data class FilterTab(val id: String, val label: String)

@Composable
fun AlbumScreen() {
    var selectedCard by remember { mutableStateOf<CardData?>(null) }
    var currentFilter by remember { mutableStateOf("all") }

    val ownedCardIds = remember(AppState.ownedCards.size) {
        AppState.ownedCards.map { it.card.id }.toSet()
    }

    val filterTabs = listOf(
        FilterTab("all", "Todas"),
        FilterTab("unlocked", "Desbloqueadas"),
        FilterTab("locked", "Pendientes"),
        FilterTab("5", "⭐⭐⭐⭐⭐"),
        FilterTab("4", "⭐⭐⭐⭐"),
    )

    val filteredCards = remember(currentFilter, ownedCardIds) {
        CardRepository.allCards.filter { c ->
            val isUnlocked = ownedCardIds.contains(c.id)
            when (currentFilter) {
                "all" -> true
                "unlocked" -> isUnlocked
                "locked" -> !isUnlocked
                "5" -> c.rarity == 5
                "4" -> c.rarity == 4
                else -> true
            }
        }
    }

    val totalCount = CardRepository.allCards.size
    val unlockedCount = ownedCardIds.size
    val percent = if (totalCount > 0) (unlockedCount * 100) / totalCount else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 1. Header Card (.album-header-card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Álbum TCG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                    Text(
                        text = "Coleccionadas: $unlockedCount / $totalCount ($percent%)",
                        fontSize = 12.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium
                    )
                }

                ChoyoCoinsBadge(amount = AppState.coins)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Filtros de Álbum con scroll horizontal (.album-filter-tabs)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterTabs) { tab ->
                val isActive = currentFilter == tab.id
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isActive) Indigo500 else Color.White,
                    border = BorderStroke(1.dp, if (isActive) Indigo500 else Slate200),
                    modifier = Modifier.clickable {
                        SoundManager.playClick()
                        currentFilter = tab.id
                    }
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else Slate500,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Grid de Cartas TCG 3D (.album-grid: 3 columnas, aspect ratio 3/4.2)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCards) { card ->
                val isUnlocked = ownedCardIds.contains(card.id)

                val cardBgGradient = if (isUnlocked) {
                    when (card.rarity) {
                        5 -> Brush.linearGradient(listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309)))
                        4 -> Brush.linearGradient(listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFF6B21A8)))
                        3 -> Brush.linearGradient(listOf(Color(0xFFBAE6FD), Color(0xFF0284C7), Color(0xFF0369A1)))
                        2 -> Brush.linearGradient(listOf(Color(0xFFBBF7D0), Color(0xFF16A34A), Color(0xFF15803D)))
                        else -> Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569)))
                    }
                } else {
                    Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.71f)
                        .clickable(enabled = isUnlocked) {
                            SoundManager.playClick()
                            selectedCard = card
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        1.5.dp,
                        if (isUnlocked) Color.White.copy(alpha = 0.5f) else Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(cardBgGradient)
                            .padding(6.dp)
                    ) {
                        if (isUnlocked) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Badge rareza y elemento
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x59000000),
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = "${card.rarity}★ ${card.element}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                // Arte central del personaje
                                Text(
                                    text = card.emoji,
                                    fontSize = 32.sp
                                )

                                // Footer con nombre y estrellas
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = card.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "★".repeat(card.rarity),
                                        fontSize = 8.sp,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }
                        } else {
                            // Estado Bloqueado (.card-item.locked)
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🔒", fontSize = 24.sp, color = Color(0xFF64748B))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "#${card.id.padStart(3, '0')}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "Bloqueada",
                                    fontSize = 9.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Detalle de Carta TCG 3D (.card-detail-modal)
    if (selectedCard != null) {
        val card = selectedCard!!

        val cardBgGradient = when (card.rarity) {
            5 -> Brush.linearGradient(listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309)))
            4 -> Brush.linearGradient(listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFF6B21A8)))
            3 -> Brush.linearGradient(listOf(Color(0xFFBAE6FD), Color(0xFF0284C7), Color(0xFF0369A1)))
            2 -> Brush.linearGradient(listOf(Color(0xFFBBF7D0), Color(0xFF16A34A), Color(0xFF15803D)))
            else -> Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569)))
        }

        Dialog(onDismissRequest = { selectedCard = null }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    // Botón Cerrar (✕)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { selectedCard = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = Slate700, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Previsualización de Carta
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .height(210.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.6f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(cardBgGradient)
                                    .padding(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
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
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(text = card.emoji, fontSize = 56.sp)

                                    Text(
                                        text = card.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = card.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )

                        Text(
                            text = "★".repeat(card.rarity) + " (${card.rarity} Estrellas)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber800,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        Text(
                            text = card.lore,
                            fontSize = 12.sp,
                            color = Slate600,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Ficha Técnica de Habilidades
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row {
                                    Text(text = "Elemento: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                                    Text(text = card.element, fontSize = 11.sp, color = Slate600)
                                }
                                Row {
                                    Text(text = "Habilidad de Pasos: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                                    Text(text = card.skill, fontSize = 11.sp, color = Slate600)
                                }
                                Row {
                                    Text(text = "Fecha de Obtención: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                                    Text(text = "Hoy", fontSize = 11.sp, color = Slate600)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
