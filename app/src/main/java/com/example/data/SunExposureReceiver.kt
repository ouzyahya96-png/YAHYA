package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SunExposureReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SunExposureReceiver", "Received sun exposure alarm broadcast")
        
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

        if (notificationsEnabled) {
            NotificationHelper.queueOrSendNotification(
                context = context,
                title = "Exposition Solaire Matinale",
                message = "☀️ Moment idéal pour ta dose de lumière naturelle — 10 à 30 minutes dehors, sans lunettes de soleil, dans l'heure qui vient.",
                timeSlot = "matin"
            )
        }

        // Reprogram the alarm for the next occurrence
        SunExposureScheduler.schedule(context)
    }
}
