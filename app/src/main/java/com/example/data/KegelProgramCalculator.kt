package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class KegelPhase(
    val phaseNumber: Int,
    val phaseName: String,
    val weekRange: String,
    val slowHoldSeconds: Int,
    val slowReps: Int,
    val fastReps: Int,
    val sessionsPerDay: Int,
    val description: String,
    val extraInstruction: String
)

object KegelProgramCalculator {
    fun getCurrentPhase(startDateStr: String): Pair<KegelPhase, Int> {
        val defaultPhase = KegelPhase(
            phaseNumber = 1,
            phaseName = "Fondations",
            weekRange = "1-4",
            slowHoldSeconds = 3,
            slowReps = 10,
            fastReps = 10,
            sessionsPerDay = 3,
            description = "Se concentrer sur l'isolation correcte du muscle PC, sans contracter abdos/fessiers.",
            extraInstruction = "Se concentrer sur l'isolation correcte du muscle PC, sans contracter abdos/fessiers."
        )

        if (startDateStr.isBlank()) {
            return Pair(defaultPhase, 1)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = try {
            sdf.parse(startDateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val diffMillis = today.time - startDate.time
        val diffDays = diffMillis / (1000 * 60 * 60 * 24)
        val weekNumber = ((diffDays / 7) + 1).toInt().coerceAtLeast(1)

        val phase = when {
            weekNumber in 1..4 -> defaultPhase
            weekNumber in 5..8 -> KegelPhase(
                phaseNumber = 2,
                phaseName = "Endurance",
                weekRange = "5-8",
                slowHoldSeconds = 5,
                slowReps = 15,
                fastReps = 15,
                sessionsPerDay = 3,
                description = "Ajouter l'exercice \"ascenseur\" : contracter progressivement par paliers 25%/50%/75%/100%, une série par jour.",
                extraInstruction = "Ajouter l'exercice \"ascenseur\" : contracter progressivement par paliers 25%/50%/75%/100%, une série par jour."
            )
            weekNumber in 9..16 -> KegelPhase(
                phaseNumber = 3,
                phaseName = "Puissance & Intégration",
                weekRange = "9-16",
                slowHoldSeconds = 10,
                slowReps = 15,
                fastReps = 20,
                sessionsPerDay = 2,
                description = "Pratiquer une contraction PC en position debout/en marchant pour renforcer le contrôle en situation réelle. Coordonner avec la respiration diaphragmatique (module Respiration déjà existant).",
                extraInstruction = "Pratiquer une contraction PC en position debout/en marchant pour renforcer le contrôle en situation réelle. Coordonner avec la respiration diaphragmatique (module Respiration déjà existant)."
            )
            else -> KegelPhase( // weekNumber >= 17 (beyond week 24, continues in Phase 4)
                phaseNumber = 4,
                phaseName = "Maîtrise & Maintenance",
                weekRange = "17-24",
                slowHoldSeconds = 15,
                slowReps = 15,
                fastReps = 15,
                sessionsPerDay = 1, // 1-2 sessions, let's specify 1 as base
                description = "Combiner avec la technique Stop-Start (déjà présente) en pratique solo. Passage en mode maintenance dès la semaine 20 si le contrôle est jugé stable.",
                extraInstruction = "Combiner avec la technique Stop-Start (déjà présente) en pratique solo. Passage en mode maintenance dès la semaine 20 si le contrôle est jugé stable."
            )
        }

        return Pair(phase, weekNumber)
    }
}
