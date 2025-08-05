package com.chrisp.healthdetect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.chrisp.healthdetect.ui.dashboard.DashboardScreen
import com.chrisp.healthdetect.ui.theme.HealthdetectwearTheme

class MainActivity : ComponentActivity() {
//    private lateinit var heartRateText: TextView
//    private val TAG = "MainActivity"
//
//    private val heartRateReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d(TAG, "Broadcast received with action: ${intent?.action}")
//            if (intent?.action == "HEART_RATE_UPDATE") {
//                val heartRate = intent.getStringExtra("heart_rate") ?: "N/A"
//                heartRateText.text = "Heart Rate: $heartRate BPM"
//                Log.d(TAG, "Heart rate updated in UI: $heartRate")
//            }
//        }
//    }

//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        Log.d(TAG, "MainActivity onCreate started")
//
//        heartRateText = findViewById(R.id.heartRateText)
//        heartRateText.text = "Heart Rate: Waiting for data..."
//
//        // Register broadcast receiver with proper flags for API 33+
//        val filter = IntentFilter("HEART_RATE_UPDATE")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(heartRateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
//        } else {
//            registerReceiver(heartRateReceiver, filter)
//        }
//        Log.d(TAG, "Broadcast receiver registered")

        // Start the WearableListenerService
        startWearableListenerService()

    setContent {
        HealthdetectwearTheme {
            MainApp()
        }
    }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun MainApp() {
        val context = LocalContext.current

        var heartRate by remember { mutableStateOf(0) }
        var lastUpdatedTimestamp by remember { mutableStateOf(System.currentTimeMillis())}

        var username by remember { mutableStateOf("Name") }
        var oxygenLevel by remember { mutableStateOf("98") }

        DisposableEffect(context) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "HEART_RATE_UPDATE") {
                        val hrString = intent.getStringExtra("heart_rate") ?: "0"
                        heartRate = hrString.toIntOrNull() ?: 0
                        lastUpdatedTimestamp = System.currentTimeMillis() // Catat waktu data diterima
                        Log.d("MainActivityCompose", "Heart rate state updated: $heartRate")
                    }
                }
            }
            val filter = IntentFilter("HEART_RATE_UPDATE")
//            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ContextCompat.RECEIVER_NOT_EXPORTED else 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            }

            onDispose {
                context.unregisterReceiver(receiver)
            }
        }

        DashboardScreen(
            heartRate = heartRate,
            lastUpdatedTimestamp = lastUpdatedTimestamp,
            username = username,
            onUsernameChange = { newUsername -> username = newUsername },
            oxygenLevel = oxygenLevel,
            onOxygenLevelChange = { newOxygenLevel -> oxygenLevel = newOxygenLevel }
        )
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
            Log.e("MainActivityCompose", "Failed to start WearableListenerService: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity onResume - restarting service to ensure connection")
        startWearableListenerService()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        try {
//            unregisterReceiver(heartRateReceiver)
//            Log.d(TAG, "Broadcast receiver unregistered")
//        } catch (e: Exception) {
//            Log.e(TAG, "Error unregistering receiver: ${e.message}")
//        }
//    }
}