package com.example.data

import kotlinx.coroutines.flow.Flow

class OperationsRepository(private val db: AppDatabase) {
    val database: AppDatabase = db
    val tasksFlow: Flow<List<Task>> = db.taskDao().getAllTasksFlow()
    val gymSessionsFlow: Flow<List<GymSession>> = db.gymSessionDao().getAllGymSessionsFlow()
    val supplementLogsFlow: Flow<List<SupplementLog>> = db.supplementLogDao().getAllSupplementLogsFlow()
    val recoveryStreaksFlow: Flow<List<RecoveryStreak>> = db.recoveryStreakDao().getAllRecoveryStreaksFlow()
    val kegelLogsFlow: Flow<List<KegelLog>> = db.kegelLogDao().getAllKegelLogsFlow()
    val breathingSessionsFlow: Flow<List<BreathingSession>> = db.breathingSessionDao().getAllBreathingSessionsFlow()
    val journalEntriesFlow: Flow<List<JournalEntry>> = db.journalEntryDao().getAllJournalEntriesFlow()
    val sleepLogsFlow: Flow<List<SleepLog>> = db.sleepLogDao().getAllSleepLogsFlow()
    val gymExercisesFlow: Flow<List<GymExercise>> = db.gymExerciseDao().getAllExercisesFlow()
    val sunExposureLogsFlow: Flow<List<SunExposureLog>> = db.sunExposureLogDao().getAllSunExposureLogsFlow()
    val communicationPracticeLogsFlow: Flow<List<CommunicationPracticeLog>> = db.communicationPracticeLogDao().getAllCommunicationPracticeLogsFlow()
    val survivalStockItemsFlow: Flow<List<SurvivalStockItem>> = db.survivalStockDao().getAllSurvivalStockItemsFlow()
    val urgeSurfLogsFlow: Flow<List<UrgeSurfLog>> = db.urgeSurfLogDao().getAllUrgeSurfLogsFlow()
    val delayTrainingLogsFlow: Flow<List<DelayTrainingLog>> = db.delayTrainingLogDao().getAllDelayTrainingLogsFlow()

    fun getExercisesForSessionFlow(sessionId: Long): Flow<List<GymExercise>> = db.gymExerciseDao().getExercisesForSessionFlow(sessionId)
    suspend fun getExercisesForSession(sessionId: Long): List<GymExercise> = db.gymExerciseDao().getExercisesForSession(sessionId)
    suspend fun insertGymExercise(exercise: GymExercise) = db.gymExerciseDao().insertExercise(exercise)
    suspend fun deleteGymExerciseById(id: Long) = db.gymExerciseDao().deleteExerciseById(id)
    suspend fun deleteExercisesForSession(sessionId: Long) = db.gymExerciseDao().deleteExercisesForSession(sessionId)

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

    // Sun Exposure Logs
    suspend fun getSunExposureLogByDate(date: String): SunExposureLog? = db.sunExposureLogDao().getSunExposureLogByDate(date)
    suspend fun insertSunExposureLog(log: SunExposureLog) = db.sunExposureLogDao().insertSunExposureLog(log)

    // Communication Practice Logs
    suspend fun getCommunicationPracticeLogByDate(date: String): CommunicationPracticeLog? = db.communicationPracticeLogDao().getCommunicationPracticeLogByDate(date)
    suspend fun insertCommunicationPracticeLog(log: CommunicationPracticeLog) = db.communicationPracticeLogDao().insertCommunicationPracticeLog(log)

    // Survival Stock Items
    fun getSurvivalStockItemsByCategoryFlow(category: String): Flow<List<SurvivalStockItem>> = db.survivalStockDao().getSurvivalStockItemsByCategoryFlow(category)
    suspend fun insertSurvivalStockItem(item: SurvivalStockItem) = db.survivalStockDao().insertSurvivalStockItem(item)
    suspend fun deleteSurvivalStockItem(item: SurvivalStockItem) = db.survivalStockDao().deleteSurvivalStockItem(item)

    // Urge Surf Logs
    suspend fun insertUrgeSurfLog(log: UrgeSurfLog) = db.urgeSurfLogDao().insertUrgeSurfLog(log)
    suspend fun deleteUrgeSurfLog(log: UrgeSurfLog) = db.urgeSurfLogDao().deleteUrgeSurfLog(log)

    // Delay Training Logs
    suspend fun getDelayTrainingLogByDate(date: String): DelayTrainingLog? = db.delayTrainingLogDao().getDelayTrainingLogByDate(date)
    suspend fun insertDelayTrainingLog(log: DelayTrainingLog) = db.delayTrainingLogDao().insertDelayTrainingLog(log)

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
        db.gymExerciseDao().deleteAllExercises()
        db.sunExposureLogDao().deleteAllSunExposureLogs()
        db.communicationPracticeLogDao().deleteAllCommunicationPracticeLogs()
        db.survivalStockDao().deleteAllSurvivalStockItems()
        db.urgeSurfLogDao().deleteAllUrgeSurfLogs()
        db.delayTrainingLogDao().deleteAllDelayTrainingLogs()
    }
}
