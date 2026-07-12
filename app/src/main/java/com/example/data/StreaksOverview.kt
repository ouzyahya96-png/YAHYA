package com.example.data

import com.example.ui.OperationsViewModel
import java.text.SimpleDateFormat
import java.util.*

data class StreakSummary(
    val label: String, // "Récupération", "Kegel", "Sans tabac", "Communication", "Entraînement au délai", "Soleil matinal"
    val currentCount: Int, // en jours
    val bestCount: Int, // meilleur streak historique
    val iconName: String, // name of icon or reference
    val sourcePageIndex: Int // page index for navigation
)

object StreaksOverview {
    
    // Generic consecutive-day streak calculator that respects active rest days
    fun calculateGenericStreak(
        completedDates: Set<String>,
        restSet: Set<String>,
        todayStr: String
    ): Pair<Int, Int> {
        if (completedDates.isEmpty()) return Pair(0, 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        // 1. Calculate Current Streak (counting backwards from today)
        var currentStreak = 0
        val checkCal = Calendar.getInstance()
        try {
            checkCal.time = sdf.parse(todayStr) ?: java.util.Date()
        } catch (e: Exception) {
            // fallback
        }
        
        var startCal: Calendar? = null
        var daysChecked = 0
        while (daysChecked < 365) {
            val dateStr = sdf.format(checkCal.time)
            if (dateStr in restSet) {
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
                daysChecked++
                continue
            }
            if (dateStr in completedDates) {
                startCal = Calendar.getInstance().apply { time = checkCal.time }
                break
            } else {
                // If today is not completed, check the previous non-rest day
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
                var prevDateStr = sdf.format(checkCal.time)
                while (prevDateStr in restSet && daysChecked < 365) {
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                    prevDateStr = sdf.format(checkCal.time)
                    daysChecked++
                }
                if (prevDateStr in completedDates) {
                    startCal = Calendar.getInstance().apply { time = checkCal.time }
                }
                break
            }
        }
        
        if (startCal != null) {
            while (true) {
                val dateStr = sdf.format(startCal.time)
                if (dateStr in restSet) {
                    startCal.add(Calendar.DAY_OF_YEAR, -1)
                    continue
                }
                if (dateStr in completedDates) {
                    currentStreak++
                    startCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
                if (currentStreak > 1000) break
            }
        }

        // 2. Calculate Best (Record) Streak historically
        val sortedCompleted = completedDates.filter { it <= todayStr }.sorted()
        if (sortedCompleted.isEmpty()) return Pair(currentStreak, currentStreak)
        
        val minDateStr = sortedCompleted.first()
        var bestStreak = 0
        var tempStreak = 0
        
        val iterCal = Calendar.getInstance()
        try {
            iterCal.time = sdf.parse(minDateStr) ?: java.util.Date()
        } catch (e: Exception) {
            // fallback
        }
        
        val todayTime = Calendar.getInstance().apply {
            try {
                time = sdf.parse(todayStr) ?: java.util.Date()
            } catch (e: Exception) {}
        }.timeInMillis
        
        while (iterCal.timeInMillis <= todayTime) {
            val dateStr = sdf.format(iterCal.time)
            if (dateStr in restSet) {
                iterCal.add(Calendar.DAY_OF_YEAR, 1)
                continue
            }
            if (dateStr in completedDates) {
                tempStreak++
                if (tempStreak > bestStreak) {
                    bestStreak = tempStreak
                }
            } else {
                tempStreak = 0
            }
            iterCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }
        
        return Pair(currentStreak, bestStreak)
    }

    // Tobacco-free streak calculation
    fun calculateTobaccoFreeStreak(
        cardioLogs: List<CardioHealthLog>,
        todayStr: String
    ): Pair<Int, Int> {
        val minDateStr = cardioLogs.map { it.date }.minOrNull() ?: return Pair(0, 0)
        val relapseDates = cardioLogs.filter { it.tobaccoUsed }.map { it.date }.toSet()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val iterCal = Calendar.getInstance()
        try {
            iterCal.time = sdf.parse(minDateStr) ?: java.util.Date()
        } catch (e: Exception) {
            return Pair(0, 0)
        }

        val todayTime = Calendar.getInstance().apply {
            try {
                time = sdf.parse(todayStr) ?: java.util.Date()
            } catch (e: Exception) {}
        }.timeInMillis

        var bestStreak = 0
        var currentRunning = 0

        while (iterCal.timeInMillis <= todayTime) {
            val dateStr = sdf.format(iterCal.time)
            if (dateStr in relapseDates) {
                currentRunning = 0
            } else {
                currentRunning++
                if (currentRunning > bestStreak) {
                    bestStreak = currentRunning
                }
            }
            iterCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return Pair(currentRunning, bestStreak)
    }

    // Aggregator to build the list of all active streaks
    fun getAllActiveStreaks(viewModel: OperationsViewModel, todayStr: String): List<StreakSummary> {
        val restSet = viewModel.restDays.value.filter { it.active }.map { it.date }.toSet()
        val list = mutableListOf<StreakSummary>()

        // 1. Récupération (Streak sans rechute)
        val currentRecovery = viewModel.calculateCurrentStreak()
        val pastRecovery = viewModel.recoveryStreaks.value
        val bestRecovery = maxOf(currentRecovery, pastRecovery.maxOfOrNull { it.days } ?: 0)
        list.add(
            StreakSummary(
                label = "Récupération",
                currentCount = currentRecovery,
                bestCount = bestRecovery,
                iconName = "FlashOn",
                sourcePageIndex = 5
            )
        )

        // 2. Kegel Program
        val kegelLogs = viewModel.kegelLogs.value
        val kegelCompletedDates = kegelLogs.filter { 
            it.done || it.morningDone || it.middayDone || it.eveningDone || it.reverseDone 
        }.map { it.date }.toSet()
        val (currentKegel, bestKegel) = calculateGenericStreak(kegelCompletedDates, restSet, todayStr)
        list.add(
            StreakSummary(
                label = "Programme Kegel",
                currentCount = currentKegel,
                bestCount = bestKegel,
                iconName = "Favorite",
                sourcePageIndex = 5
            )
        )

        // 3. Sans tabac
        val cardioLogs = viewModel.cardioHealthLogs.value
        val (currentTobacco, bestTobacco) = calculateTobaccoFreeStreak(cardioLogs, todayStr)
        list.add(
            StreakSummary(
                label = "Sans tabac 🚭",
                currentCount = currentTobacco,
                bestCount = bestTobacco,
                iconName = "SmokeFree",
                sourcePageIndex = 6
            )
        )

        // 4. Communication
        val commLogs = viewModel.communicationPracticeLogs.value
        val commCompletedDates = commLogs.filter { it.practiced }.map { it.date }.toSet()
        val (currentComm, bestComm) = calculateGenericStreak(commCompletedDates, restSet, todayStr)
        list.add(
            StreakSummary(
                label = "Communication",
                currentCount = currentComm,
                bestCount = bestComm,
                iconName = "Forum",
                sourcePageIndex = 7
            )
        )

        // 5. Entraînement au délai
        val delayLogs = viewModel.delayTrainingLogs.value
        val delayCompletedDates = delayLogs.filter { it.completed }.map { it.date }.toSet()
        val (currentDelay, bestDelay) = calculateGenericStreak(delayCompletedDates, restSet, todayStr)
        list.add(
            StreakSummary(
                label = "Entraînement au délai",
                currentCount = currentDelay,
                bestCount = bestDelay,
                iconName = "Psychology",
                sourcePageIndex = 5
            )
        )

        // 6. Soleil matinal
        val sunLogs = viewModel.sunExposureLogs.value
        val sunCompletedDates = sunLogs.filter { it.done || it.minutesExposed > 0 }.map { it.date }.toSet()
        val (currentSun, bestSun) = calculateGenericStreak(sunCompletedDates, restSet, todayStr)
        list.add(
            StreakSummary(
                label = "Soleil matinal",
                currentCount = currentSun,
                bestCount = bestSun,
                iconName = "WbSunny",
                sourcePageIndex = 6
            )
        )

        return list
    }
}
