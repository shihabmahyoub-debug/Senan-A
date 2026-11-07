package com.senan.ai

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SenanAutomationService : AccessibilityService() {

    companion object {
        @Volatile
        private var instance: SenanAutomationService? = null

        fun perform(action: String, value: String) {
            Handler(Looper.getMainLooper()).post {
                instance?.executeAction(action, value)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // يستقبل أحداث التطبيقات عند تفعيل الخدمة
    }

    override fun onInterrupt() {
        // إيقاف التفاعل عند مقاطعة الخدمة
    }

    private fun executeAction(action: String, value: String) {
        when (action.lowercase()) {

            "click_text" -> {
                clickText(value)
            }

            "click_id" -> {
                clickId(value)
            }

            "back" -> {
                performGlobalAction(GLOBAL_ACTION_BACK)
            }

            "home" -> {
                performGlobalAction(GLOBAL_ACTION_HOME)
            }

            "scroll_forward" -> {
                rootInActiveWindow?.performAction(
                    AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                )
            }

            "scroll_backward" -> {
                rootInActiveWindow?.performAction(
                    AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                )
            }

            "tap" -> {
                val parts = value.split(",")
                if (parts.size == 2) {
                    val x = parts[0].trim().toFloatOrNull()
                    val y = parts[1].trim().toFloatOrNull()

                    if (x != null && y != null) {
                        tapScreen(x, y)
                    }
                }
            }
        }
    }

    private fun clickText(text: String) {
        val root = rootInActiveWindow ?: return
        val nodes = root.findAccessibilityNodeInfosByText(text)

        for (node in nodes) {
            if (clickNode(node)) return
        }
    }

    private fun clickId(id: String) {
        val root = rootInActiveWindow ?: return
        val nodes = root.findAccessibilityNodeInfosByViewId(id)

        for (node in nodes) {
            if (clickNode(node)) return
        }
    }

    private fun clickNode(node: AccessibilityNodeInfo): Boolean {
        var current: AccessibilityNodeInfo? = node

        while (current != null) {
            if (current.isClickable) {
                return current.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )
            }
            current = current.parent
        }

        return false
    }

    private fun tapScreen(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    100
                )
            )
            .build()

        dispatchGesture(gesture, null, null)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}
