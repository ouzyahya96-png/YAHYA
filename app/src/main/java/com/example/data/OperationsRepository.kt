package com.example.data

import kotlinx.coroutines.flow.Flow

class OperationsRepository(private val db: AppDatabase) {
    val tasksFlow: Flow<List<Task>> = db.taskDao().getAllTasksFlow()
    val gymSessionsFlow: Flow<List<GymSession>> = db.gymSessionDao().getAllGymSessionsFlow()
    val supplementLogsFlow: Flow<List<SupplementLog>> = db.supplementLogDao().getAllSupplementLogsFlow()
    val recoveryStreaksFlow: Flow<List<RecoveryStreak>> = db.recoveryStreakDao().getAllRecoveryStreaksFlow()
    val kegelLogsFlow: Flow<List<KegelLog>> = db.kegelLogDao().getAllKegelLogsFlow()
    val breathingSessionsFlow: Flow<List<BreathingSession>> = db.breathingSessionDao().getAllBreathingSessionsFlow()
    val journalEntriesFlow: Flow<List<JournalEntry>> = db.journalEntryDao().getAllJournalEntriesFlow()
    val sleepLogsFlow: Flow<List<SleepLog>> = db.sleepLogDao().getAllSleepLogsFlow()

    // Tasks CRUD
    suspend fun insertTask(task: Task) = db.taskDao().insertTask(task)
    suspend fun updateTask(task: Task) = db.taskDao().updateTask(task)
    suspend fun deleteTaskById(id: Long) = db.taskDao().deleteTaskById(id)

    // Gym CRUD
    suspend fun insertGymSession(session: GymSession) = db.gymSessionDao().insertGymSession(session)
    suspend fun deleteGymSessionById(id: Long) = db.gymSessionDao().deleteGymSessionById(id)

    // Supplements
    suspend fun getSupplementLogByDate(date: String): SupplementLog? = db.supplementLogDao().getSupplementLogByDate(date)
    suspend fun insertSupplementLog(log: SupplementLog) = db.supplementLogDao().insertSupplementLog(log)

    // Recovery Streaks
    suspend fun insertRecoveryStreak(streak: RecoveryStreak) = db.recoveryStreakDao().insertRecoveryStreak(streak)

    // Kegel Logs
    suspend fun getKegelLogByDate(date: String): KegelLog? = db.kegelLogDao().getKegelLogByDate(date)
    suspend fun insertKegelLog(log: KegelLog) = db.kegelLogDao().insertKegelLog(log)

    // Breathing Sessions
    suspend fun insertBreathingSession(session: BreathingSession) = db.breathingSessionDao().insertBreathingSession(session)

    // Journal Entries
    suspend fun getJournalEntryByDate(date: String): JournalEntry? = db.journalEntryDao().getJournalEntryByDate(date)
    suspend fun insertJournalEntry(entry: JournalEntry) = db.journalEntryDao().insertJournalEntry(entry)

    // Sleep Logs
    suspend fun getSleepLogByDate(date: String): SleepLog? = db.sleepLogDao().getSleepLogByDate(date)
    suspend fun insertSleepLog(log: SleepLog) = db.sleepLogDao().insertSleepLog(log)

    // Clear all data (Full Reset)
    suspend fun clearAllData() {
        db.taskDao().deleteAllTasks()
        db.gymSessionDao().deleteAllGymSessions()
        db.supplementLogDao().deleteAllSupplementLogs()
        db.recoveryStreakDao().deleteAllRecoveryStreaks()
        db.kegelLogDao().deleteAllKegelLogs()
        db.breathingSessionDao().deleteAllBreathingSessions()
        db.journalEntryDao().deleteAllJournalEntries()
        db.sleepLogDao().deleteAllSleepLogs()
    }
}
