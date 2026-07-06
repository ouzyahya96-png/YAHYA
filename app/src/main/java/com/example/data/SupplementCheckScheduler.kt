package com.example.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

object SupplementCheckScheduler {
    private const val TAG = "SupplementCheckScheduler"
    private val ALARM_HOURS = listOf(12, 22)
    private val REQUEST_CODES = listOf(12000, 22000)

    fun scheduleAll(context: Context) {
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications are disabled in settings. Skipping scheduling.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (i in ALARM_HOURS.indices) {
            val hour = ALARM_HOURS[i]
            val requestCode = REQUEST_CODES[i]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                
                // If scheduled time has already passed today, set for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, SupplementCheckReceiver::class.java).apply {
                action = "com.example.ACTION_SUPPLEMENT_CHECK"
                putExtra("requestCode", requestCode)
                putExtra("hour", hour)
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                flags
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d(TAG, "Scheduled exact alarm for $hour:00 with requestCode $requestCode")
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d(TAG, "Scheduled fallback alarm for $hour:00 with requestCode $requestCode (exact not permitted)")
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled exact alarm for $hour:00 with requestCode $requestCode")
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled exact alarm for $hour:00 with requestCode $requestCode")
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException scheduling exact alarm: ${e.message}. Falling back.")
                try {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to schedule even fallback alarm: ${ex.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling alarm for hour $hour: ${e.message}")
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (i in ALARM_HOURS.indices) {
            val requestCode = REQUEST_CODES[i]
            val intent = Intent(context, SupplementCheckReceiver::class.java).apply {
                action = "com.example.ACTION_SUPPLEMENT_CHECK"
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_NO_CREATE
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                flags
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(TAG, "Cancelled alarm with requestCode $requestCode")
            }
        }
    }
}
