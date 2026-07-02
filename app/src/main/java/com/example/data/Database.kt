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
    val tongkatAli: Boolean = false
)

@Entity(tableName = "recovery_streaks")
data class RecoveryStreak(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String, // e.g., "Streak #1"
    val days: Int,
    val endDate: String // "YYYY-MM-DD"
)

@Entity(tableName = "kegel_logs")
data class KegelLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val done: Boolean = false
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
    val motivation: Int // 1 to 10
)

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val bedtime: String, // "HH:MM"
    val waketime: String, // "HH:MM"
    val durationHours: Float
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
        SleepLog::class
    ],
    version = 1,
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
