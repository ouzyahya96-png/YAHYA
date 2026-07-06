package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.OperationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OperationsWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateAllWidgets(context, appWidgetManager, appWidgetIds)
            } catch (e: Exception) {
                Log.e("OperationsWidget", "Error updating widget: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, OperationsWidget::class.java))
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    updateAllWidgets(context, appWidgetManager, appWidgetIds)
                } catch (e: Exception) {
                    Log.e("OperationsWidget", "Error onReceive update: ${e.message}", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.widget.ACTION_UPDATE_WIDGET"

        fun triggerManualUpdate(context: Context) {
            val intent = Intent(context, OperationsWidget::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }

        private suspend fun updateAllWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            val sharedPrefs = context.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val startStr = sharedPrefs.getString("current_streak_start", todayStr) ?: todayStr

            val streak = try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val start = sdf.parse(startStr)
                val end = sdf.parse(todayStr)
                val diff = end.time.minus(start.time)
                val days = (diff / (1000 * 60 * 60 * 24)).toInt()
                days + 1
            } catch (e: Exception) {
                1
            }

            val db = AppDatabase.getDatabase(context)
            val repository = OperationsRepository(db)

            val tasks = repository.tasksFlow.first()
            val gymSessions = repository.gymSessionsFlow.first()

            val remainingTasksTodayCount = tasks.count { it.date == todayStr && !it.done }

            val todayTasksWithTime = tasks.filter { it.date == todayStr && it.time != null && !it.done }
            val todayGymSessions = gymSessions.filter { it.date == todayStr }

            val currentTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            class EventHolder(val title: String, val time: String)

            val upcomingEvents = mutableListOf<EventHolder>()

            todayTasksWithTime.forEach {
                it.time?.let { time ->
                    if (time >= currentTimeStr) {
                        upcomingEvents.add(EventHolder(it.title, time))
                    }
                }
            }

            todayGymSessions.forEach {
                if (it.time >= currentTimeStr) {
                    upcomingEvents.add(EventHolder("Séance GYM", it.time))
                }
            }

            upcomingEvents.sortBy { it.time }
            val nextEvent = upcomingEvents.firstOrNull()

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_operations)

                views.setTextViewText(R.id.widget_streak, "Jour $streak / 180")

                val tasksText = when (remainingTasksTodayCount) {
                    0 -> "Aucune tâche restante"
                    1 -> "1 tâche restante"
                    else -> "$remainingTasksTodayCount tâches restantes"
                }
                views.setTextViewText(R.id.widget_tasks, tasksText)

                if (nextEvent != null) {
                    views.setTextViewText(R.id.widget_next_event, "Prochain : ${nextEvent.title} à ${nextEvent.time}")
                } else {
                    views.setTextViewText(R.id.widget_next_event, "Aucun événement à venir")
                }

                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    mainIntent,
                    pendingIntentFlags
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
