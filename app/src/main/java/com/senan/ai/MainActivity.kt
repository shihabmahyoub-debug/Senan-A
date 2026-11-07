package com.senan.ai

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.content.Intent
import android.provider.Settings
import android.content.pm.PackageManager
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(
            SenanBridge(),
            "SenanAndroid"
        )

        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class SenanBridge {

        @JavascriptInterface
        fun getInstalledApps(): String {
            val apps = JSONArray()
            val manager = packageManager

            val installed = manager.getInstalledApplications(
                PackageManager.GET_META_DATA
            )

            for (app in installed) {
                val item = JSONObject()
                item.put("name", manager.getApplicationLabel(app).toString())
                item.put("packageName", app.packageName)
                apps.put(item)
            }

            return apps.toString()
        }

        @JavascriptInterface
        fun openApp(packageName: String): Boolean {
            return try {
                val intent = packageManager
                    .getLaunchIntentForPackage(packageName)

                if (intent != null) {
                    startActivity(intent)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        @JavascriptInterface
        fun openAccessibilitySettings() {
            startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
        }

        @JavascriptInterface
        fun performAction(action: String, value: String) {
            SenanAutomationService.perform(action, value)
        }

        @JavascriptInterface
        fun schedule(
            delayMinutes: Int,
            action: String,
            value: String
        ) {
            TaskScheduler.schedule(
                this@MainActivity,
                delayMinutes,
                action,
                value
            )
        }

        @JavascriptInterface
        fun showMessage(message: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
