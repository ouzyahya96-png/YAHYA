package com.example.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

object SunExposureScheduler {
    private const val TAG = "SunExposureScheduler"
    private const val ALARM_HOUR = 8
    private const val REQUEST_CODE = 8000

    fun schedule(context: Context) {
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications are disabled in settings. Skipping scheduling.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, ALARM_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If scheduled time has already passed today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, SunExposureReceiver::class.java).apply {
            action = "com.example.ACTION_SUN_EXPOSURE"
            putExtra("requestCode", REQUEST_CODE)
            putExtra("hour", ALARM_HOUR)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
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
                    Log.d(TAG, "Scheduled exact alarm for 8:00 with requestCode $REQUEST_CODE")
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled fallback exact alarm for 8:00 with requestCode $REQUEST_CODE")
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled exact alarm for 8:00 with requestCode $REQUEST_CODE")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled exact alarm for 8:00 with requestCode $REQUEST_CODE")
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
            Log.e(TAG, "Error scheduling alarm for hour 8: ${e.message}")
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, SunExposureReceiver::class.java).apply {
            action = "com.example.ACTION_SUN_EXPOSURE"
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            flags
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm with requestCode $REQUEST_CODE")
        }
    }
}
