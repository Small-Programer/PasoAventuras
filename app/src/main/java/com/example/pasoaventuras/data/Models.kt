package com.example.pasoaventuras.data

data class CardData(
    val id: String,
    val name: String,
    val rarity: Int,          // 1-5 stars
    val element: String,
    val emoji: String,
    val lore: String,
    val skill: String,
    val color: Long           // ARGB hex
)

data class CardInstance(
    val card: CardData,
    val instanceId: String = java.util.UUID.randomUUID().toString()
)

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val goal: Int,
    var current: Int = 0,
    val reward: Int,           // ChoyoCoins reward
    var claimed: Boolean = false,
    val isWeekly: Boolean = false
) {
    val progress: Float get() = if (goal == 0) 1f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val isComplete: Boolean get() = current >= goal
}
