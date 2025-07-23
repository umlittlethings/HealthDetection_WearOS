package com.chrisp.healthdetectwear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var textHR: TextView
    private lateinit var startButton: Button

    private val TAG = "HR_Monitor"
    private val REQUEST_BODY_SENSORS = 1
    private var isMonitoring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout container
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 100, 40, 40)
        }

        textHR = TextView(this).apply {
            textSize = 24f
            text = "Heart Rate: -- bpm"
        }

        startButton = Button(this).apply {
            text = "Start Monitoring"
            setOnClickListener {
                if (!isMonitoring) {
                    checkPermissionAndStart()
                }
            }
        }

        layout.addView(textHR)
        layout.addView(startButton)

        setContentView(layout)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    private fun checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BODY_SENSORS), REQUEST_BODY_SENSORS
            )
        } else {
            initSensor()
        }
    }

    private fun initSensor() {
        if (heartRateSensor == null) {
            Log.e(TAG, "No heart rate sensor available")
            textHR.text = "Sensor HR tidak tersedia 😕"
            return
        }

        if (!isMonitoring) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
            isMonitoring = true
            Log.d(TAG, "Sensor HR mulai dipantau")
            textHR.text = "Memulai monitoring..."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BODY_SENSORS) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                initSensor()
            } else {
                Log.e(TAG, "BODY_SENSORS permission denied")
                textHR.text = "Izin BODY_SENSORS ditolak"
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values.firstOrNull()?.toInt() ?: return
            textHR.text = "Heart Rate: $bpm bpm"
            Log.d(TAG, "HR: $bpm bpm")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy changed: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isMonitoring) {
            sensorManager.unregisterListener(this)
        }
    }
}
