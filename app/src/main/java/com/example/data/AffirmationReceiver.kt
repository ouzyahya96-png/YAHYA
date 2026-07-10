package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AffirmationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AffirmationReceiver", "Received affirmation alarm broadcast")
        
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

        if (notificationsEnabled) {
            val hour = intent.getIntExtra("hour", 9)
            val affirmation = if (hour == 9) {
                AffirmationsData.getCombinedAffirmations(3)
            } else {
                AffirmationsData.getRandomAffirmation()
            }
            val timeSlot = when (hour) {
                9 -> "matin"
                15 -> "apres-midi"
                else -> "soir"
            }
            NotificationHelper.queueOrSendNotification(
                context = context,
                title = "Rappel du Directeur",
                message = affirmation,
                timeSlot = timeSlot
            )
        }

        // Reprogram the alarm for the next occurrence
        AffirmationScheduler.scheduleAll(context)
    }
}
