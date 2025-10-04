package com.vishwawijekoon.habit360.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.activities.MainActivity
import com.vishwawijekoon.habit360.utils.PreferenceHelper

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all active widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.habit_widget)

            // Calculate habit completion percentage
            val habits = PreferenceHelper.getHabits(context)
            val completedHabits = habits.count { it.isCompleted }
            val percentage = if (habits.isNotEmpty()) {
                (completedHabits.toFloat() / habits.size * 100).toInt()
            } else {
                0
            }

            // Update the views
            views.setTextViewText(R.id.tvWidgetPercentage, "$percentage%")
            views.setProgressBar(R.id.pbWidgetProgress, 100, percentage, false)

            // Create an Intent to launch MainActivity when the widget is clicked
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)
            views.setOnClickPendingIntent(R.id.widget_root_view, pendingIntent) // Assuming root view has this id

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
