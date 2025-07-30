package com.chrisp.healthdetect

import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HeartRateService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private val TAG = "HeartRateService"
    private val CHANNEL_ID = "hr_channel"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        startForegroundService()
    }

    private fun sendHeartRateToPhone(bpm: Int) {
        serviceScope.launch {
            try {
                val nodeClient = Wearable.getNodeClient(this@HeartRateService)
                val messageClient = Wearable.getMessageClient(this@HeartRateService)

                // Get connected nodes
                val nodes = nodeClient.connectedNodes.await()
                Log.d(TAG, "Connected nodes: ${nodes.size}")

                if (nodes.isEmpty()) {
                    Log.w(TAG, "No connected nodes found")
                    return@launch
                }

                for (node in nodes) {
                    Log.d(TAG, "Sending heart rate $bpm to node: ${node.displayName}")
                    val message = bpm.toString()

                    messageClient.sendMessage(
                        node.id,
                        "/heart-rate",
                        message.toByteArray()
                    ).await()

                    Log.d(TAG, "Heart rate sent successfully to ${node.displayName}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send heart rate: ${e.message}", e)
            }
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Heart Rate Monitoring")
            .setContentText("Monitoring heart rate...")
            .setSmallIcon(R.drawable.ic_menu_info_details)
            .build()

        startForeground(1, notification)

        heartRateSensor?.let { sensor ->
            val registered = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d(TAG, "Heart rate sensor registered: $registered")
        } ?: run {
            Log.e(TAG, "Heart Rate Sensor not available")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Heart Rate Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values.firstOrNull()?.toInt() ?: return
            if (bpm > 0) { // Only send valid heart rate values
                Log.d(TAG, "Heart Rate detected: $bpm bpm")
                sendHeartRateToPhone(bpm)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}