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

class SupplementCheckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra("hour", 12)
        Log.d("SupplementCheckReceiver", "Received supplement check alarm broadcast for hour: $hour")

        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val repository = OperationsRepository(db)

                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val todayLog = repository.supplementLogsFlow.first().firstOrNull { it.date == todayStr }

                if (hour == 12) {
                    // Morning check: Tongkat Ali, Bore (boron), Vitamine D3 (vitaminD3)
                    val missingMorning = mutableListOf<String>()
                    val tongkatTaken = todayLog?.tongkatAli ?: false
                    val boronTaken = todayLog?.boron ?: false
                    val vitD3Taken = todayLog?.vitaminD3 ?: false

                    if (!tongkatTaken) missingMorning.add("Tongkat Ali")
                    if (!boronTaken) missingMorning.add("Bore")
                    if (!vitD3Taken) missingMorning.add("Vitamine D3")

                    if (missingMorning.isNotEmpty()) {
                        val message = "Tu n'as pas encore pris ${missingMorning.joinToString(", ")} ce matin, il n'est pas trop tard."
                        NotificationHelper.queueOrSendNotification(
                            context = context,
                            title = "Compléments oubliés",
                            message = message,
                            timeSlot = "matin"
                        )
                    }
                } else if (hour == 22) {
                    // Evening check: Magnésium (magnesium), Ashwagandha (ashwagandha), L-Théanine (lTheanine), Zinc (zinc)
                    val missingEvening = mutableListOf<String>()
                    val magnesiumTaken = todayLog?.magnesium ?: false
                    val ashwagandhaTaken = todayLog?.ashwagandha ?: false
                    val lTheanineTaken = todayLog?.lTheanine ?: false
                    val zincTaken = todayLog?.zinc ?: false

                    if (!magnesiumTaken) missingEvening.add("Magnésium")
                    if (!ashwagandhaTaken) missingEvening.add("Ashwagandha")
                    if (!lTheanineTaken) missingEvening.add("L-Théanine")
                    if (!zincTaken) missingEvening.add("Zinc")

                    if (missingEvening.isNotEmpty()) {
                        val message = "Tu n'as pas encore pris ${missingEvening.joinToString(", ")} ce soir, il n'est pas trop tard."
                        NotificationHelper.queueOrSendNotification(
                            context = context,
                            title = "Compléments oubliés",
                            message = message,
                            timeSlot = "soir"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("SupplementCheckReceiver", "Error checking supplements: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }

        // Reschedule
        SupplementCheckScheduler.scheduleAll(context)
    }
}
