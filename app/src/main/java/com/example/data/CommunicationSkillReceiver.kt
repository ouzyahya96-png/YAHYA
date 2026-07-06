package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class CommunicationSkillReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CommunicationSkillReceiver", "Received communication skill alarm broadcast")

        val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) return

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val skill = CommunicationSkillsData.getSkillForDate(todayStr)

        NotificationHelper.queueOrSendNotification(
            context = context,
            title = "Compétence de Communication du Jour",
            message = skill,
            timeSlot = "matin"
        )

        // Reschedule
        CommunicationSkillScheduler.schedule(context)
    }
}
