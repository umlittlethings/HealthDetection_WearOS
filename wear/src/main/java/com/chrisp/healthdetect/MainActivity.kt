package com.chrisp.healthdetect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.os.Handler
import android.os.Looper


class MainActivity : ComponentActivity() {

    private lateinit var textHR: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startHeartRateService()
        } else {
            textHR.setText(R.string.permission_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 100, 40, 40)
        }

        textHR = TextView(this).apply {
            textSize = 24f
            setText(R.string.heart_rate_default)
        }

        startButton = Button(this).apply {
            setText(R.string.start_monitoring)
            setOnClickListener {
                checkPermissionAndStart()
            }
        }

        stopButton = Button(this).apply {
            setText(R.string.stop_monitoring)
            setOnClickListener {
                stopHeartRateService()
                textHR.setText(R.string.monitoring_stopped)
            }
        }

        layout.addView(textHR)
        layout.addView(startButton)
        layout.addView(stopButton)

        setContentView(layout)
    }



    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED -> {
                startHeartRateService()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
            }
        }
    }

    private fun startHeartRateService() {
        val intent = Intent(this, HeartRateService::class.java)
        startService(intent)
        textHR.setText(R.string.monitoring_started)

        // Stop service automatically after 15 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            stopHeartRateService()
            textHR.setText(R.string.monitoring_stopped)
        }, 30_000) // 15,000 ms = 15 seconds
    }


    private fun stopHeartRateService() {
        val intent = Intent(this, HeartRateService::class.java)
        stopService(intent)
    }
}
