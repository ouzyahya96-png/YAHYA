package com.example.data

import com.example.ui.OperationsViewModel
import java.text.SimpleDateFormat
import java.util.*

data class PriorityAlert(
    val text: String,
    val priority: Int, // 1 = le plus urgent
    val sourcePageIndex: Int,
    val alertId: String // identifiant stable pour la logique de rejet (dismiss) du jour
)

object PriorityAlertsEngine {
    fun getTopAlerts(viewModel: OperationsViewModel): List<PriorityAlert> {
        val alerts = mutableListOf<PriorityAlert>()
        val todayStr = viewModel.getTodayDate()
        val chantiers = viewModel.chantiers.value
        val allMilestones = viewModel.allMilestones.value
        val pelvicChecks = viewModel.pelvicTensionChecks.value
        val supplementLogs = viewModel.supplementLogs.value
        val kegelLogs = viewModel.kegelLogs.value

        // 1. Chantier en retard
        for (chantier in chantiers) {
            val isEnRetardByStatus = chantier.status == "En retard"
            val isEnRetardByMilestone = allMilestones.any { 
                it.chantierId == chantier.id && !it.completed && it.targetDate < todayStr 
            }
            if (isEnRetardByStatus || isEnRetardByMilestone) {
                alerts.add(
                    PriorityAlert(
                        text = "Le chantier ${chantier.name} est en retard — action requise.",
                        priority = 1,
                        sourcePageIndex = 15,
                        alertId = "chantier_late_${chantier.id}"
                    )
                )
            }
        }

        // 2. Jalon de chantier à échéance dans moins de 3 jours
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (milestone in allMilestones) {
            if (!milestone.completed) {
                try {
                    val todayVal = sdf.parse(todayStr)
                    val targetVal = sdf.parse(milestone.targetDate)
                    if (todayVal != null && targetVal != null) {
                        val diffMs = targetVal.time - todayVal.time
                        val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
                        if (diffDays in 0..2) {
                            val chantier = chantiers.find { it.id == milestone.chantierId }
                            if (chantier != null) {
                                val daysText = when (diffDays) {
                                    0 -> "aujourd'hui"
                                    1 -> "demain"
                                    else -> "dans $diffDays jours"
                                }
                                alerts.add(
                                    PriorityAlert(
                                        text = "Jalon '${milestone.name}' du chantier ${chantier.name} approche ($daysText).",
                                        priority = 2,
                                        sourcePageIndex = 15,
                                        alertId = "milestone_near_${milestone.id}"
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore parsing issues
                }
            }
        }

        // 3. Tension pelvienne signalée 2 semaines consécutives
        if (pelvicChecks.size >= 2) {
            val sorted = pelvicChecks.sortedByDescending { it.weekStartDate }
            if (sorted[0].tensionReported && sorted[1].tensionReported) {
                alerts.add(
                    PriorityAlert(
                        text = "Tension pelvienne signalée plusieurs semaines de suite — réduis temporairement l'intensité des contractions (moins de répétitions, tenues plus courtes) et privilégie le relâchement. Si la gêne persiste, consulte un kinésithérapeute pelvien.",
                        priority = 3,
                        sourcePageIndex = 5,
                        alertId = "pelvic_tension_consecutive"
                    )
                )
            }
        }

        // 4. Cycle adaptogène à 56 jours
        val ashwaStreak = viewModel.calculateConsecutiveDaysTaken("ashwagandha", supplementLogs)
        if (ashwaStreak >= 56) {
            alerts.add(
                PriorityAlert(
                    text = "Ça fait 8 semaines de prise continue de Ashwagandha. Une pause de 2-4 semaines est recommandée pour préserver l'efficacité.",
                    priority = 4,
                    sourcePageIndex = 3,
                    alertId = "adaptogen_cycle_ashwagandha"
                )
            )
        }
        val tongkatStreak = viewModel.calculateConsecutiveDaysTaken("tongkatAli", supplementLogs)
        if (tongkatStreak >= 56) {
            alerts.add(
                PriorityAlert(
                    text = "Ça fait 8 semaines de prise continue de Tongkat Ali. Une pause de 2-4 semaines est recommandée pour préserver l'efficacité.",
                    priority = 4,
                    sourcePageIndex = 3,
                    alertId = "adaptogen_cycle_tongkatAli"
                )
            )
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // 5. Streak Kegel non completé aujourd'hui, uniquement après 20h00
        if (currentHour >= 20) {
            val startKegelDate = viewModel.getKegelProgramStartDateOrDefault()
            val (phase, _) = KegelProgramCalculator.getCurrentPhase(startKegelDate)
            val todayKegelLog = kegelLogs.find { it.date == todayStr }
            val isKegelCompleted = todayKegelLog?.done == true || (todayKegelLog != null && when (phase.sessionsPerDay) {
                3 -> todayKegelLog.morningDone && todayKegelLog.middayDone && todayKegelLog.eveningDone
                2 -> todayKegelLog.morningDone && todayKegelLog.eveningDone
                else -> todayKegelLog.eveningDone
            })

            if (!isKegelCompleted) {
                alerts.add(
                    PriorityAlert(
                        text = "Ta séance Kegel du jour n'est pas encore faite.",
                        priority = 5,
                        sourcePageIndex = 5,
                        alertId = "kegel_incomplete_today"
                    )
                )
            }
        }

        // 6. Complément du soir non pris, uniquement après 21h00
        if (currentHour >= 21) {
            val todaySuppLog = supplementLogs.find { it.date == todayStr }
            val missingEvening = mutableListOf<String>()
            if (todaySuppLog?.magnesium != true) missingEvening.add("Magnésium")
            if (todaySuppLog?.ashwagandha != true) missingEvening.add("Ashwagandha")
            if (todaySuppLog?.lTheanine != true) missingEvening.add("L-Théanine")
            if (todaySuppLog?.zinc != true) missingEvening.add("Zinc")

            if (missingEvening.isNotEmpty()) {
                alerts.add(
                    PriorityAlert(
                        text = "Tu n'as pas encore pris ${missingEvening.joinToString(", ")} ce soir, il n'est pas trop tard.",
                        priority = 6,
                        sourcePageIndex = 3,
                        alertId = "supplement_evening_incomplete"
                    )
                )
            }
        }

        return alerts.sortedBy { it.priority }
    }
}
