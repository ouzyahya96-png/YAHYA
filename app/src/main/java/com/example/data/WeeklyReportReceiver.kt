package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeeklyReportReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WeeklyReportReceiver", "Received weekly report alarm broadcast")

        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

        if (!notificationsEnabled) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val repository = OperationsRepository(db)

                // Get last 7 days dates
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val last7Days = (0..6).map { i ->
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -i)
                    format.format(cal.time)
                }.toSet()

                // Fetch data
                val tasks = repository.tasksFlow.first()
                val sleep = repository.sleepLogsFlow.first()
                val gym = repository.gymSessionsFlow.first()
                val kegel = repository.kegelLogsFlow.first()
                val supplements = repository.supplementLogsFlow.first()

                // Calculate stats
                val completedTasksInLast7Days = tasks.filter { it.date in last7Days && it.done }
                val sleepLogsInLast7Days = sleep.filter { it.date in last7Days }
                val avgSleep = if (sleepLogsInLast7Days.isNotEmpty()) sleepLogsInLast7Days.map { it.durationHours }.average() else 0.0
                val gymInLast7Days = gym.filter { it.date in last7Days }
                val kegelInLast7Days = kegel.filter { it.date in last7Days && it.done }
                val supplementsInLast7Days = supplements.filter { it.date in last7Days }

                val totalDaysWithSupplements = supplementsInLast7Days.count {
                    it.creatine || it.omega3 || it.magnesium || it.ashwagandha || it.tongkatAli
                }

                val reportText = buildString {
                    append("Sommeil moyen : ${"%.1f".format(avgSleep)} h/nuit\n")
                    append("Séances GYM complétées : ${gymInLast7Days.size}\n")
                    append("Exercices de Kegel : ${kegelInLast7Days.size}/7 jours validés\n")
                    append("Tâches accomplies : ${completedTasksInLast7Days.size}\n")
                    append("Compléments pris : $totalDaysWithSupplements/7 jours")
                }

                // Store report in sharedPrefs
                val history = sharedPrefs.getString("weekly_report_history", "") ?: ""
                val timestamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val newEntry = "[$timestamp]\n$reportText"
                val updatedHistory = if (history.isEmpty()) newEntry else "$newEntry\n\n===\n\n$history"

                sharedPrefs.edit()
                    .putString("last_weekly_report", reportText)
                    .putString("weekly_report_history", updatedHistory)
                    .apply()

                // Trigger Notification
                NotificationHelper.triggerNotification(
                    context = context,
                    title = "Bilan Hebdomadaire Disponible",
                    message = reportText,
                    playSound = soundEnabled
                )

                // Schedule next Sunday
                WeeklyReportScheduler.schedule(context)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
