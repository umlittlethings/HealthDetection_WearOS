package com.chrisp.healthdetect

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable

class HeartRateService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var stepCounterSensor: Sensor? = null

    private var isExerciseActive = false
    private var totalHeartRate = 0f
    private var heartRateSamples = 0
    private var exerciseStartTime: Long = 0L
    private var stepsAtStart = 0
    private var currentStepCount = 0
    private var lastStepUpdateTime: Long = 0L
    private val stepUpdateInterval = 5000L

    companion object {
        const val CHANNEL_ID = "HeartRateMonitorChannel"
        const val NOTIFICATION_ID = 1
        const val TAG = "HeartRateService"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepsAtStart = getSavedStepsAtStart()


        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        startForeground(NOTIFICATION_ID, createNotification())
        Log.d(TAG, "Service created and sensors registered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_START_EXERCISE" -> startExercise()
            "ACTION_STOP_EXERCISE" -> stopExercise()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Service destroyed and sensors unregistered")
    }

    private fun saveStepsAtStart(value: Int) {
        val prefs = getSharedPreferences("health_prefs", MODE_PRIVATE)
        prefs.edit().putInt("steps_at_start", value).apply()
    }

    private fun getSavedStepsAtStart(): Int {
        val prefs = getSharedPreferences("health_prefs", MODE_PRIVATE)
        return prefs.getInt("steps_at_start", 0)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> {
                val heartRate = event.values[0]
                if (heartRate > 0) {
                    Log.d(TAG, "Heart Rate: $heartRate")
                    if (isExerciseActive) {
                        totalHeartRate += heartRate
                        heartRateSamples++
                    }
                }
                sendHeartRateToPhone(heartRate)
            }

            Sensor.TYPE_STEP_COUNTER -> {
                val now = System.currentTimeMillis()
                if (now - lastStepUpdateTime > stepUpdateInterval) {
                    currentStepCount = event.values[0].toInt()
                    lastStepUpdateTime = now
                    Log.d(TAG, "Steps: $currentStepCount")
                }
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startExercise() {
        isExerciseActive = true
        stepsAtStart = currentStepCount
        saveStepsAtStart(stepsAtStart)
        exerciseStartTime = System.currentTimeMillis()
        totalHeartRate = 0f
        heartRateSamples = 0

        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Heart rate sensor re-registered at start of exercise")
        }

        Toast.makeText(this, "Exercise started", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Exercise started")
    }

    private fun stopExercise() {
        isExerciseActive = false

        val stepsDuringExercise = currentStepCount - stepsAtStart
        val durationMinutes = (System.currentTimeMillis() - exerciseStartTime) / 1000 / 60f
        val avgHeartRate = if (heartRateSamples > 0) totalHeartRate / heartRateSamples else 0f

        val calories = (stepsDuringExercise * 0.04f) + ((avgHeartRate - 60).coerceAtLeast(0f) * durationMinutes * 0.1f)

        Toast.makeText(this, "Exercise stopped\nCalories burned: $calories", Toast.LENGTH_LONG).show()

        Log.d(TAG, "Exercise stopped")
        Log.d(TAG, "Steps: $stepsDuringExercise, Avg HR: $avgHeartRate, Duration: $durationMinutes mins, Calories: $calories kcal")

        heartRateSensor?.let {
            sensorManager.unregisterListener(this, it)
            Log.d(TAG, "Heart rate sensor unregistered after exercise")
        }

        val summary = "Calories: ${"%.2f".format(calories)}, Steps: $stepsDuringExercise, AvgHR: ${"%.2f".format(avgHeartRate)}, Duration: ${"%.1f".format(durationMinutes)}m"
        sendExerciseSummaryToPhone(summary)
    }

    private fun createNotification(): Notification {
        val channelName = "Heart Rate Monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Monitoring Heart Rate")
            .setContentText("Service is running...")
            .build()
    }

    private fun sendHeartRateToPhone(heartRate: Float) {
        val client = Wearable.getMessageClient(this)
        val heartRateStr = heartRate.toInt().toString()

        // Kirim ke semua node yang terhubung
        Thread {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(this).connectedNodes)
                for (node in nodes) {
                    client.sendMessage(node.id, "/heart-rate", heartRateStr.toByteArray())
                }
                Log.d(TAG, "Sent heart rate: $heartRateStr to phone")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send heart rate: ${e.message}", e)
            }
        }.start()
    }

    private fun sendExerciseSummaryToPhone(summary: String) {
        val client = Wearable.getMessageClient(this)

        Thread {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(this).connectedNodes)
                for (node in nodes) {
                    client.sendMessage(node.id, "/exercise-summary", summary.toByteArray())
                }
                Log.d(TAG, "Sent summary to phone: $summary")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send summary: ${e.message}", e)
            }
        }.start()
    }

}