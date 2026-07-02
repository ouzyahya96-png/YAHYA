package com.example.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

object DigestScheduler {
    private const val TAG = "DigestScheduler"
    private val SLOTS = listOf("matin", "apres-midi", "soir")
    private val PIVOT_HOURS = listOf(9, 14, 21)
    private val PIVOT_MINUTES = listOf(30, 0, 30)
    private val REQUEST_CODES = listOf(9500, 14000, 21500)

    fun scheduleAll(context: Context) {
        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val digestEnabled = sharedPrefs.getBoolean("digest_mode_enabled", false)
        
        if (!notificationsEnabled || !digestEnabled) {
            cancelAll(context)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (i in SLOTS.indices) {
            val slot = SLOTS[i]
            val hour = PIVOT_HOURS[i]
            val minute = PIVOT_MINUTES[i]
            val requestCode = REQUEST_CODES[i]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, DigestReceiver::class.java).apply {
                putExtra("timeSlot", slot)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled digest alarm for slot $slot at ${hour}h${minute}")
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException: cannot schedule exact alarm: ${e.message}")
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        for (requestCode in REQUEST_CODES) {
            val intent = Intent(context, DigestReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                flags
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
        Log.d(TAG, "Cancelled all digest alarms")
    }
}
