package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first

data class ExportedData(
    val tasks: List<Task> = emptyList(),
    val gymSessions: List<GymSession> = emptyList(),
    val supplementLogs: List<SupplementLog> = emptyList(),
    val recoveryStreaks: List<RecoveryStreak> = emptyList(),
    val kegelLogs: List<KegelLog> = emptyList(),
    val breathingSessions: List<BreathingSession> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val sleepLogs: List<SleepLog> = emptyList(),
    val gymExercises: List<GymExercise> = emptyList()
)

object DataExportManager {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(ExportedData::class.java).indent("  ")

    suspend fun exportToJson(repository: OperationsRepository): String {
        val tasks = repository.tasksFlow.first()
        val gymSessions = repository.gymSessionsFlow.first()
        val supplementLogs = repository.supplementLogsFlow.first()
        val recoveryStreaks = repository.recoveryStreaksFlow.first()
        val kegelLogs = repository.kegelLogsFlow.first()
        val breathingSessions = repository.breathingSessionsFlow.first()
        val journalEntries = repository.journalEntriesFlow.first()
        val sleepLogs = repository.sleepLogsFlow.first()
        val gymExercises = repository.gymExercisesFlow.first()

        val data = ExportedData(
            tasks = tasks,
            gymSessions = gymSessions,
            supplementLogs = supplementLogs,
            recoveryStreaks = recoveryStreaks,
            kegelLogs = kegelLogs,
            breathingSessions = breathingSessions,
            journalEntries = journalEntries,
            sleepLogs = sleepLogs,
            gymExercises = gymExercises
        )

        return adapter.toJson(data)
    }

    suspend fun importFromJson(repository: OperationsRepository, jsonString: String): Boolean {
        val data = try {
            adapter.fromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: return false

        // Clear all database tables first
        repository.clearAllData()

        // Bulk insert each list
        data.tasks.forEach { repository.insertTask(it) }
        data.gymSessions.forEach { repository.insertGymSession(it) }
        data.supplementLogs.forEach { repository.insertSupplementLog(it) }
        data.recoveryStreaks.forEach { repository.insertRecoveryStreak(it) }
        data.kegelLogs.forEach { repository.insertKegelLog(it) }
        data.breathingSessions.forEach { repository.insertBreathingSession(it) }
        data.journalEntries.forEach { repository.insertJournalEntry(it) }
        data.sleepLogs.forEach { repository.insertSleepLog(it) }
        data.gymExercises.forEach { repository.insertGymExercise(it) }

        return true
    }
}
