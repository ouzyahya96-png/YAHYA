package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DigestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeSlot = intent.getStringExtra("timeSlot") ?: return
        Log.d("DigestReceiver", "Received digest alarm broadcast for slot: $timeSlot")
        
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val digestEnabled = sharedPrefs.getBoolean("digest_mode_enabled", false)
        val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

        if (notificationsEnabled && digestEnabled) {
            val queue = sharedPrefs.getString("digest_queue_$timeSlot", "") ?: ""
            if (queue.isNotEmpty()) {
                val messages = queue.split("|||").filter { it.isNotEmpty() }
                if (messages.isNotEmpty()) {
                    val count = messages.size
                    val consolidatedMessage = "$count rappels : ${messages.joinToString(", ")}"
                    
                    val slotDisplay = when (timeSlot) {
                        "matin" -> "Matin"
                        "apres-midi" -> "Après-midi"
                        else -> "Soir"
                    }
                    
                    NotificationHelper.triggerNotification(
                        context = context,
                        title = "Mode Digest - Rappels du $slotDisplay",
                        message = consolidatedMessage,
                        playSound = soundEnabled
                    )
                    
                    // Clear queue
                    sharedPrefs.edit().putString("digest_queue_$timeSlot", "").apply()
                }
            }
        }
        
        // Reschedule
        DigestScheduler.scheduleAll(context)
    }
}
