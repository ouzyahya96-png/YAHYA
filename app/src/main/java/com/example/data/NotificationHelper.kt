package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

object NotificationHelper {
    private const val CHANNEL_ID = "directeur_operations_reminders"
    private const val CHANNEL_NAME = "Rappels Opérations"
    private const val CHANNEL_DESC = "Notifications et rappels du Directeur des Opérations"
    private var isChannelCreated = false

    private fun createNotificationChannel(context: Context) {
        if (isChannelCreated) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        isChannelCreated = true
    }

    fun triggerNotification(context: Context, title: String, message: String, playSound: Boolean = true) {
        try {
            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            // Note: Since API 33, POST_NOTIFICATIONS runtime permission must be granted.
            // Check will be handled in UI, but catch exceptions here.
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

            if (playSound) {
                playDiscreetSound(context)
            }
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Permission not granted for notifications: ${e.message}")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error playing notification: ${e.message}")
        }
    }

    fun queueOrSendNotification(context: Context, title: String, message: String, timeSlot: String) {
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val digestEnabled = sharedPrefs.getBoolean("digest_mode_enabled", false)
        val soundEnabled = sharedPrefs.getBoolean("sound_enabled", true)

        if (!notificationsEnabled) return

        if (!digestEnabled) {
            triggerNotification(context, title, message, playSound = soundEnabled)
        } else {
            val currentQueue = sharedPrefs.getString("digest_queue_$timeSlot", "") ?: ""
            val newQueue = if (currentQueue.isEmpty()) message else "$currentQueue|||$message"
            sharedPrefs.edit().putString("digest_queue_$timeSlot", newQueue).apply()
            Log.d("NotificationHelper", "Queued message for $timeSlot: $message")
        }
    }

    private fun playDiscreetSound(context: Context) {
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone?.play()
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to play notification sound: ${e.message}")
        }
    }
}
