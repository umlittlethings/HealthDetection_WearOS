package com.chrisp.healthdetect

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class HeartRateWearableListenerService : WearableListenerService() {

    private val TAG = "PhoneWearableListener"
    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "wearable_listener_channel"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WearableListenerService created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, createNotification())
            Log.d(TAG, "Service started as foreground service")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "WearableListenerService onStartCommand called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification())
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wearable Communication",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Listening for wearable device messages"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Heart Rate Monitor")
            .setContentText("Listening for heart rate data from watch")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(TAG, "=== MESSAGE RECEIVED ===")
        Log.d(TAG, "Source node: ${messageEvent.sourceNodeId}")
        Log.d(TAG, "Message path: ${messageEvent.path}")
        Log.d(TAG, "Message data size: ${messageEvent.data.size}")
        Log.d(TAG, "Raw data: ${messageEvent.data.contentToString()}")

        if (messageEvent.path == "/heart-rate") {
            try {
                val heartRate = String(messageEvent.data, Charsets.UTF_8)
                Log.d(TAG, "Heart rate received: $heartRate")

                // Broadcast to MainActivity
                val broadcastIntent = Intent("HEART_RATE_UPDATE").apply {
                    putExtra("heart_rate", heartRate)
                    // Add flags to ensure broadcast is delivered
                    addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                }

                sendBroadcast(broadcastIntent)
                Log.d(TAG, "Broadcast sent with heart rate: $heartRate")

                // Update notification to show latest heart rate
                updateNotificationWithHeartRate(heartRate)

            } catch (e: Exception) {
                Log.e(TAG, "Error processing heart rate message: ${e.message}", e)
            }
        } else {
            Log.w(TAG, "Received message with unexpected path: ${messageEvent.path}")
        }
    }

    private fun updateNotificationWithHeartRate(heartRate: String) {
        try {
            val updatedNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Heart Rate Monitor")
                .setContentText("Latest: $heartRate BPM")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setOngoing(true)
                .build()

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.notify(NOTIFICATION_ID, updatedNotification)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WearableListenerService destroyed")
    }
}