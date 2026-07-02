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

    // --- Settings / Preferences State ---
    private val _geminiApiKey = MutableStateFlow<String>(sharedPrefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(sharedPrefs.getBoolean("notifications_enabled", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(sharedPrefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _currentStreakStart = MutableStateFlow<String>(sharedPrefs.getString("current_streak_start", "") ?: "")
    val currentStreakStart: StateFlow<String> = _currentStreakStart.asStateFlow()

    // Cached Gemini Analysis
    private val _geminiAnalysis = MutableStateFlow<String>(sharedPrefs.getString("gemini_analysis", "") ?: "")
    val geminiAnalysis: StateFlow<String> = _geminiAnalysis.asStateFlow()

    private val _isLoadingAnalysis = MutableStateFlow(false)
    val isLoadingAnalysis: StateFlow<Boolean> = _isLoadingAnalysis.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

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

    init {
        // Initialize streak start date if empty
        if (_currentStreakStart.value.isNullOrEmpty()) {
            val today = getTodayDate()
            sharedPrefs.edit().putString("current_streak_start", today).apply()
            _currentStreakStart.value = today
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
        return days + 1
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

    // --- Recovery Streak Actions ---
    fun resetRecoveryStreak() {
        val today = getTodayDate()
        val start = _currentStreakStart.value ?: today
        val days = calculateCurrentStreak()

        viewModelScope.launch(Dispatchers.IO) {
            // Save past streak history if it lasted at least 1 day
            if (days > 0) {
                val currentCount = recoveryStreaks.value.size + 1
                val pastStreak = RecoveryStreak(
                    label = "Streak #$currentCount",
                    days = days,
                    endDate = today
                )
                repository.insertRecoveryStreak(pastStreak)
            }

            // Reset current start to today
            sharedPrefs.edit().putString("current_streak_start", today).apply()
            _currentStreakStart.value = today

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

        val contractSeconds = when (variant) {
            "Rapides" -> 1
            "Longues" -> 10
            else -> 5 // Standard
        }
        val relaxSeconds = when (variant) {
            "Rapides" -> 1
            "Longues" -> 10
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
            val log = KegelLog(date = today, done = true)
            repository.insertKegelLog(log)

            if (_notificationsEnabled.value) {
                NotificationHelper.triggerNotification(
                    application,
                    "Exercice Kegel Terminé",
                    "Votre routine Kegel quotidienne est validée !",
                    playSound = _soundEnabled.value
                )
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

                // Hold phase: 4s
                _breathingState.value = "HOLD"
                for (s in 4 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                    _breathingTotalSecondsElapsed.value += 1
                }

                // Exhale phase: 6s
                _breathingState.value = "OUT"
                for (s in 6 downTo 1) {
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
    fun saveJournalEntry(text: String, stress: Int, tension: Int, motivation: Int) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val entry = JournalEntry(
                date = today,
                text = text,
                stress = stress,
                tension = tension,
                motivation = motivation
            )
            repository.insertJournalEntry(entry)
        }
    }

    // --- Sleep Actions ---
    fun addSleepLog(bedtime: String, waketime: String, durationHours: Float) {
        val today = getTodayDate()
        viewModelScope.launch(Dispatchers.IO) {
            val log = SleepLog(
                date = today,
                bedtime = bedtime,
                waketime = waketime,
                durationHours = durationHours
            )
            repository.insertSleepLog(log)
        }
    }

    // --- Settings / Paramètres ---
    fun updateSettings(apiKey: String, notifsEnabled: Boolean, sound: Boolean) {
        sharedPrefs.edit()
            .putString("gemini_api_key", apiKey)
            .putBoolean("notifications_enabled", notifsEnabled)
            .putBoolean("sound_enabled", sound)
            .apply()

        _geminiApiKey.value = apiKey
        _notificationsEnabled.value = notifsEnabled
        _soundEnabled.value = sound
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
        }
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
                suppTotalCount += 5
                if (log.creatine) suppTakenCount++
                if (log.omega3) suppTakenCount++
                if (log.magnesium) suppTakenCount++
                if (log.ashwagandha) suppTakenCount++
                if (log.tongkatAli) suppTakenCount++
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
            - Journal - Stress moyen : $avgStress/10, Tension moyenne : $avgTension/10, Motivation moyenne : $avgMotivation/10
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
                    _geminiAnalysis.value = textResponse
                    sharedPrefs.edit().putString("gemini_analysis", textResponse).apply()
                } else {
                    _analysisError.value = "Réponse vide de Gemini."
                }
            } catch (e: Exception) {
                Log.e("OperationsViewModel", "Gemini call failed", e)
                _analysisError.value = "Échec de l'analyse: ${e.message}"
            } finally {
                _isLoadingAnalysis.value = false
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
