package com.example.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class OperationsViewModel(
    private val application: Application,
    private val repository: OperationsRepository
) : AndroidViewModel(application) {

    private val sharedPrefs: SharedPreferences =
        application.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)

    // --- State Flows from Room ---
    val tasks: StateFlow<List<Task>> = repository.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gymSessions: StateFlow<List<GymSession>> = repository.gymSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplementLogs: StateFlow<List<SupplementLog>> = repository.supplementLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recoveryStreaks: StateFlow<List<RecoveryStreak>> = repository.recoveryStreaksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kegelLogs: StateFlow<List<KegelLog>> = repository.kegelLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val breathingSessions: StateFlow<List<BreathingSession>> = repository.breathingSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = repository.journalEntriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepLogs: StateFlow<List<SleepLog>> = repository.sleepLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gymExercises: StateFlow<List<GymExercise>> = repository.gymExercisesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sunExposureLogs: StateFlow<List<SunExposureLog>> = repository.sunExposureLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communicationPracticeLogs: StateFlow<List<CommunicationPracticeLog>> = repository.communicationPracticeLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val survivalStockItems: StateFlow<List<SurvivalStockItem>> = repository.survivalStockItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val urgeSurfLogs: StateFlow<List<UrgeSurfLog>> = repository.urgeSurfLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val delayTrainingLogs: StateFlow<List<DelayTrainingLog>> = repository.delayTrainingLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pelvicTensionChecks: StateFlow<List<PelvicTensionCheck>> = repository.pelvicTensionChecksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cardioHealthLogs: StateFlow<List<CardioHealthLog>> = repository.cardioHealthLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val morningErectionLogs: StateFlow<List<MorningErectionLog>> = repository.morningErectionLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restDays: StateFlow<List<RestDay>> = repository.restDaysFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyWins: StateFlow<List<DailyWin>> = repository.dailyWinsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gratitudeLogs: StateFlow<List<GratitudeLog>> = repository.gratitudeLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chantiers: StateFlow<List<Chantier>> = repository.chantiersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMilestones: StateFlow<List<ChantierMilestone>> = repository.allMilestonesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Settings / Preferences State ---
    private val _whyStatement = MutableStateFlow(sharedPrefs.getString("why_statement", "") ?: "")
    val whyStatement: StateFlow<String> = _whyStatement.asStateFlow()

    private val _goalName = MutableStateFlow(sharedPrefs.getString("goal_name", "Mon mariage") ?: "Mon mariage")
    val goalName: StateFlow<String> = _goalName.asStateFlow()

    private val _goalTargetDate = MutableStateFlow(sharedPrefs.getString("goal_target_date", "") ?: "")
    val goalTargetDate: StateFlow<String> = _goalTargetDate.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(getTodayDate())
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    private val _calendarViewMode = MutableStateFlow("Semaine")
    val calendarViewMode: StateFlow<String> = _calendarViewMode.asStateFlow()

    private val _showAlerts = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_alerts", true))
    val showAlerts: StateFlow<Boolean> = _showAlerts.asStateFlow()

    private val _showCountdown = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_countdown", true))
    val showCountdown: StateFlow<Boolean> = _showCountdown.asStateFlow()

    private val _showWeeklyPreview = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_weekly_preview", true))
    val showWeeklyPreview: StateFlow<Boolean> = _showWeeklyPreview.asStateFlow()

    private val _showAffirmation = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_affirmation", true))
    val showAffirmation: StateFlow<Boolean> = _showAffirmation.asStateFlow()

    private val _showGratitude = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_gratitude", true))
    val showGratitude: StateFlow<Boolean> = _showGratitude.asStateFlow()

    private val _showFavorites = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_favorites", true))
    val showFavorites: StateFlow<Boolean> = _showFavorites.asStateFlow()

    private val _showSecondaryGrid = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_secondary_grid", true))
    val showSecondaryGrid: StateFlow<Boolean> = _showSecondaryGrid.asStateFlow()

    private val _showGeminiAnalysis = MutableStateFlow(sharedPrefs.getBoolean("dashboard_show_gemini_analysis", true))
    val showGeminiAnalysis: StateFlow<Boolean> = _showGeminiAnalysis.asStateFlow()

    private val _householdSize = MutableStateFlow(sharedPrefs.getInt("household_size", 10))
    val householdSize: StateFlow<Int> = _householdSize.asStateFlow()

    private val _kegelProgramStartDate = MutableStateFlow(sharedPrefs.getString("kegel_program_start_date", "") ?: "")
    val kegelProgramStartDate: StateFlow<String> = _kegelProgramStartDate.asStateFlow()

    private val _geminiApiKey = MutableStateFlow<String>(sharedPrefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(sharedPrefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(sharedPrefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _digestModeEnabled = MutableStateFlow(sharedPrefs.getBoolean("digest_mode_enabled", false))
    val digestModeEnabled: StateFlow<Boolean> = _digestModeEnabled.asStateFlow()

    private val _currentStreakStart = MutableStateFlow<String>(sharedPrefs.getString("current_streak_start", "") ?: "")
    val currentStreakStart: StateFlow<String> = _currentStreakStart.asStateFlow()

    // Cached Gemini Analysis
    private val _geminiAnalysis = MutableStateFlow<String>(sharedPrefs.getString("gemini_analysis", "") ?: "")
    val geminiAnalysis: StateFlow<String> = _geminiAnalysis.asStateFlow()

    private val _isLoadingAnalysis = MutableStateFlow(false)
    val isLoadingAnalysis: StateFlow<Boolean> = _isLoadingAnalysis.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

    private val _geminiAnalysisDate = MutableStateFlow<String>(sharedPrefs.getString("gemini_analysis_date", "") ?: "")
    val geminiAnalysisDate: StateFlow<String> = _geminiAnalysisDate.asStateFlow()

    private val _isAnalysisOffline = MutableStateFlow<Boolean>(false)
    val isAnalysisOffline: StateFlow<Boolean> = _isAnalysisOffline.asStateFlow()

    // Daily affirmation state
    private val _dailyAffirmation = MutableStateFlow<String>("")
    val dailyAffirmation: StateFlow<String> = _dailyAffirmation.asStateFlow()

    private val _appLockEnabled = MutableStateFlow(sharedPrefs.getBoolean("app_lock_enabled", false))
    val appLockEnabled: StateFlow<Boolean> = _appLockEnabled.asStateFlow()

    private val _fallbackPinHash = MutableStateFlow<String>(sharedPrefs.getString("fallback_pin_hash", "") ?: "")
    val fallbackPinHash: StateFlow<String> = _fallbackPinHash.asStateFlow()

    fun setAppLockEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("app_lock_enabled", enabled).apply()
        _appLockEnabled.value = enabled
    }

    fun setFallbackPin(pin: String) {
        val hash = com.example.data.BiometricAuthManager.hashPin(pin)
        sharedPrefs.edit().putString("fallback_pin_hash", hash).apply()
        _fallbackPinHash.value = hash
    }

    fun clearFallbackPin() {
        sharedPrefs.edit().remove("fallback_pin_hash").apply()
        _fallbackPinHash.value = ""
    }

    // --- Active Exercise Timers States ---
    // Kegel timer states
    private val _kegelRepCount = MutableStateFlow(0)
    val kegelRepCount = _kegelRepCount.asStateFlow()

    private val _kegelMaxReps = MutableStateFlow(10)
    val kegelMaxReps = _kegelMaxReps.asStateFlow()

    private val _kegelIsContracting = MutableStateFlow(false)
    val kegelIsContracting = _kegelIsContracting.asStateFlow()

    private val _kegelSecondsLeft = MutableStateFlow(0)
    val kegelSecondsLeft = _kegelSecondsLeft.asStateFlow()

    private val _kegelIsRunning = MutableStateFlow(false)
    val kegelIsRunning = _kegelIsRunning.asStateFlow()

    private var kegelJob: Job? = null

    // Breathing timer states
    private val _breathingState = MutableStateFlow("IN") // "IN", "HOLD", "OUT"
    val breathingState = _breathingState.asStateFlow()

    private val _breathingSecondsLeft = MutableStateFlow(4)
    val breathingSecondsLeft = _breathingSecondsLeft.asStateFlow()

    private val _breathingTotalSecondsElapsed = MutableStateFlow(0)
    val breathingTotalSecondsElapsed = _breathingTotalSecondsElapsed.asStateFlow()

    private val _breathingIsRunning = MutableStateFlow(false)
    val breathingIsRunning = _breathingIsRunning.asStateFlow()

    private var breathingJob: Job? = null

    // Active tab for RecoveryPage (hoisted to viewmodel for deep linking/navigation)
    private val _recoveryActiveTab = MutableStateFlow("Streak")
    val recoveryActiveTab = _recoveryActiveTab.asStateFlow()

    fun setRecoveryActiveTab(tab: String) {
        _recoveryActiveTab.value = tab
    }

    init {
        // Initialize streak start date if empty
        if (_currentStreakStart.value.isNullOrEmpty()) {
            val today = getTodayDate()
            sharedPrefs.edit().putString("current_streak_start", today).apply()
            _currentStreakStart.value = today
        }
        loadDailyAffirmation()

        viewModelScope.launch {
            supplementLogs.collect { logs ->
                if (logs.isNotEmpty()) {
                    val ashwaStreak = calculateConsecutiveDaysTaken("ashwagandha", logs)
                    checkAndNotifyAdaptogenCycle("ashwagandha", ashwaStreak)
                    
                    val tongkatStreak = calculateConsecutiveDaysTaken("tongkatAli", logs)
                    checkAndNotifyAdaptogenCycle("tongkatAli", tongkatStreak)
                }
            }
        }
    }

    private fun loadDailyAffirmation() {
        val today = getTodayDate()
        val savedDate = sharedPrefs.getString("daily_affirmation_date", "")
        val savedAffirmation = sharedPrefs.getString("daily_affirmation", "")

        if (savedDate == today && !savedAffirmation.isNullOrEmpty()) {
            _dailyAffirmation.value = savedAffirmation
        } else {
            val newAffirmation = com.example.data.AffirmationsData.getRandomAffirmation()
            sharedPrefs.edit()
                .putString("daily_affirmation_date", today)
                .putString("daily_affirmation", newAffirmation)
                .apply()
            _dailyAffirmation.value = newAffirmation
        }
    }

    // --- Helper Date functions ---
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getDayOfWeekLabel(dateStr: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val date = sdf.parse(dateStr)
            val outFormat = SimpleDateFormat("EEE", Locale.getDefault())
            outFormat.format(date).uppercase()
        } catch (e: Exception) {
            ""
        }
    }

    fun calculateCurrentStreak(): Int {
        val startStr = _currentStreakStart.value
        if (startStr.isNullOrEmpty()) return 1
        val todayStr = getTodayDate()
        val days = calculateDaysBetween(startStr, todayStr)
        val activeRestDaysCount = restDays.value.count { it.date >= startStr && it.date <= todayStr && it.active }
        val finalStreak = (days - activeRestDaysCount) + 1
        return if (finalStreak < 1) 1 else finalStreak
    }

    private fun calculateDaysBetween(startDateStr: String, endDateStr: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val start = sdf.parse(startDateStr)
            val end = sdf.parse(endDateStr)
            val diff = end.time - start.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    // --- Task Actions ---
    fun addTask(title: String, description: String, priority: String, date: String, time: String?, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                date = date,
                time = time,
                category = category,
                done = false
            )
            repository.insertTask(task)
            com.example.widget.OperationsWidget.triggerManualUpdate(application)

            // Trigger notification if scheduled for a time today
            if (time != null && date == getTodayDate() && _notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Nouvelle Tâche Planifiée",
                    "Rappel: $title est prévu pour aujourd'hui à $time.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(done = !task.done)
            repository.updateTask(updated)
            com.example.widget.OperationsWidget.triggerManualUpdate(application)
            if (updated.done && _notificationsEnabled.value) {
                // Trigger immediate simple notification/sound for motivation
                NotificationHelper.triggerNotification(
                    application,
                    "Tâche Complétée",
                    "Félicitations! Vous avez terminé: ${task.title}.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTaskById(id)
            com.example.widget.OperationsWidget.triggerManualUpdate(application)
        }
    }

    // --- GYM Actions ---
    fun addGymSession(name: String, date: String, time: String, duration: Int, muscleGroups: List<String>, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session = GymSession(
                name = name,
                date = date,
                time = time,
                durationMinutes = duration,
                muscleGroups = muscleGroups.joinToString(", "),
                notes = notes
            )
            repository.insertGymSession(session)

            if (_notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Séance GYM Enregistrée",
                    "Votre séance $name est programmée pour le $date à $time.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    fun deleteGymSession(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGymSessionById(id)
            repository.deleteExercisesForSession(id) // Also cleanup related exercises
        }
    }

    fun addGymExercise(sessionId: Long, exerciseName: String, sets: Int, reps: Int, weightKg: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousMax = gymExercises.value
                .filter { it.exerciseName.trim().equals(exerciseName.trim(), ignoreCase = true) }
                .map { it.weightKg }
                .maxOrNull() ?: 0f

            val exercise = GymExercise(sessionId = sessionId, exerciseName = exerciseName, sets = sets, reps = reps, weightKg = weightKg)
            repository.insertGymExercise(exercise)

            if (previousMax > 0f && weightKg > previousMax) {
                val notifsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
                val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)
                if (notifsEnabled) {
                    NotificationHelper.triggerNotification(
                        context = application,
                        title = "🔥 Nouveau record !",
                        message = "Félicitations ! Nouveau record pour $exerciseName : $weightKg kg !",
                        playSound = soundEnabled
                    )
                }
            }
        }
    }

    fun updateGymExercise(id: Long, sessionId: Long, exerciseName: String, sets: Int, reps: Int, weightKg: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousMax = gymExercises.value
                .filter { it.id != id && it.exerciseName.trim().equals(exerciseName.trim(), ignoreCase = true) }
                .map { it.weightKg }
                .maxOrNull() ?: 0f

            val exercise = GymExercise(id = id, sessionId = sessionId, exerciseName = exerciseName, sets = sets, reps = reps, weightKg = weightKg)
            repository.insertGymExercise(exercise)

            if (previousMax > 0f && weightKg > previousMax) {
                val notifsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
                val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)
                if (notifsEnabled) {
                    NotificationHelper.triggerNotification(
                        context = application,
                        title = "🔥 Nouveau record !",
                        message = "Félicitations ! Nouveau record pour $exerciseName : $weightKg kg !",
                        playSound = soundEnabled
                    )
                }
            }
        }
    }

    fun deleteGymExercise(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGymExerciseById(id)
        }
    }

    // --- Supplements Actions ---
    fun toggleSupplement(supplementName: String, isChecked: Boolean) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getSupplementLogByDate(today) ?: SupplementLog(date = today)
            val updatedLog = when (supplementName) {
                "creatine" -> currentLog.copy(creatine = isChecked)
                "omega3" -> currentLog.copy(omega3 = isChecked)
                "magnesium" -> currentLog.copy(magnesium = isChecked)
                "ashwagandha" -> currentLog.copy(ashwagandha = isChecked)
                "tongkatAli" -> currentLog.copy(tongkatAli = isChecked)
                "vitaminD3" -> currentLog.copy(vitaminD3 = isChecked)
                "zinc" -> currentLog.copy(zinc = isChecked)
                "lTheanine" -> currentLog.copy(lTheanine = isChecked)
                "boron" -> currentLog.copy(boron = isChecked)
                "lCitrulline" -> currentLog.copy(lCitrulline = isChecked)
                else -> currentLog
            }
            repository.insertSupplementLog(updatedLog)

            if (isChecked && _notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Complément Pris",
                    "Complément enregistré avec succès : $supplementName.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    fun calculateConsecutiveDaysTaken(supplementKey: String, logsList: List<SupplementLog> = supplementLogs.value): Int {
        val logs = logsList.associateBy { it.date }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val restSet = restDays.value.filter { it.active }.map { it.date }.toSet()
        
        var count = 0
        val startCal = Calendar.getInstance()
        
        fun isTaken(dateStr: String): Boolean {
            val log = logs[dateStr] ?: return false
            return when (supplementKey) {
                "creatine" -> log.creatine
                "omega3" -> log.omega3
                "magnesium" -> log.magnesium
                "ashwagandha" -> log.ashwagandha
                "tongkatAli" -> log.tongkatAli
                "vitaminD3" -> log.vitaminD3
                "zinc" -> log.zinc
                "lTheanine" -> log.lTheanine
                "boron" -> log.boron
                "lCitrulline" -> log.lCitrulline
                else -> false
            }
        }
        
        var checkCal = Calendar.getInstance()
        var daysChecked = 0
        while (daysChecked < 365) {
            val dateStr = sdf.format(checkCal.time)
            if (dateStr in restSet) {
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
                daysChecked++
                continue
            }
            if (isTaken(dateStr)) {
                startCal.time = checkCal.time
                break
            } else {
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
                val prevDateStr = sdf.format(checkCal.time)
                if (prevDateStr in restSet) {
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                    continue
                }
                if (!isTaken(prevDateStr)) {
                    return 0
                }
                startCal.time = checkCal.time
                break
            }
        }
        
        while (true) {
            val dateStr = sdf.format(startCal.time)
            if (dateStr in restSet) {
                startCal.add(Calendar.DAY_OF_YEAR, -1)
                continue
            }
            if (isTaken(dateStr)) {
                count++
                startCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
            if (count > 1000) break
        }
        
        return count
    }

    fun checkAndNotifyAdaptogenCycle(supplementKey: String, count: Int) {
        if (supplementKey != "ashwagandha" && supplementKey != "tongkatAli") return
        val lastNotified = sharedPrefs.getInt("${supplementKey}_last_notified_streak", 0)
        
        if (count >= 56) {
            if (lastNotified < 56) {
                val displayName = if (supplementKey == "ashwagandha") "Ashwagandha" else "Tongkat Ali"
                val title = "Cycle Adaptogènes"
                val message = "Ça fait 8 semaines de prise continue de $displayName. Une pause de 2-4 semaines est recommandée pour préserver l'efficacité."
                
                if (_notificationsEnabled.value) {
                    NotificationHelper.triggerNotification(
                        application,
                        title,
                        message,
                        playSound = _soundEnabled.value
                    )
                }
                
                sharedPrefs.edit().putInt("${supplementKey}_last_notified_streak", count).apply()
            }
        } else {
            if (lastNotified > 0) {
                sharedPrefs.edit().putInt("${supplementKey}_last_notified_streak", 0).apply()
            }
        }
    }

    // --- Sun Exposure Actions ---
    fun logSunExposure(minutes: Int) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val log = SunExposureLog(
                date = today,
                minutesExposed = minutes,
                done = minutes > 0
            )
            repository.insertSunExposureLog(log)
        }
    }

    // --- Communication Actions ---
    fun markSkillPracticed(date: String, practiced: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getCommunicationPracticeLogByDate(date)
            val skillText = existing?.skillText ?: com.example.data.CommunicationSkillsData.getSkillForDate(date)
            val newLog = com.example.data.CommunicationPracticeLog(
                date = date,
                skillText = skillText,
                practiced = practiced
            )
            repository.insertCommunicationPracticeLog(newLog)
        }
    }

    // --- Recovery Streak Actions ---
    fun resetRecoveryStreak(trigger: String? = null) {
        val today = getTodayDate()
        val start = _currentStreakStart.value
        val days = calculateCurrentStreak()

        viewModelScope.launch(Dispatchers.IO) {
            // Save past streak history if it lasted at least 1 day
            if (days > 0) {
                val currentCount = recoveryStreaks.value.size + 1
                val pastStreak = RecoveryStreak(
                    label = "Streak #$currentCount",
                    days = days,
                    startDate = start,
                    endDate = today,
                    trigger = trigger
                )
                repository.insertRecoveryStreak(pastStreak)
            }

            // Reset current start to today
            sharedPrefs.edit().putString("current_streak_start", today).apply()
            _currentStreakStart.value = today
            com.example.widget.OperationsWidget.triggerManualUpdate(application)

            if (_notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Streak réinitialisé",
                    "Nouveau départ. Concentrez-vous sur le jour 1. La persévérance fait la force.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    // --- Kegel Timer Control ---
    fun startKegelTimer(maxReps: Int, variant: String) {
        stopKegelTimer()
        _kegelMaxReps.value = maxReps
        _kegelRepCount.value = 0
        _kegelIsRunning.value = true

        val startDate = getKegelProgramStartDateOrDefault()
        val (phase, _) = KegelProgramCalculator.getCurrentPhase(startDate)

        val contractSeconds = when (variant) {
            "Rapides" -> 1
            "Longues" -> phase.slowHoldSeconds
            else -> 5 // Standard
        }
        val relaxSeconds = when (variant) {
            "Rapides" -> 1
            "Longues" -> phase.slowHoldSeconds
            else -> 5 // Standard
        }

        kegelJob = viewModelScope.launch {
            for (rep in 1..maxReps) {
                _kegelRepCount.value = rep
                
                // Contraction phase
                _kegelIsContracting.value = true
                for (s in contractSeconds downTo 1) {
                    _kegelSecondsLeft.value = s
                    delay(1000)
                }

                // Relaxation phase
                _kegelIsContracting.value = false
                for (s in relaxSeconds downTo 1) {
                    _kegelSecondsLeft.value = s
                    delay(1000)
                }
            }
            // Complete session
            completeKegelSession()
        }
    }

    fun stopKegelTimer() {
        kegelJob?.cancel()
        _kegelIsRunning.value = false
        _kegelRepCount.value = 0
    }

    private fun completeKegelSession() {
        _kegelIsRunning.value = false
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getKegelLogByDate(today) ?: KegelLog(date = today)
            val startDate = getKegelProgramStartDateOrDefault()
            val (phase, _) = KegelProgramCalculator.getCurrentPhase(startDate)
            val updatedLog = when (phase.sessionsPerDay) {
                3 -> {
                    if (!currentLog.morningDone) {
                        currentLog.copy(morningDone = true, done = true && currentLog.reverseDone)
                    } else if (!currentLog.middayDone) {
                        currentLog.copy(middayDone = true, done = (currentLog.morningDone || true || currentLog.eveningDone) && currentLog.reverseDone)
                    } else {
                        currentLog.copy(eveningDone = true, done = (currentLog.morningDone || currentLog.middayDone || true) && currentLog.reverseDone)
                    }
                }
                2 -> {
                    if (!currentLog.morningDone) {
                        currentLog.copy(morningDone = true, done = true && currentLog.reverseDone)
                    } else {
                        currentLog.copy(eveningDone = true, done = (currentLog.morningDone || true) && currentLog.reverseDone)
                    }
                }
                else -> {
                    currentLog.copy(done = true && currentLog.reverseDone, morningDone = true)
                }
            }
            repository.insertKegelLog(updatedLog)

            if (_notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Exercice Kegel Terminé",
                    "Votre routine Kegel classique du jour est validée ! N'oubliez pas le relâchement pelvien ce soir.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    fun getKegelProgramStartDateOrDefault(): String {
        var current = _kegelProgramStartDate.value
        if (current.isBlank()) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            sharedPrefs.edit().putString("kegel_program_start_date", today).apply()
            _kegelProgramStartDate.value = today
            current = today
        }
        return current
    }

    fun updateKegelProgramStartDate(dateStr: String) {
        sharedPrefs.edit().putString("kegel_program_start_date", dateStr).apply()
        _kegelProgramStartDate.value = dateStr
    }

    fun toggleKegelLogSession(date: String, sessionType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getKegelLogByDate(date) ?: KegelLog(date = date)
            val startDate = getKegelProgramStartDateOrDefault()
            val (phase, _) = KegelProgramCalculator.getCurrentPhase(startDate)
            val updatedLog = when (sessionType) {
                "morning" -> {
                    val newMorning = !currentLog.morningDone
                    val classicDone = when (phase.sessionsPerDay) {
                        3 -> newMorning || currentLog.middayDone || currentLog.eveningDone
                        2 -> newMorning || currentLog.eveningDone
                        else -> true
                    }
                    currentLog.copy(
                        morningDone = newMorning,
                        done = classicDone && currentLog.reverseDone
                    )
                }
                "midday" -> {
                    val newMidday = !currentLog.middayDone
                    val classicDone = when (phase.sessionsPerDay) {
                        3 -> currentLog.morningDone || newMidday || currentLog.eveningDone
                        2 -> true
                        else -> true
                    }
                    currentLog.copy(
                        middayDone = newMidday,
                        done = classicDone && currentLog.reverseDone
                    )
                }
                "evening" -> {
                    val newEvening = !currentLog.eveningDone
                    val classicDone = when (phase.sessionsPerDay) {
                        3 -> currentLog.morningDone || currentLog.middayDone || newEvening
                        2 -> currentLog.morningDone || newEvening
                        else -> true
                    }
                    currentLog.copy(
                        eveningDone = newEvening,
                        done = classicDone && currentLog.reverseDone
                    )
                }
                "reverse" -> {
                    val newReverse = !currentLog.reverseDone
                    val classicDone = when (phase.sessionsPerDay) {
                        3 -> currentLog.morningDone || currentLog.middayDone || currentLog.eveningDone
                        2 -> currentLog.morningDone || currentLog.eveningDone
                        else -> currentLog.morningDone || currentLog.done
                    }
                    currentLog.copy(
                        reverseDone = newReverse,
                        done = classicDone && newReverse
                    )
                }
                else -> {
                    val newDone = !currentLog.done
                    currentLog.copy(
                        done = newDone,
                        morningDone = newDone,
                        middayDone = false,
                        eveningDone = false,
                        reverseDone = newDone
                    )
                }
            }
            repository.insertKegelLog(updatedLog)
        }
    }

    fun insertUrgeSurfLog(durationMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = UrgeSurfLog(date = getTodayDate(), durationMinutes = durationMinutes)
            repository.insertUrgeSurfLog(log)
        }
    }

    fun deleteUrgeSurfLog(log: UrgeSurfLog) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUrgeSurfLog(log)
        }
    }

    fun toggleDelayTrainingLog(date: String, challengeText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = repository.getDelayTrainingLogByDate(date)
            if (log != null) {
                repository.insertDelayTrainingLog(log.copy(completed = !log.completed))
            } else {
                repository.insertDelayTrainingLog(DelayTrainingLog(date = date, challengeText = challengeText, completed = true))
            }
        }
    }

    // --- Deep Breathing Timer Control ---
    fun startBreathingTimer(durationMinutes: Int) {
        stopBreathingTimer()
        _breathingIsRunning.value = true
        _breathingTotalSecondsElapsed.value = 0

        val totalSecondsTarget = durationMinutes * 60

        breathingJob = viewModelScope.launch {
            while (_breathingTotalSecondsElapsed.value < totalSecondsTarget) {
                // Inhale phase: 4s
                _breathingState.value = "IN"
                for (s in 4 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                    _breathingTotalSecondsElapsed.value += 1
                }

                // Hold high phase (poumons pleins): 2s
                _breathingState.value = "HOLD_HIGH"
                for (s in 2 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                    _breathingTotalSecondsElapsed.value += 1
                }

                // Exhale phase: 7s
                _breathingState.value = "OUT"
                for (s in 7 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                    _breathingTotalSecondsElapsed.value += 1
                }

                // Hold low phase (poumons vides): 2s
                _breathingState.value = "HOLD_LOW"
                for (s in 2 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                    _breathingTotalSecondsElapsed.value += 1
                }
            }
            completeBreathingSession(totalSecondsTarget)
        }
    }

    fun stopBreathingTimer() {
        breathingJob?.cancel()
        _breathingIsRunning.value = false
        _breathingTotalSecondsElapsed.value = 0
    }

    private fun completeBreathingSession(durationSeconds: Int) {
        _breathingIsRunning.value = false
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val session = BreathingSession(date = today, durationSeconds = durationSeconds)
            repository.insertBreathingSession(session)

            if (_notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Session Respiration Terminée",
                    "Félicitations pour ces ${durationSeconds / 60} minutes de cohérence cardiaque.",
                    playSound = _soundEnabled.value
                )
            }
        }
    }

    // --- Journal Entry ---
    fun saveJournalEntry(text: String, stress: Int, tension: Int, motivation: Int, performanceAnxiety: Int = 0) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val entry = JournalEntry(
                date = today,
                text = text,
                stress = stress,
                tension = tension,
                motivation = motivation,
                performanceAnxiety = performanceAnxiety
            )
            repository.insertJournalEntry(entry)
        }
    }

    // --- Rest Days ---
    fun toggleRestDay(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getRestDayByDate(date)
            if (existing != null) {
                repository.insertRestDay(RestDay(date = date, active = !existing.active))
            } else {
                repository.insertRestDay(RestDay(date = date, active = true))
            }
        }
    }

    // --- Daily Wins ---
    fun saveDailyWin(date: String, winText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDailyWin(DailyWin(date = date, winText = winText))
        }
    }

    // --- Gratitude Logs ---
    fun saveGratitude(date: String, g1: String, g2: String, g3: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGratitudeLog(GratitudeLog(date = date, gratitude1 = g1, gratitude2 = g2, gratitude3 = g3))
        }
    }

    // --- Why Statement ---
    fun saveWhyStatement(text: String) {
        sharedPrefs.edit().putString("why_statement", text).apply()
        _whyStatement.value = text
    }

    fun saveGoalName(name: String) {
        sharedPrefs.edit().putString("goal_name", name).apply()
        _goalName.value = name
    }

    fun saveGoalTargetDate(dateStr: String) {
        sharedPrefs.edit().putString("goal_target_date", dateStr).apply()
        _goalTargetDate.value = dateStr
    }

    fun selectCalendarDate(dateStr: String) {
        _selectedCalendarDate.value = dateStr
    }

    fun setCalendarViewMode(mode: String) {
        _calendarViewMode.value = mode
    }

    fun setDashboardSectionVisible(sectionKey: String, visible: Boolean) {
        sharedPrefs.edit().putBoolean(sectionKey, visible).apply()
        when (sectionKey) {
            "dashboard_show_alerts" -> _showAlerts.value = visible
            "dashboard_show_countdown" -> _showCountdown.value = visible
            "dashboard_show_weekly_preview" -> _showWeeklyPreview.value = visible
            "dashboard_show_affirmation" -> _showAffirmation.value = visible
            "dashboard_show_gratitude" -> _showGratitude.value = visible
            "dashboard_show_favorites" -> _showFavorites.value = visible
            "dashboard_show_secondary_grid" -> _showSecondaryGrid.value = visible
            "dashboard_show_gemini_analysis" -> _showGeminiAnalysis.value = visible
        }
    }

    // --- Pelvic Tension Checks ---
    fun savePelvicTensionCheck(weekStartDate: String, tensionReported: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPelvicTensionCheck(PelvicTensionCheck(weekStartDate, tensionReported))
        }
    }

    // --- Cardio Health Logs ---
    fun saveCardioHealthLog(
        date: String,
        systolic: Int?,
        diastolic: Int?,
        waist: Float?,
        alcohol: Int,
        tobacco: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = CardioHealthLog(
                date = date,
                systolicBP = systolic,
                diastolicBP = diastolic,
                waistCircumferenceCm = waist,
                alcoholUnits = alcohol,
                tobaccoUsed = tobacco
            )
            repository.insertCardioHealthLog(log)
        }
    }

    // --- Morning Erection Logs ---
    fun saveMorningErectionLog(date: String, quality: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMorningErectionLog(MorningErectionLog(date, quality))
        }
    }

    // --- Sleep Actions ---
    fun addSleepLog(
        bedtime: String,
        waketime: String,
        durationHours: Float,
        quality: Int = 3,
        stretchingDone: Boolean = false,
        screensOffBeforeBed: Boolean = false
    ) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val log = SleepLog(
                date = today,
                bedtime = bedtime,
                waketime = waketime,
                durationHours = durationHours,
                quality = quality,
                stretchingDone = stretchingDone,
                screensOffBeforeBed = screensOffBeforeBed
            )
            repository.insertSleepLog(log)
        }
    }

    // --- Settings / Paramètres ---
    fun updateSettings(apiKey: String, notifsEnabled: Boolean, sound: Boolean, digestEnabled: Boolean) {
        sharedPrefs.edit()
            .putString("gemini_api_key", apiKey)
            .putBoolean("notifications_enabled", notifsEnabled)
            .putBoolean("sound_enabled", sound)
            .putBoolean("digest_mode_enabled", digestEnabled)
            .apply()

        _geminiApiKey.value = apiKey
        _notificationsEnabled.value = notifsEnabled
        _soundEnabled.value = sound
        _digestModeEnabled.value = digestEnabled

        if (notifsEnabled) {
            com.example.data.AffirmationScheduler.scheduleAll(application)
            com.example.data.WeeklyReportScheduler.schedule(application)
            com.example.data.SunExposureScheduler.schedule(application)
            com.example.data.DigestScheduler.scheduleAll(application)
            com.example.data.SupplementCheckScheduler.scheduleAll(application)
            com.example.data.CommunicationSkillScheduler.schedule(application)
        } else {
            com.example.data.AffirmationScheduler.cancelAll(application)
            com.example.data.WeeklyReportScheduler.cancel(application)
            com.example.data.SunExposureScheduler.cancel(application)
            com.example.data.DigestScheduler.cancelAll(application)
            com.example.data.SupplementCheckScheduler.cancelAll(application)
            com.example.data.CommunicationSkillScheduler.cancel(application)
        }
    }

    fun updateHouseholdSize(size: Int) {
        val safeSize = size.coerceAtLeast(1)
        sharedPrefs.edit()
            .putInt("household_size", safeSize)
            .apply()
        _householdSize.value = safeSize
    }

    fun insertSurvivalStockItem(category: String, name: String, quantity: Float, unit: String, purchaseDate: String, estimatedExpiryDate: String?, storageMethod: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = SurvivalStockItem(
                category = category,
                name = name,
                quantity = quantity,
                unit = unit,
                purchaseDate = purchaseDate,
                estimatedExpiryDate = estimatedExpiryDate,
                storageMethod = storageMethod
            )
            repository.insertSurvivalStockItem(item)
        }
    }

    fun deleteSurvivalStockItem(item: SurvivalStockItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSurvivalStockItem(item)
        }
    }

    fun fullResetData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllData()
            
            // Reset preferences
            sharedPrefs.edit().clear().apply()
            _geminiApiKey.value = ""
            _notificationsEnabled.value = true
            _soundEnabled.value = true
            _geminiAnalysis.value = ""
            
            val today = getTodayDate()
            sharedPrefs.edit().putString("current_streak_start", today).apply()
            _currentStreakStart.value = today
            com.example.widget.OperationsWidget.triggerManualUpdate(application)
        }
    }

    suspend fun exportData(): String {
        return com.example.data.DataExportManager.exportToJson(repository)
    }

    suspend fun importData(json: String): Boolean {
        val success = com.example.data.DataExportManager.importFromJson(repository, json)
        if (success) {
            com.example.widget.OperationsWidget.triggerManualUpdate(application)
        }
        return success
    }

    // --- Gemini Daily Analysis ---
    fun generateGeminiAnalysis() {
        val apiKey = _geminiApiKey.value.trim()
        if (apiKey.isEmpty()) {
            _analysisError.value = "Clé API manquante dans les paramètres."
            return
        }

        _isLoadingAnalysis.value = true
        _analysisError.value = null

        viewModelScope.launch {
            // Collect context data
            val recentSleep = sleepLogs.value.take(7)
            val avgSleep = if (recentSleep.isNotEmpty()) {
                recentSleep.map { it.durationHours }.average()
            } else {
                7.5
            }

            val recentSupps = supplementLogs.value.take(7)
            var suppTakenCount = 0
            var suppTotalCount = 0
            recentSupps.forEach { log ->
                suppTotalCount += 10
                if (log.creatine) suppTakenCount++
                if (log.omega3) suppTakenCount++
                if (log.magnesium) suppTakenCount++
                if (log.ashwagandha) suppTakenCount++
                if (log.tongkatAli) suppTakenCount++
                if (log.vitaminD3) suppTakenCount++
                if (log.zinc) suppTakenCount++
                if (log.lTheanine) suppTakenCount++
                if (log.boron) suppTakenCount++
                if (log.lCitrulline) suppTakenCount++
            }

            val gymCount = gymSessions.value.filter {
                calculateDaysBetween(it.date, getTodayDate()) <= 7
            }.size

            val currentStreak = calculateCurrentStreak()

            val kegelCount = kegelLogs.value.filter {
                calculateDaysBetween(it.date, getTodayDate()) <= 7
            }.size

            val breathingCount = breathingSessions.value.filter {
                calculateDaysBetween(it.date, getTodayDate()) <= 7
            }.size

            val recentJournal = journalEntries.value.take(7)
            val avgStress = if (recentJournal.isNotEmpty()) recentJournal.map { it.stress }.average().toInt() else 5
            val avgTension = if (recentJournal.isNotEmpty()) recentJournal.map { it.tension }.average().toInt() else 5
            val avgMotivation = if (recentJournal.isNotEmpty()) recentJournal.map { it.motivation }.average().toInt() else 5
            val avgPerfAnxiety = if (recentJournal.isNotEmpty()) recentJournal.map { it.performanceAnxiety }.average().toInt() else 0

            val recentCardio = cardioHealthLogs.value.take(7)
            val avgSystolic = if (recentCardio.any { it.systolicBP != null }) recentCardio.mapNotNull { it.systolicBP }.average().toInt() else null
            val avgDiastolic = if (recentCardio.any { it.diastolicBP != null }) recentCardio.mapNotNull { it.diastolicBP }.average().toInt() else null
            val avgAlcohol = if (recentCardio.isNotEmpty()) recentCardio.map { it.alcoholUnits }.average() else 0.0
            val smokeFreeDays = 7 - recentCardio.count { it.tobaccoUsed }

            val recentErections = morningErectionLogs.value.take(7)
            val erectionsYes = recentErections.count { it.quality == "Oui" }
            val erectionsPartial = recentErections.count { it.quality == "Partielle" }
            val erectionsNo = recentErections.count { it.quality == "Non" }

            val recentTasks = tasks.value
            val tasksCompleted = recentTasks.filter { it.done }.size
            val tasksTotal = recentTasks.size

            val prompt = """
            Vous êtes le "Directeur des Opérations", un coach de vie clinique, direct, pragmatique et scientifique. Votre rôle est d'analyser les données de performance de la semaine de l'utilisateur et de lui donner un résumé clair ainsi qu'une recommandation concrète pour aujourd'hui.
            
            Voici ses données de la semaine :
            - Sommeil (moyenne de sommeil) : ${String.format("%.1f", avgSleep)} heures par nuit
            - Compléments pris : $suppTakenCount/$suppTotalCount prises enregistrées
            - Séances de sport (GYM) : $gymCount séances effectuées
            - Récupération (Streak sans rechute) : Jour $currentStreak sur 180
            - Exercices Kegel effectués : $kegelCount fois
            - Sessions de Respiration : $breathingCount sessions
            - Journal - Stress moyen : $avgStress/10, Tension moyenne : $avgTension/10, Motivation moyenne : $avgMotivation/10, Anxiété de performance sexuelle moyenne : $avgPerfAnxiety/10
            - Santé Cardiovasculaire : Tension moyenne : ${if (avgSystolic != null && avgDiastolic != null) "$avgSystolic/$avgDiastolic" else "Non mesurée"}, Alcool moyen : ${String.format("%.1f", avgAlcohol)} unités/jour, Jours sans tabac : $smokeFreeDays/7
            - Érections matinales (sur les 7 derniers jours) : $erectionsYes Oui, $erectionsPartial Partielles, $erectionsNo Non
            - Tâches accomplies : $tasksCompleted/$tasksTotal tâches de la semaine
            
            Veuillez générer exactement :
            1. Une synthèse directe de 2 à 3 phrases sur son état actuel (ce qui va bien, ce qui doit être corrigé), sur un ton clinique, rigoureux, sans fioritures et motivant.
            2. Une recommandation concrète, actionnable et prioritaire pour aujourd'hui.
            
            Répondez exclusivement en français. Soyez précis, ferme et direct.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    GeminiClient.api.generateContent(apiKey, request)
                }
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!textResponse.isNullOrEmpty()) {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", java.util.Locale.getDefault())
                    val currentDateStr = sdf.format(java.util.Date())
                    _geminiAnalysis.value = textResponse
                    _geminiAnalysisDate.value = currentDateStr
                    _isAnalysisOffline.value = false
                    _analysisError.value = null
                    sharedPrefs.edit()
                        .putString("gemini_analysis", textResponse)
                        .putString("gemini_analysis_date", currentDateStr)
                        .apply()
                } else {
                    _analysisError.value = "Réponse vide de Gemini."
                }
            } catch (e: Exception) {
                Log.e("OperationsViewModel", "Gemini call failed", e)
                val cached = sharedPrefs.getString("gemini_analysis", null)
                val cachedDate = sharedPrefs.getString("gemini_analysis_date", "") ?: ""
                if (!cached.isNullOrEmpty()) {
                    _geminiAnalysis.value = cached
                    _geminiAnalysisDate.value = cachedDate
                    _isAnalysisOffline.value = true
                    _analysisError.value = null
                } else {
                    _geminiAnalysis.value = ""
                    _geminiAnalysisDate.value = ""
                    _isAnalysisOffline.value = false
                    _analysisError.value = "OFFLINE_NO_CACHE"
                }
            } finally {
                _isLoadingAnalysis.value = false
            }
        }
    }

    // --- Chantier Actions ---
    fun addChantier(
        name: String,
        location: String,
        startDate: String,
        targetEndDate: String,
        progressPercent: Int,
        budgetTotal: Float,
        budgetSpent: Float,
        status: String,
        notes: String,
        onComplete: ((Long) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val chantier = Chantier(
                name = name,
                location = location,
                startDate = startDate,
                targetEndDate = targetEndDate,
                progressPercent = progressPercent,
                budgetTotal = budgetTotal,
                budgetSpent = budgetSpent,
                status = status,
                notes = notes
            )
            val newId = repository.insertChantier(chantier)
            withContext(Dispatchers.Main) {
                onComplete?.invoke(newId)
            }
        }
    }

    fun updateChantier(chantier: Chantier) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChantier(chantier)
        }
    }

    fun deleteChantierById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteChantierById(id)
        }
    }

    // Milestones
    fun getMilestonesForChantierFlow(chantierId: Long): Flow<List<ChantierMilestone>> = repository.getMilestonesForChantierFlow(chantierId)

    fun addMilestone(chantierId: Long, name: String, targetDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val milestone = ChantierMilestone(
                chantierId = chantierId,
                name = name,
                targetDate = targetDate,
                completed = false
            )
            repository.insertMilestone(milestone)
        }
    }

    fun updateMilestone(milestone: ChantierMilestone) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMilestone(milestone)
        }
    }

    fun deleteMilestoneById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMilestoneById(id)
        }
    }

    // Incidents
    fun getIncidentsForChantierFlow(chantierId: Long): Flow<List<ChantierIncident>> = repository.getIncidentsForChantierFlow(chantierId)

    fun addIncident(chantierId: Long, date: String, description: String, severity: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val incident = ChantierIncident(
                chantierId = chantierId,
                date = date,
                description = description,
                severity = severity
            )
            repository.insertIncident(incident)
        }
    }

    fun deleteIncidentById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIncidentById(id)
        }
    }

    // Helper functions for Ritual Page toggling
    fun toggleStretchingDone(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getSleepLogByDate(date) ?: SleepLog(
                date = date,
                bedtime = "23:00",
                waketime = "07:00",
                durationHours = 8.0f
            )
            val updatedLog = currentLog.copy(stretchingDone = !currentLog.stretchingDone)
            repository.insertSleepLog(updatedLog)
        }
    }

    fun toggleScreensOffBeforeBed(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getSleepLogByDate(date) ?: SleepLog(
                date = date,
                bedtime = "23:00",
                waketime = "07:00",
                durationHours = 8.0f
            )
            val updatedLog = currentLog.copy(screensOffBeforeBed = !currentLog.screensOffBeforeBed)
            repository.insertSleepLog(updatedLog)
        }
    }

    fun toggleTobaccoUsed(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = repository.getCardioHealthLogByDate(date) ?: CardioHealthLog(date = date)
            val updatedLog = currentLog.copy(tobaccoUsed = !currentLog.tobaccoUsed)
            repository.insertCardioHealthLog(updatedLog)
        }
    }

    fun toggleBreathingDone(date: String, durationSeconds: Int = 300) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = breathingSessions.value.filter { it.date == date }
            if (sessions.isNotEmpty()) {
                repository.deleteBreathingSessionsByDate(date)
            } else {
                val session = BreathingSession(date = date, durationSeconds = durationSeconds)
                repository.insertBreathingSession(session)
            }
        }
    }

    fun deleteGymSessionByDateAndType(date: String, isCardio: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessions = gymSessions.value.filter { it.date == date }
            val targetSession = sessions.find { 
                val matchesCardio = it.muscleGroups.contains("Cardio", ignoreCase = true)
                if (isCardio) matchesCardio else !matchesCardio
            }
            if (targetSession != null) {
                repository.deleteGymSessionById(targetSession.id)
            }
        }
    }
}

class OperationsViewModelFactory(
    private val application: Application,
    private val repository: OperationsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OperationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OperationsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
