package com.example.pasoaventuras.data

object CardRepository {

    val allCards: List<CardData> = listOf(
        // 5 Estrellas
        CardData(
            id = "1", name = "Choyuma Solar", rarity = 5, element = "Fuego", emoji = "☀️",
            lore = "El legendario guerrero que obtiene su fuerza vital del calor generado al caminar bajo el sol.",
            skill = "+25% ChoyoCoins por paso", color = 0xFFF59E0B
        ),
        CardData(
            id = "2", name = "Choyuku Abisal", rarity = 5, element = "Agua", emoji = "🌊",
            lore = "Guardián de las corrientes oceánicas profundas. Su paso es suave pero imparable.",
            skill = "-15% coste de tiradas Gacha", color = 0xFF0284C7
        ),
        CardData(
            id = "3", name = "Choyudragon Astral", rarity = 5, element = "Luz", emoji = "✨",
            lore = "Espíritu cósmico que bendice a los caminantes nocturnos con vitalidad inagotable.",
            skill = "+50% XP en misiones", color = 0xFF8B5CF6
        ),
        // 4 Estrellas
        CardData(
            id = "4", name = "Choyugi Ninja", rarity = 4, element = "Viento", emoji = "🌪️",
            lore = "Ágil explorador de sombras capaz de recorrer kilómetros sin dejar rastro de fatiga.",
            skill = "+10% velocidad de conteo", color = 0xFFA855F7
        ),
        CardData(
            id = "5", name = "Choyorock Coloso", rarity = 4, element = "Tierra", emoji = "⛰️",
            lore = "Guardián de las montañas rocosas que recompensa la constancia y la resistencia inquebrantable.",
            skill = "+5 monedas por cada 500 pasos", color = 0xFF78350F
        ),
        CardData(
            id = "6", name = "Choyuknight Paladín", rarity = 4, element = "Luz", emoji = "🛡️",
            lore = "Caballero sagrado que protege a su portador contra la pereza y el cansancio.",
            skill = "Doble racha de días activos", color = 0xFF6366F1
        ),
        // 3 Estrellas
        CardData(
            id = "7", name = "Choyomancer Mago", rarity = 3, element = "Arcano", emoji = "🔮",
            lore = "Canaliza la energía cinética de cada zancada para transformarla en relucientes gemas.",
            skill = "+3 monedas por misión", color = 0xFF0284C7
        ),
        CardData(
            id = "8", name = "Choyotl Silvestre", rarity = 3, element = "Planta", emoji = "🍃",
            lore = "Criatura de los bosques que florece con cada paso dado entre senderos naturales.",
            skill = "+5% quema de calorías", color = 0xFF10B981
        ),
        CardData(
            id = "9", name = "Choyobolt Chispa", rarity = 3, element = "Rayo", emoji = "⚡",
            lore = "Pequeño acumulador de estática que chisporrotea cuando aceleras el paso.",
            skill = "+8% XP de caminata", color = 0xFFF59E0B
        ),
        CardData(
            id = "10", name = "Choyofrost Escarcha", rarity = 3, element = "Hielo", emoji = "❄️",
            lore = "Domina el frío invernal manteniendo un ritmo cardíaco óptimo y fresco.",
            skill = "+10% resistencia diaria", color = 0xFF38BDF8
        ),
        // 2 Estrellas
        CardData(
            id = "11", name = "Choyorunner Veloz", rarity = 2, element = "Viento", emoji = "👟",
            lore = "Siempre equipado con sus mejores zapatillas listo para cualquier maratón.",
            skill = "+2 monedas diarias", color = 0xFF16A34A
        ),
        CardData(
            id = "12", name = "Choyoscout Guía", rarity = 2, element = "Tierra", emoji = "🧭",
            lore = "Experto en orientación con brújula que nunca pierde el rumbo hacia su meta.",
            skill = "+100 pasos de bono diario", color = 0xFF15803D
        ),
        CardData(
            id = "13", name = "Choyodrop Burbuja", rarity = 2, element = "Agua", emoji = "💧",
            lore = "Mantiene la hidratación al tope durante las caminatas más exigentes.",
            skill = "+2% eficiencia energética", color = 0xFF0EA5E9
        ),
        // 1 Estrella
        CardData(
            id = "14", name = "Choyito Caminante", rarity = 1, element = "Neutro", emoji = "🐾",
            lore = "El fiel compañero que da sus primeros pasos con alegría y entusiasmo sin igual.",
            skill = "El valor del primer paso", color = 0xFF64748B
        ),
        CardData(
            id = "15", name = "Choyispita Fuego", rarity = 1, element = "Fuego", emoji = "🔥",
            lore = "Una chispa diminuta que enciende el deseo de explorar el mundo entero.",
            skill = "Calor motivador", color = 0xFFEF4444
        )
    )

    fun pullCard(): CardData {
        val rand = (1..100).random()
        val rarity = when {
            rand <= 1  -> 5   // 1%
            rand <= 7  -> 4   // 6%
            rand <= 20 -> 3   // 13%
            rand <= 50 -> 2   // 30%
            else       -> 1   // 50%
        }
        return allCards.filter { it.rarity == rarity }.random()
    }
}
