package com.senan.ai

import android.content.Context
import android.content.Intent
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.SystemClock

object TaskScheduler {

    fun schedule(
        context: Context,
        delayMinutes: Int,
        action: String,
        value: String
    ) {
        val safeDelay = delayMinutes.coerceAtLeast(1)

        val intent = Intent(
            context,
            TaskAlarmReceiver::class.java
        ).apply {
            putExtra("action", action)
            putExtra("value", value)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE)
                as AlarmManager

        val triggerTime =
            SystemClock.elapsedRealtime() +
                safeDelay * 60 * 1000L

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}
