package com.chrisp.healthdetectwear.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register passive listener
        lifecycleScope.launch {
            try {
                val client = HealthServices.getClient(this@MainActivity)
                val passiveClient = client.passiveMonitoringClient

                val config = PassiveListenerConfig.builder()
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .build()

                passiveClient.setPassiveListenerServiceAsync(
                    HeartRateService::class.java,
                    config
                ).await()

                Log.d("MainActivity", "Passive listener registered.")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error setting passive listener: ${e.message}")
            }
        }
    }
}