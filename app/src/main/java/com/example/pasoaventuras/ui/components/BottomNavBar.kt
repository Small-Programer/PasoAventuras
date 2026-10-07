package com.example.pasoaventuras.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.ui.theme.*

data class NavItem(val route: String, val label: String, val icon: String)

val bottomNavItems = listOf(
    NavItem("dashboard", "Inicio", "👟"),
    NavItem("misiones", "Misiones", "🎯"),
    NavItem("gacha", "Gacha", "🔮"),
    NavItem("album", "Álbum", "🃏"),
    NavItem("perfil", "Perfil", "⚙️"),
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = Color(0xF5FFFFFF),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xCCE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route

                // Animación de traslación y escala (CSS: translateY(-2px) scale(1.15))
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.18f else 1f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "navIconScale"
                )
                val iconOffsetY by animateDpAsState(
                    targetValue = if (selected) (-3).dp else 0.dp,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "navIconOffset"
                )

                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            SoundManager.playClick()
                            onNavigate(item.route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Píldora de selección (.nav-btn.active::before)
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(26.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = 2.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE0E7FF))
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.icon,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .offset(y = iconOffsetY)
                                .scale(iconScale)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (selected) Indigo500 else Color(0xFF64748B)
                        )
                    }

                    // Punto rojo sobre la pestaña de misiones (.nav-badge-dot)
                    if (item.route == "misiones" && AppState.hasUnclaimedMissions) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 6.dp)
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
        }
    }
}
