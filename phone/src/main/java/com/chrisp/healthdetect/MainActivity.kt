package com.chrisp.healthdetect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var heartRateText: TextView
    private val TAG = "MainActivity"

    private val heartRateReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast received with action: ${intent?.action}")
            if (intent?.action == "HEART_RATE_UPDATE") {
                val heartRate = intent.getStringExtra("heart_rate") ?: "N/A"
                heartRateText.text = "Heart Rate: $heartRate BPM"
                Log.d(TAG, "Heart rate updated in UI: $heartRate")
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity onCreate started")

        heartRateText = findViewById(R.id.heartRateText)
        heartRateText.text = "Heart Rate: Waiting for data..."

        // Register broadcast receiver with proper flags for API 33+
        val filter = IntentFilter("HEART_RATE_UPDATE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(heartRateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(heartRateReceiver, filter)
        }
        Log.d(TAG, "Broadcast receiver registered")

        val summaryFilter = IntentFilter("EXERCISE_SUMMARY_UPDATE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(exerciseSummaryReceiver, summaryFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(exerciseSummaryReceiver, summaryFilter)
        }

        // Start the WearableListenerService
        startWearableListenerService()
    }

    private fun startWearableListenerService() {
        try {
            val serviceIntent = Intent(this, HeartRateWearableListenerService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
                Log.d(TAG, "Started WearableListenerService as foreground service")
            } else {
                startService(serviceIntent)
                Log.d(TAG, "Started WearableListenerService as regular service")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start WearableListenerService: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity onResume - restarting service to ensure connection")
        startWearableListenerService()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(heartRateReceiver)
            Log.d(TAG, "Broadcast receiver unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver: ${e.message}")
        }
        unregisterReceiver(exerciseSummaryReceiver)
    }

    private val exerciseSummaryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "EXERCISE_SUMMARY_UPDATE") {
                val summary = intent.getStringExtra("summary") ?: "No summary"
                Log.d("MainActivity", "Summary received: $summary")
                Toast.makeText(this@MainActivity, summary, Toast.LENGTH_LONG).show()
            }
        }
    }
}