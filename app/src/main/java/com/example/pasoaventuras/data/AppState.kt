package com.example.pasoaventuras.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppState {
    // User
    var userName by mutableStateOf("")
    var userEmail by mutableStateOf("")
    var userAvatar by mutableStateOf("Aventurero")
    var isLoggedIn by mutableStateOf(false)
    var level by mutableIntStateOf(1)
    var streak by mutableIntStateOf(0)
    var xp by mutableIntStateOf(0)

    // Steps & Goals
    var steps by mutableIntStateOf(0)
    var totalSteps by mutableIntStateOf(0)
    var dailyGoal by mutableIntStateOf(10000)
    var isSensorAvailable by mutableStateOf(false)
    var isWalking by mutableStateOf(false)

    // Economy
    var coins by mutableIntStateOf(0)
    var audioEnabled by mutableStateOf(true)

    // Collection
    val ownedCards = mutableStateListOf<CardInstance>()

    // Missions
    val missions = mutableStateListOf<Mission>()
    val hasUnclaimedMissions: Boolean get() = missions.any { it.isComplete && !it.claimed }

    // Contador para guardar en Firebase cada N pasos
    private var stepsSinceLastSave = 0

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

    // Tracking de fecha y semana para reinicio de misiones
    var lastDailyDate by mutableStateOf("")
    var lastWeeklyYearWeek by mutableStateOf("")

    // Historial de pasos por fecha (ej. "2026-10-18" -> 4520)
    val dailyStepsHistory = mutableStateMapOf<String, Int>()

    fun getCurrentDateKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    private fun getCurrentYearWeekKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "%04d-W%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.WEEK_OF_YEAR)
        )
    }

    fun checkAndResetMissionsBySchedule() {
        val today = getCurrentDateKey()
        val thisWeek = getCurrentYearWeekKey()
        var changed = false

        // Solo si ya teníamos una fecha previa registrada y hoy es un día distinto
        if (lastDailyDate.isNotEmpty() && lastDailyDate != today) {
            if (steps > 0) {
                dailyStepsHistory[lastDailyDate] = steps
            }
            steps = 0
            missions.forEachIndexed { index, mission ->
                if (!mission.isWeekly) {
                    missions[index] = mission.copy(current = 0, claimed = false)
                }
            }
            lastDailyDate = today
            changed = true
        } else if (lastDailyDate.isEmpty()) {
            lastDailyDate = today
        }

        if (lastWeeklyYearWeek.isNotEmpty() && lastWeeklyYearWeek != thisWeek) {
            missions.forEachIndexed { index, mission ->
                if (mission.isWeekly) {
                    missions[index] = mission.copy(current = 0, claimed = false)
                }
            }
            lastWeeklyYearWeek = thisWeek
            changed = true
        } else if (lastWeeklyYearWeek.isEmpty()) {
            lastWeeklyYearWeek = thisWeek
        }

        updateMissions()
        if (changed && isLoggedIn) {
            FirebaseManager.saveUserDataToFirestore()
        }
    }

    fun init() {
        if (missions.isEmpty()) {
            missions.addAll(listOf(
                Mission("m1", "Paseo Matutino", "Camina 3,000 pasos hoy", 3000, 0, 25, false, false),
                Mission("m2", "Quema de Calorias", "Quema al menos 200 kcal", 5000, 0, 30, false, false),
                Mission("m3", "Invocador Novato", "Realiza 1 invocacion en el Gacha", 1, 0, 40, false, false),
                Mission("m4", "Maraton de Resistencia", "Alcanza 10,000 pasos en un solo dia", 10000, 0, 60, false, false),
                Mission("m5", "Explorador Epico", "Acumula 15,000 pasos semanales", 15000, 0, 100, false, true),
            ))
        }
        updateMissions()
    }

    fun reset() {
        userName = ""
        userEmail = ""
        userAvatar = "Aventurero"
        level = 1
        streak = 0
        xp = 0
        steps = 0
        totalSteps = 0
        coins = 0
        isLoggedIn = false
        isSensorAvailable = false
        isWalking = false
        lastDailyDate = ""
        lastWeeklyYearWeek = ""
        dailyStepsHistory.clear()
        ownedCards.clear()
        missions.clear()
        init()
        stepsSinceLastSave = 0
    }

    fun addSteps(count: Int) {
        steps += count
        totalSteps += count
        xp += (count / 20).coerceAtLeast(1)

        val todayKey = getCurrentDateKey()
        dailyStepsHistory[todayKey] = steps

        val coinsEarned = count / 100
        if (coinsEarned > 0) coins += coinsEarned

        updateLevel()
        updateMissions()

        // Guardar en Firebase cada 20 pasos para que se refleje rápido en la nube
        stepsSinceLastSave += count
        if (stepsSinceLastSave >= 20) {
            stepsSinceLastSave = 0
            FirebaseManager.saveUserDataToFirestore()
        }
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

        val m3Index = missions.indexOfFirst { it.id == "m3" }
        if (m3Index >= 0) {
            val m3 = missions[m3Index]
            val newCurrent = (m3.current + amount).coerceAtMost(m3.goal)
            missions[m3Index] = m3.copy(current = newCurrent)
        }

        FirebaseManager.saveUserDataToFirestore()
        return pulled
    }

    fun resetDailySteps() {
        steps = 0
        stepsSinceLastSave = 0
        updateMissions()
        FirebaseManager.saveUserDataToFirestore()
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
