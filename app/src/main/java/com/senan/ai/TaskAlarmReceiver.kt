package com.senan.ai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val action = intent.getStringExtra("action") ?: return
        val value = intent.getStringExtra("value") ?: ""

        SenanAutomationService.perform(action, value)
    }
}
