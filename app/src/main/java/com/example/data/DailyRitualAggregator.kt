package com.example.data

import com.example.ui.OperationsViewModel

data class RitualItem(
    val label: String,
    val timeSlot: String, // "Matin", "Journée", "Soir"
    val isCompleted: Boolean,
    val sourcePageIndex: Int, // for navigation if needed
    val quickToggleAction: (() -> Unit)? = null // null if complex input is required
)

data class RitualPlan(
    val morningItems: List<RitualItem>,
    val dayItems: List<RitualItem>,
    val eveningItems: List<RitualItem>,
    val completionPercent: Int
)

object DailyRitualAggregator {
    fun buildTodayRitual(viewModel: OperationsViewModel, date: String): RitualPlan {
        // 1. Gather all raw lists from view model flows
        val allTasks = viewModel.tasks.value
        val allGymSessions = viewModel.gymSessions.value
        val allSuppLogs = viewModel.supplementLogs.value
        val allKegelLogs = viewModel.kegelLogs.value
        val allBreathingSessions = viewModel.breathingSessions.value
        val allJournalEntries = viewModel.journalEntries.value
        val allSleepLogs = viewModel.sleepLogs.value
        val allSunExposureLogs = viewModel.sunExposureLogs.value
        val allCommunicationPracticeLogs = viewModel.communicationPracticeLogs.value
        val allDelayTrainingLogs = viewModel.delayTrainingLogs.value
        val allCardioHealthLogs = viewModel.cardioHealthLogs.value
        val allMorningErectionLogs = viewModel.morningErectionLogs.value
        val allDailyWins = viewModel.dailyWins.value
        val allGratitudeLogs = viewModel.gratitudeLogs.value
        val dailyAffirmation = viewModel.dailyAffirmation.value

        // 2. Find today's specific logs
        val todaySuppLog = allSuppLogs.find { it.date == date } ?: SupplementLog(date = date)
        val todayKegelLog = allKegelLogs.find { it.date == date } ?: KegelLog(date = date)
        val todayGymSessions = allGymSessions.filter { it.date == date }
        val todaySunExposureLog = allSunExposureLogs.find { it.date == date }
        val todayCommunicationLog = allCommunicationPracticeLogs.find { it.date == date }
        val todayErectionLog = allMorningErectionLogs.find { it.date == date }
        val todayDelayLog = allDelayTrainingLogs.find { it.date == date }
        val todayGratitudeLog = allGratitudeLogs.find { it.date == date }
        val todaySleepLog = allSleepLogs.find { it.date == date }
        val todayJournalEntry = allJournalEntries.find { it.date == date }
        val todayDailyWin = allDailyWins.find { it.date == date }
        val todayCardioLog = allCardioHealthLogs.find { it.date == date }

        val todayChallenge = DelayTrainingChallenges.getChallengeForDay(date)

        // --- MATIN ---
        val morning = mutableListOf<RitualItem>()

        // Morning supplements
        morning.add(
            RitualItem(
                label = "Complément : Tongkat Ali",
                timeSlot = "Matin",
                isCompleted = todaySuppLog.tongkatAli,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("tongkatAli", !todaySuppLog.tongkatAli) }
            )
        )
        morning.add(
            RitualItem(
                label = "Complément : Bore",
                timeSlot = "Matin",
                isCompleted = todaySuppLog.boron,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("boron", !todaySuppLog.boron) }
            )
        )
        morning.add(
            RitualItem(
                label = "Complément : Vitamine D3",
                timeSlot = "Matin",
                isCompleted = todaySuppLog.vitaminD3,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("vitaminD3", !todaySuppLog.vitaminD3) }
            )
        )

        // Cardio
        val hasCardioToday = todayGymSessions.any { it.muscleGroups.contains("Cardio", ignoreCase = true) }
        morning.add(
            RitualItem(
                label = "Séance Cardio",
                timeSlot = "Matin",
                isCompleted = hasCardioToday,
                sourcePageIndex = 4,
                quickToggleAction = {
                    if (hasCardioToday) {
                        viewModel.deleteGymSessionByDateAndType(date, isCardio = true)
                    } else {
                        viewModel.addGymSession("Séance Cardio", date, "08:00", 30, listOf("Cardio"), "Enregistré via Rituel")
                    }
                }
            )
        )

        // Morning Sun
        val hasSunToday = todaySunExposureLog?.done == true || (todaySunExposureLog?.minutesExposed ?: 0) > 0
        morning.add(
            RitualItem(
                label = "Soleil matinal (15 min)",
                timeSlot = "Matin",
                isCompleted = hasSunToday,
                sourcePageIndex = 6,
                quickToggleAction = {
                    if (hasSunToday) {
                        viewModel.logSunExposure(0)
                    } else {
                        viewModel.logSunExposure(15)
                    }
                }
            )
        )

        // Morning Erection
        val hasErectionLogged = todayErectionLog != null
        morning.add(
            RitualItem(
                label = if (hasErectionLogged) "Érection matinale : ${todayErectionLog?.quality}" else "Érection matinale (log)",
                timeSlot = "Matin",
                isCompleted = hasErectionLogged,
                sourcePageIndex = 6,
                quickToggleAction = {
                    viewModel.saveMorningErectionLog(date, if (hasErectionLogged) "Non" else "Oui")
                }
            )
        )

        // Communication practice of the day
        val hasCommPracticed = todayCommunicationLog?.practiced == true
        morning.add(
            RitualItem(
                label = "Pratique Communication : ${todayCommunicationLog?.skillText ?: CommunicationSkillsData.getSkillForDate(date)}",
                timeSlot = "Matin",
                isCompleted = hasCommPracticed,
                sourcePageIndex = 7,
                quickToggleAction = {
                    viewModel.markSkillPracticed(date, !hasCommPracticed)
                }
            )
        )

        // Morning Affirmation (read-only, always completed once displayed on this checklist)
        if (dailyAffirmation.isNotBlank()) {
            morning.add(
                RitualItem(
                    label = "Affirmation : « $dailyAffirmation »",
                    timeSlot = "Matin",
                    isCompleted = true,
                    sourcePageIndex = 12,
                    quickToggleAction = null
                )
            )
        }

        // --- JOURNÉE ---
        val day = mutableListOf<RitualItem>()

        // To-Do list items with time set
        val todayScheduledTasks = allTasks.filter { it.date == date && !it.time.isNullOrBlank() }
        todayScheduledTasks.forEach { task ->
            day.add(
                RitualItem(
                    label = "Tâche : ${task.title} à ${task.time}",
                    timeSlot = "Journée",
                    isCompleted = task.done,
                    sourcePageIndex = 1,
                    quickToggleAction = { viewModel.toggleTaskDone(task) }
                )
            )
        }

        // GYM session
        val hasGymToday = todayGymSessions.any { !it.muscleGroups.contains("Cardio", ignoreCase = true) }
        day.add(
            RitualItem(
                label = "Séance Musculation / GYM",
                timeSlot = "Journée",
                isCompleted = hasGymToday,
                sourcePageIndex = 4,
                quickToggleAction = {
                    if (hasGymToday) {
                        viewModel.deleteGymSessionByDateAndType(date, isCardio = false)
                    } else {
                        viewModel.addGymSession("Séance Musculation", date, "12:00", 60, listOf("Full Body"), "Enregistré via Rituel")
                    }
                }
            )
        )

        // Delay training challenge
        val hasDelayCompleted = todayDelayLog?.completed == true
        day.add(
            RitualItem(
                label = "Entraînement au Délai : $todayChallenge",
                timeSlot = "Journée",
                isCompleted = hasDelayCompleted,
                sourcePageIndex = 5,
                quickToggleAction = {
                    viewModel.toggleDelayTrainingLog(date, todayChallenge)
                }
            )
        )

        // Gratitude (3 inputs)
        val hasGratitudeCompleted = todayGratitudeLog != null && 
                todayGratitudeLog.gratitude1.isNotBlank() && 
                todayGratitudeLog.gratitude2.isNotBlank() && 
                todayGratitudeLog.gratitude3.isNotBlank()
        day.add(
            RitualItem(
                label = "Saisie Gratitude (3 points de reconnaissance)",
                timeSlot = "Journée",
                isCompleted = hasGratitudeCompleted,
                sourcePageIndex = 12,
                quickToggleAction = null
            )
        )

        // --- SOIR ---
        val evening = mutableListOf<RitualItem>()

        // Evening supplements
        evening.add(
            RitualItem(
                label = "Complément : Magnésium",
                timeSlot = "Soir",
                isCompleted = todaySuppLog.magnesium,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("magnesium", !todaySuppLog.magnesium) }
            )
        )
        evening.add(
            RitualItem(
                label = "Complément : Ashwagandha",
                timeSlot = "Soir",
                isCompleted = todaySuppLog.ashwagandha,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("ashwagandha", !todaySuppLog.ashwagandha) }
            )
        )
        evening.add(
            RitualItem(
                label = "Complément : L-Théanine",
                timeSlot = "Soir",
                isCompleted = todaySuppLog.lTheanine,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("lTheanine", !todaySuppLog.lTheanine) }
            )
        )
        evening.add(
            RitualItem(
                label = "Complément : Zinc",
                timeSlot = "Soir",
                isCompleted = todaySuppLog.zinc,
                sourcePageIndex = 3,
                quickToggleAction = { viewModel.toggleSupplement("zinc", !todaySuppLog.zinc) }
            )
        )

        // Kegel Program
        val startKegelDate = viewModel.getKegelProgramStartDateOrDefault()
        val (phase, _) = KegelProgramCalculator.getCurrentPhase(startKegelDate)
        
        if (phase.sessionsPerDay >= 3) {
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Matin",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.morningDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "morning") }
                )
            )
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Midi",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.middayDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "midday") }
                )
            )
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Soir",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.eveningDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "evening") }
                )
            )
        } else if (phase.sessionsPerDay == 2) {
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Matin",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.morningDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "morning") }
                )
            )
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Soir",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.eveningDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "evening") }
                )
            )
        } else {
            evening.add(
                RitualItem(
                    label = "Séance Kegel : Soir",
                    timeSlot = "Soir",
                    isCompleted = todayKegelLog.eveningDone,
                    sourcePageIndex = 5,
                    quickToggleAction = { viewModel.toggleKegelLogSession(date, "evening") }
                )
            )
        }

        // Reverse Kegel
        evening.add(
            RitualItem(
                label = "Séance Reverse Kegel",
                timeSlot = "Soir",
                isCompleted = todayKegelLog.reverseDone,
                sourcePageIndex = 5,
                quickToggleAction = { viewModel.toggleKegelLogSession(date, "reverse") }
            )
        )

        // Deep Breathing
        val hasBreathingToday = allBreathingSessions.any { it.date == date }
        evening.add(
            RitualItem(
                label = "Respiration Diaphragmatique",
                timeSlot = "Soir",
                isCompleted = hasBreathingToday,
                sourcePageIndex = 8,
                quickToggleAction = { viewModel.toggleBreathingDone(date, 300) }
            )
        )

        // Stretching
        val hasStretchingToday = todaySleepLog?.stretchingDone == true
        evening.add(
            RitualItem(
                label = "Étirements / Souplesse",
                timeSlot = "Soir",
                isCompleted = hasStretchingToday,
                sourcePageIndex = 8,
                quickToggleAction = { viewModel.toggleStretchingDone(date) }
            )
        )

        // Screens off before bedtime
        val hasScreensOffToday = todaySleepLog?.screensOffBeforeBed == true
        evening.add(
            RitualItem(
                label = "Écrans coupés avant le coucher",
                timeSlot = "Soir",
                isCompleted = hasScreensOffToday,
                sourcePageIndex = 8,
                quickToggleAction = { viewModel.toggleScreensOffBeforeBed(date) }
            )
        )

        // Daily journal text + wins
        val hasJournalCompleted = todayJournalEntry != null && todayDailyWin != null && todayDailyWin.winText.isNotBlank()
        evening.add(
            RitualItem(
                label = "Journal & Victoire du jour",
                timeSlot = "Soir",
                isCompleted = hasJournalCompleted,
                sourcePageIndex = 8,
                quickToggleAction = null
            )
        )

        // Tobacco (No Tobacco Day = True if tobaccoUsed is False)
        val hasTobaccoUsed = todayCardioLog?.tobaccoUsed == true
        evening.add(
            RitualItem(
                label = "Jour sans tabac 🚭",
                timeSlot = "Soir",
                isCompleted = !hasTobaccoUsed,
                sourcePageIndex = 6,
                quickToggleAction = { viewModel.toggleTobaccoUsed(date) }
            )
        )

        // 3. Compute completion rate
        val totalItems = morning.size + day.size + evening.size
        val completedItems = morning.count { it.isCompleted } + day.count { it.isCompleted } + evening.count { it.isCompleted }
        val completionPercent = if (totalItems > 0) (completedItems * 100) / totalItems else 0

        return RitualPlan(
            morningItems = morning,
            dayItems = day,
            eveningItems = evening,
            completionPercent = completionPercent
        )
    }
}
