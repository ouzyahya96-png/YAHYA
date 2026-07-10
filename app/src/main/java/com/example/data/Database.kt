package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String, // "HAUTE", "MOYENNE", "BASSE"
    val date: String, // "YYYY-MM-DD"
    val time: String? = null, // "HH:MM" or null
    val category: String = "perso", // "perso", "travail", "santé"
    val done: Boolean = false
)

@Entity(tableName = "gym_sessions")
data class GymSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: String, // "YYYY-MM-DD"
    val time: String, // "HH:MM"
    val durationMinutes: Int,
    val muscleGroups: String, // e.g., "Push, Legs" (comma-separated list)
    val notes: String = ""
)

@Entity(tableName = "supplement_logs")
data class SupplementLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val creatine: Boolean = false,
    val omega3: Boolean = false,
    val magnesium: Boolean = false,
    val ashwagandha: Boolean = false,
    val tongkatAli: Boolean = false,
    val vitaminD3: Boolean = false,
    val zinc: Boolean = false,
    val lTheanine: Boolean = false,
    val boron: Boolean = false,
    val lCitrulline: Boolean = false
)

@Entity(tableName = "recovery_streaks")
data class RecoveryStreak(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String, // e.g., "Streak #1"
    val days: Int,
    val startDate: String, // "YYYY-MM-DD"
    val endDate: String, // "YYYY-MM-DD"
    val trigger: String? = null
)

@Entity(tableName = "kegel_logs")
data class KegelLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val done: Boolean = false,
    val morningDone: Boolean = false,
    val middayDone: Boolean = false,
    val eveningDone: Boolean = false,
    val reverseDone: Boolean = false
)

@Entity(tableName = "urge_surf_logs")
data class UrgeSurfLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val durationMinutes: Int
)

@Entity(tableName = "delay_training_logs")
data class DelayTrainingLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val challengeText: String,
    val completed: Boolean = false
)

@Entity(tableName = "breathing_sessions")
data class BreathingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val durationSeconds: Int
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val text: String,
    val stress: Int, // 1 to 10
    val tension: Int, // 1 to 10
    val motivation: Int, // 1 to 10
    val performanceAnxiety: Int = 0 // échelle 1-10, indépendante du champ "stress" existant
)

@Entity(tableName = "pelvic_tension_checks")
data class PelvicTensionCheck(
    @PrimaryKey val weekStartDate: String, // "YYYY-MM-DD"
    val tensionReported: Boolean
)

@Entity(tableName = "cardio_health_logs")
data class CardioHealthLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val systolicBP: Int? = null,
    val diastolicBP: Int? = null,
    val waistCircumferenceCm: Float? = null,
    val alcoholUnits: Int = 0,
    val tobaccoUsed: Boolean = false
)

@Entity(tableName = "morning_erection_logs")
data class MorningErectionLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val quality: String // "Oui", "Partielle", "Non"
)

@Entity(tableName = "rest_days")
data class RestDay(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val active: Boolean = true
)

@Entity(tableName = "daily_wins")
data class DailyWin(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val winText: String
)

@Entity(tableName = "gratitude_logs")
data class GratitudeLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val gratitude1: String = "",
    val gratitude2: String = "",
    val gratitude3: String = ""
)

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val bedtime: String, // "HH:MM"
    val waketime: String, // "HH:MM"
    val durationHours: Float,
    val quality: Int = 3,
    val stretchingDone: Boolean = false,
    val screensOffBeforeBed: Boolean = false
)

@Entity(tableName = "gym_exercises")
data class GymExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float
)

@Entity(tableName = "sun_exposure_logs")
data class SunExposureLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val minutesExposed: Int = 0,
    val done: Boolean = false
)

@Entity(tableName = "communication_practice_logs")
data class CommunicationPracticeLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val skillText: String,
    val practiced: Boolean = false
)

@Entity(tableName = "survival_stock_items")
data class SurvivalStockItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Eau", "Céréales", "Légumineuses", "Conserves", "Graisses", "Sucre", "Lait en poudre", "Sel", "Hygiène", "Médical"
    val name: String,
    val quantity: Float,
    val unit: String, // "kg", "L", "unités"
    val purchaseDate: String, // "YYYY-MM-DD"
    val estimatedExpiryDate: String?, // "YYYY-MM-DD" or null
    val storageMethod: String
)



// --- Room DAOs ---

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}

@Dao
interface GymSessionDao {
    @Query("SELECT * FROM gym_sessions ORDER BY date DESC, time DESC")
    fun getAllGymSessionsFlow(): Flow<List<GymSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGymSession(session: GymSession)

    @Query("DELETE FROM gym_sessions WHERE id = :id")
    suspend fun deleteGymSessionById(id: Long)

    @Query("DELETE FROM gym_sessions")
    suspend fun deleteAllGymSessions()
}

@Dao
interface SupplementLogDao {
    @Query("SELECT * FROM supplement_logs ORDER BY date DESC")
    fun getAllSupplementLogsFlow(): Flow<List<SupplementLog>>

    @Query("SELECT * FROM supplement_logs WHERE date = :date LIMIT 1")
    suspend fun getSupplementLogByDate(date: String): SupplementLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplementLog(log: SupplementLog)

    @Query("DELETE FROM supplement_logs")
    suspend fun deleteAllSupplementLogs()
}

@Dao
interface RecoveryStreakDao {
    @Query("SELECT * FROM recovery_streaks ORDER BY id DESC")
    fun getAllRecoveryStreaksFlow(): Flow<List<RecoveryStreak>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryStreak(streak: RecoveryStreak)

    @Query("DELETE FROM recovery_streaks")
    suspend fun deleteAllRecoveryStreaks()
}

@Dao
interface KegelLogDao {
    @Query("SELECT * FROM kegel_logs ORDER BY date DESC")
    fun getAllKegelLogsFlow(): Flow<List<KegelLog>>

    @Query("SELECT * FROM kegel_logs WHERE date = :date LIMIT 1")
    suspend fun getKegelLogByDate(date: String): KegelLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKegelLog(log: KegelLog)

    @Query("DELETE FROM kegel_logs")
    suspend fun deleteAllKegelLogs()
}

@Dao
interface UrgeSurfLogDao {
    @Query("SELECT * FROM urge_surf_logs ORDER BY date DESC, id DESC")
    fun getAllUrgeSurfLogsFlow(): Flow<List<UrgeSurfLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrgeSurfLog(log: UrgeSurfLog)

    @Delete
    suspend fun deleteUrgeSurfLog(log: UrgeSurfLog)

    @Query("DELETE FROM urge_surf_logs")
    suspend fun deleteAllUrgeSurfLogs()
}

@Dao
interface DelayTrainingLogDao {
    @Query("SELECT * FROM delay_training_logs ORDER BY date DESC")
    fun getAllDelayTrainingLogsFlow(): Flow<List<DelayTrainingLog>>

    @Query("SELECT * FROM delay_training_logs WHERE date = :date LIMIT 1")
    suspend fun getDelayTrainingLogByDate(date: String): DelayTrainingLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelayTrainingLog(log: DelayTrainingLog)

    @Query("DELETE FROM delay_training_logs")
    suspend fun deleteAllDelayTrainingLogs()
}

@Dao
interface BreathingSessionDao {
    @Query("SELECT * FROM breathing_sessions ORDER BY date DESC")
    fun getAllBreathingSessionsFlow(): Flow<List<BreathingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreathingSession(session: BreathingSession)

    @Query("DELETE FROM breathing_sessions")
    suspend fun deleteAllBreathingSessions()
}

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllJournalEntriesFlow(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getJournalEntryByDate(date: String): JournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllJournalEntries()
}

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun getAllSleepLogsFlow(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    suspend fun getSleepLogByDate(date: String): SleepLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLog)

    @Query("DELETE FROM sleep_logs")
    suspend fun deleteAllSleepLogs()
}

@Dao
interface GymExerciseDao {
    @Query("SELECT * FROM gym_exercises WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getExercisesForSessionFlow(sessionId: Long): Flow<List<GymExercise>>

    @Query("SELECT * FROM gym_exercises WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getExercisesForSession(sessionId: Long): List<GymExercise>

    @Query("SELECT * FROM gym_exercises ORDER BY id ASC")
    fun getAllExercisesFlow(): Flow<List<GymExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: GymExercise)

    @Query("DELETE FROM gym_exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: Long)

    @Query("DELETE FROM gym_exercises WHERE sessionId = :sessionId")
    suspend fun deleteExercisesForSession(sessionId: Long)

    @Query("DELETE FROM gym_exercises")
    suspend fun deleteAllExercises()
}

@Dao
interface SunExposureLogDao {
    @Query("SELECT * FROM sun_exposure_logs ORDER BY date DESC")
    fun getAllSunExposureLogsFlow(): Flow<List<SunExposureLog>>

    @Query("SELECT * FROM sun_exposure_logs WHERE date = :date LIMIT 1")
    suspend fun getSunExposureLogByDate(date: String): SunExposureLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunExposureLog(log: SunExposureLog)

    @Query("DELETE FROM sun_exposure_logs")
    suspend fun deleteAllSunExposureLogs()
}

@Dao
interface CommunicationPracticeLogDao {
    @Query("SELECT * FROM communication_practice_logs ORDER BY date DESC")
    fun getAllCommunicationPracticeLogsFlow(): Flow<List<CommunicationPracticeLog>>

    @Query("SELECT * FROM communication_practice_logs WHERE date = :date LIMIT 1")
    suspend fun getCommunicationPracticeLogByDate(date: String): CommunicationPracticeLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunicationPracticeLog(log: CommunicationPracticeLog)

    @Query("DELETE FROM communication_practice_logs")
    suspend fun deleteAllCommunicationPracticeLogs()
}

@Dao
interface SurvivalStockDao {
    @Query("SELECT * FROM survival_stock_items ORDER BY category ASC")
    fun getAllSurvivalStockItemsFlow(): Flow<List<SurvivalStockItem>>

    @Query("SELECT * FROM survival_stock_items WHERE category = :category ORDER BY estimatedExpiryDate ASC")
    fun getSurvivalStockItemsByCategoryFlow(category: String): Flow<List<SurvivalStockItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvivalStockItem(item: SurvivalStockItem)

    @Delete
    suspend fun deleteSurvivalStockItem(item: SurvivalStockItem)

    @Query("DELETE FROM survival_stock_items")
    suspend fun deleteAllSurvivalStockItems()
}

@Dao
interface PelvicTensionCheckDao {
    @Query("SELECT * FROM pelvic_tension_checks ORDER BY weekStartDate DESC")
    fun getAllPelvicTensionChecksFlow(): Flow<List<PelvicTensionCheck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPelvicTensionCheck(check: PelvicTensionCheck)

    @Query("DELETE FROM pelvic_tension_checks")
    suspend fun deleteAllPelvicTensionChecks()
}

@Dao
interface CardioHealthLogDao {
    @Query("SELECT * FROM cardio_health_logs ORDER BY date DESC")
    fun getAllCardioHealthLogsFlow(): Flow<List<CardioHealthLog>>

    @Query("SELECT * FROM cardio_health_logs WHERE date = :date LIMIT 1")
    suspend fun getCardioHealthLogByDate(date: String): CardioHealthLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardioHealthLog(log: CardioHealthLog)

    @Query("DELETE FROM cardio_health_logs")
    suspend fun deleteAllCardioHealthLogs()
}

@Dao
interface MorningErectionLogDao {
    @Query("SELECT * FROM morning_erection_logs ORDER BY date DESC")
    fun getAllMorningErectionLogsFlow(): Flow<List<MorningErectionLog>>

    @Query("SELECT * FROM morning_erection_logs WHERE date = :date LIMIT 1")
    suspend fun getMorningErectionLogByDate(date: String): MorningErectionLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMorningErectionLog(log: MorningErectionLog)

    @Query("DELETE FROM morning_erection_logs")
    suspend fun deleteAllMorningErectionLogs()
}

@Dao
interface RestDayDao {
    @Query("SELECT * FROM rest_days ORDER BY date DESC")
    fun getAllRestDaysFlow(): Flow<List<RestDay>>

    @Query("SELECT * FROM rest_days WHERE date = :date LIMIT 1")
    suspend fun getRestDayByDate(date: String): RestDay?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestDay(restDay: RestDay)

    @Query("DELETE FROM rest_days WHERE date = :date")
    suspend fun deleteRestDayByDate(date: String)

    @Query("DELETE FROM rest_days")
    suspend fun deleteAllRestDays()
}

@Dao
interface DailyWinDao {
    @Query("SELECT * FROM daily_wins ORDER BY date DESC")
    fun getAllDailyWinsFlow(): Flow<List<DailyWin>>

    @Query("SELECT * FROM daily_wins WHERE date = :date LIMIT 1")
    suspend fun getDailyWinByDate(date: String): DailyWin?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWin(dailyWin: DailyWin)

    @Query("DELETE FROM daily_wins")
    suspend fun deleteAllDailyWins()
}

@Dao
interface GratitudeLogDao {
    @Query("SELECT * FROM gratitude_logs ORDER BY date DESC")
    fun getAllGratitudeLogsFlow(): Flow<List<GratitudeLog>>

    @Query("SELECT * FROM gratitude_logs WHERE date = :date LIMIT 1")
    suspend fun getGratitudeLogByDate(date: String): GratitudeLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGratitudeLog(log: GratitudeLog)

    @Query("DELETE FROM gratitude_logs")
    suspend fun deleteAllGratitudeLogs()
}


// --- Room Database ---

@Database(
    entities = [
        Task::class,
        GymSession::class,
        SupplementLog::class,
        RecoveryStreak::class,
        KegelLog::class,
        BreathingSession::class,
        JournalEntry::class,
        SleepLog::class,
        GymExercise::class,
        SunExposureLog::class,
        CommunicationPracticeLog::class,
        SurvivalStockItem::class,
        UrgeSurfLog::class,
        DelayTrainingLog::class,
        PelvicTensionCheck::class,
        CardioHealthLog::class,
        MorningErectionLog::class,
        RestDay::class,
        DailyWin::class,
        GratitudeLog::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun gymSessionDao(): GymSessionDao
    abstract fun supplementLogDao(): SupplementLogDao
    abstract fun recoveryStreakDao(): RecoveryStreakDao
    abstract fun kegelLogDao(): KegelLogDao
    abstract fun breathingSessionDao(): BreathingSessionDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun sleepLogDao(): SleepLogDao
    abstract fun gymExerciseDao(): GymExerciseDao
    abstract fun sunExposureLogDao(): SunExposureLogDao
    abstract fun communicationPracticeLogDao(): CommunicationPracticeLogDao
    abstract fun survivalStockDao(): SurvivalStockDao
    abstract fun urgeSurfLogDao(): UrgeSurfLogDao
    abstract fun delayTrainingLogDao(): DelayTrainingLogDao
    abstract fun pelvicTensionCheckDao(): PelvicTensionCheckDao
    abstract fun cardioHealthLogDao(): CardioHealthLogDao
    abstract fun morningErectionLogDao(): MorningErectionLogDao
    abstract fun restDayDao(): RestDayDao
    abstract fun dailyWinDao(): DailyWinDao
    abstract fun gratitudeLogDao(): GratitudeLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "directeur_operations_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
