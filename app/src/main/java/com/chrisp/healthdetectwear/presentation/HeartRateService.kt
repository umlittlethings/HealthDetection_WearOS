package com.chrisp.healthdetectwear.presentation

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType

class HeartRateService : PassiveListenerService() {

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val heartRates = dataPoints.getData(DataType.HEART_RATE_BPM)
        for (hr in heartRates) {
            val bpm = hr.value
            val timestamp = System.currentTimeMillis()
            Log.d("HR_FAST", "HR: ${bpm.toInt()} bpm at $timestamp")
        }
    }
}
