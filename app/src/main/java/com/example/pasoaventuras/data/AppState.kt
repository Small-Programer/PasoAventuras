package com.example.pasoaventuras.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppState {
    // User
    var userName by mutableStateOf("Carlos Andrey")
    var userEmail by mutableStateOf("carlos@correo.com")
    var userAvatar by mutableStateOf("👑")
    var isLoggedIn by mutableStateOf(false)
    var level by mutableIntStateOf(12)
    var streak by mutableIntStateOf(5)
    var xp by mutableIntStateOf(780)

    // Steps & Goals
    var steps by mutableIntStateOf(8432)
    var totalSteps by mutableIntStateOf(48432)
    var dailyGoal by mutableIntStateOf(10000)
    var isSensorAvailable by mutableStateOf(false)
    var isWalking by mutableStateOf(false)

    // Economy & Settings
    var coins by mutableIntStateOf(120)  // 120 ChoyoCoins como en el HTML
    var audioEnabled by mutableStateOf(true)

    // Collection
    val ownedCards = mutableStateListOf<CardInstance>()

    // Missions
    val missions = mutableStateListOf<Mission>()
    val hasUnclaimedMissions: Boolean get() = missions.any { it.isComplete && !it.claimed }

    // Derived
    val progress: Float get() = (steps.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val progressPercent: Int get() = (progress * 100).toInt()
    val distanceKm: Float get() = steps * 0.00075f
    val caloriesBurned: Int get() = (steps * 0.04f).toInt()

    val levelTitle: String get() = when (level) {
        in 1..3 -> "Viajero"
        in 4..7 -> "Explorador"
        in 8..11 -> "Aventurero"
        in 12..14 -> "Explorador Pro"
        else -> "Explorador Legendario"
    }

    fun init() {
        if (missions.isEmpty()) {
            missions.addAll(listOf(
                Mission("m1", "Paseo Matutino", "Camina 3,000 pasos hoy", 3000, 3000, 25, true, false),
                Mission("m2", "Quema de Calorías", "Quema al menos 200 kcal", 5000, 5000, 30, true, false),
                Mission("m3", "Invocador Novato", "Realiza 1 invocación en el Gacha", 1, 0, 40, false, false),
                Mission("m4", "Maratón de Resistencia", "Alcanza 10,000 pasos en un solo día", 10000, steps, 60, false, false),
                Mission("m5", "Explorador Épico", "Acumula 15,000 pasos semanales", 15000, steps, 100, false, true),
            ))
        }

        // Cartas iniciales que ya tiene desbloqueadas en el HTML (IDs: 1, 2, 4, 11, 14)
        if (ownedCards.isEmpty()) {
            val initialCardIds = listOf("1", "2", "4", "11", "14")
            CardRepository.allCards
                .filter { initialCardIds.contains(it.id) }
                .forEach { ownedCards.add(CardInstance(it)) }
        }
    }

    fun addSteps(count: Int) {
        steps += count
        totalSteps += count
        xp += (count / 20).coerceAtLeast(1)

        val coinsEarned = count / 100
        if (coinsEarned > 0) {
            coins += coinsEarned
        }

        updateLevel()
        updateMissions()
    }

    fun claimMission(id: String) {
        val mission = missions.find { it.id == id } ?: return
        if (mission.isComplete && !mission.claimed) {
            coins += mission.reward
            xp += 100
            val idx = missions.indexOf(mission)
            if (idx >= 0) missions[idx] = mission.copy(claimed = true)
            updateLevel()
            FirebaseManager.saveUserDataToFirestore()
        }
    }

    fun pullGacha(amount: Int = 1): List<CardInstance>? {
        val cost = if (amount == 1) 50 else 225
        if (coins < cost) return null
        coins -= cost

        val pulled = mutableListOf<CardInstance>()
        for (i in 0 until amount) {
            val card = CardRepository.pullCard()
            val instance = CardInstance(card)
            ownedCards.add(instance)
            pulled.add(instance)
        }

        // Avanzar la misión de invocación (Invocador Novato)
        val m3Index = missions.indexOfFirst { it.id == "m3" }
        if (m3Index >= 0) {
            val m3 = missions[m3Index]
            val newCurrent = (m3.current + amount).coerceAtMost(m3.goal)
            missions[m3Index] = m3.copy(current = newCurrent)
        }

        FirebaseManager.saveUserDataToFirestore()
        return pulled
    }

    private fun updateLevel() {
        level = 1 + (xp / 1000)
    }

    private fun updateMissions() {
        missions.forEachIndexed { idx, mission ->
            if (!mission.claimed && mission.id != "m3") {
                val currentProgress = if (mission.id == "m2") {
                    minOf(caloriesBurned, mission.goal)
                } else {
                    minOf(steps, mission.goal)
                }
                missions[idx] = mission.copy(current = currentProgress)
            }
        }
    }
}
