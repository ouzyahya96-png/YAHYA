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
            val affirmation = AffirmationsData.getRandomAffirmation()
            NotificationHelper.triggerNotification(
                context = context,
                title = "Rappel du Directeur",
                message = affirmation,
                playSound = soundEnabled
            )
        }

        // Reprogram the alarm for the next occurrence
        AffirmationScheduler.scheduleAll(context)
    }
}
