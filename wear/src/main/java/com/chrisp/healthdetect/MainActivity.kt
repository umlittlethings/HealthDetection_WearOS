package com.chrisp.healthdetect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var textHR: TextView
    private lateinit var startMonitoringBtn: Button
    private lateinit var stopMonitoringBtn: Button
    private lateinit var startExerciseBtn: Button
    private lateinit var stopExerciseBtn: Button

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startHeartRateService()
        else textHR.text = "Permission denied"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val innerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 100, 40, 40)
        }

        val scrollView = ScrollView(this).apply {
            addView(innerLayout)
        }

        textHR = TextView(this).apply {
            textSize = 20f
            text = "Heart Rate Monitor"
        }

        startMonitoringBtn = Button(this).apply {
            text = "Start Monitoring"
            setOnClickListener { checkPermissionAndStart() }
        }

        stopMonitoringBtn = Button(this).apply {
            text = "Stop Monitoring"
            setOnClickListener {
                stopService(Intent(this@MainActivity, HeartRateService::class.java))
                textHR.text = "Monitoring stopped"
            }
        }

        startExerciseBtn = Button(this).apply {
            text = "Start Exercise"
            setOnClickListener {
                val intent = Intent(this@MainActivity, HeartRateService::class.java)
                intent.action = "ACTION_START_EXERCISE"
                startService(intent)
                textHR.text = "Exercise started"
            }
        }

        stopExerciseBtn = Button(this).apply {
            text = "Stop Exercise"
            setOnClickListener {
                val intent = Intent(this@MainActivity, HeartRateService::class.java)
                intent.action = "ACTION_STOP_EXERCISE"
                startService(intent)
                textHR.text = "Exercise stopped"
            }
        }

        // Tambahkan semua ke inner layout
        innerLayout.apply {
            addView(textHR)
            addView(startMonitoringBtn)
            addView(stopMonitoringBtn)
            addView(startExerciseBtn)
            addView(stopExerciseBtn)
        }

        setContentView(scrollView)
    }


    private fun checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startHeartRateService()
        } else {
            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }

    private fun startHeartRateService() {
        val intent = Intent(this, HeartRateService::class.java)
        startService(intent)
        textHR.text = "Monitoring started"
    }
}
