package com.chrisp.healthdetectwear.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.ExerciseUpdateCallback
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var exerciseClient: androidx.health.services.client.ExerciseClient? = null
    private var isExerciseActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start real-time exercise monitoring
        startRealTimeExerciseMonitoring()
    }

    private fun startRealTimeExerciseMonitoring() {
        Log.d("MainActivity", "Starting real-time exercise HR monitoring...")

        lifecycleScope.launch {
            try {
                val client = HealthServices.getClient(this@MainActivity)
                exerciseClient = client.exerciseClient

                // Check if exercise is supported
                val capabilities = exerciseClient!!.getCapabilitiesAsync().await()
                if (!capabilities.supportedExerciseTypes.contains(ExerciseType.WORKOUT)) {
                    Log.e("MainActivity", "Exercise monitoring not supported")
                    return@launch
                }

                // Configure exercise for real-time HR monitoring
                val exerciseConfig = ExerciseConfig.builder(ExerciseType.WORKOUT)
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .setIsAutoPauseAndResumeEnabled(false)
                    .setIsGpsEnabled(false) // Disable GPS for better performance
                    .build()

                // Set up callback for real-time updates
                val callback = object : ExerciseUpdateCallback {
                    override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                        val heartRates = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                        for (hr in heartRates) {
                            val bpm = hr.value
                            val timestamp = System.currentTimeMillis()
                            Log.d("HR_REALTIME", "REAL-TIME HR: ${bpm.toInt()} bpm at $timestamp")
                        }
                    }

                    override fun onLapSummaryReceived(lapSummary: androidx.health.services.client.data.ExerciseLapSummary) {
                        // Handle lap summaries if needed
                    }

                    override fun onRegistered() {
                        Log.d("MainActivity", "Exercise callback registered")
                    }

                    override fun onRegistrationFailed(throwable: Throwable) {
                        Log.e("MainActivity", "Exercise callback registration failed: ${throwable.message}")
                    }

                    override fun onAvailabilityChanged(
                        dataType: DataType<*, *>,
                        availability: androidx.health.services.client.data.Availability
                    ) {
                        Log.d("MainActivity", "Data availability changed: $dataType -> $availability")
                    }
                }

                // Start exercise with callback
                exerciseClient!!.setUpdateCallback(callback)
                exerciseClient!!.startExerciseAsync(exerciseConfig).await()

                isExerciseActive = true
                Log.d("MainActivity", "Real-time exercise HR monitoring started")

                // Keep monitoring active
                while (isExerciseActive) {
                    delay(1000) // Check every second
                    Log.d("MainActivity", "Real-time HR monitoring active...")
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Error starting real-time monitoring: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycleScope.launch {
            try {
                if (isExerciseActive && exerciseClient != null) {
                    exerciseClient!!.endExerciseAsync().await()
                    isExerciseActive = false
                    Log.d("MainActivity", "Exercise monitoring stopped")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error stopping exercise: ${e.message}")
            }
        }
    }
}

// Alternative approach using PassiveMonitoringClient with better configuration
class AlternativeMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startOptimizedPassiveMonitoring()
    }

    private fun startOptimizedPassiveMonitoring() {
        Log.d("AlternativeMain", "Starting optimized passive HR monitoring...")

        lifecycleScope.launch {
            try {
                val client = HealthServices.getClient(this@AlternativeMainActivity)
                val passiveClient = client.passiveMonitoringClient

                // More optimized config for faster updates
                val config = androidx.health.services.client.data.PassiveListenerConfig.builder()
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .setShouldUserActivityInfoBeRequested(false)
                    .build()

                passiveClient.setPassiveListenerServiceAsync(
                    OptimizedHeartRateService::class.java,
                    config
                ).await()

                Log.d("AlternativeMain", "Optimized passive HR monitoring started")

                // Keep app alive
                while (true) {
                    delay(2000) // Check every 2 seconds
                    Log.d("AlternativeMain", "Passive HR monitoring active...")
                }

            } catch (e: Exception) {
                Log.e("AlternativeMain", "Error: ${e.message}")
            }
        }
    }
}

// Optimized service for better real-time performance
class OptimizedHeartRateService : androidx.health.services.client.PassiveListenerService() {

    override fun onNewDataPointsReceived(dataPoints: androidx.health.services.client.data.DataPointContainer) {
        val currentTime = System.currentTimeMillis()
        val heartRates = dataPoints.getData(DataType.HEART_RATE_BPM)

        for (hr in heartRates) {
            val bpm = hr.value
            val dataTimestamp = hr.timeDurationFromBoot.toMillis()

            // Log both actual timestamp and current time to see the difference
            Log.d("HR_OPTIMIZED", "HR: ${bpm.toInt()} bpm | Data time: $dataTimestamp | Current time: $currentTime")

            // Calculate delay
            val delay = currentTime - dataTimestamp
            Log.d("HR_OPTIMIZED", "Data delay: ${delay}ms")
        }
    }
}