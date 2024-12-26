package com.example.yeezlemobileapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TestService : Service() {

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        Log.d("com.example.yeezlemobileapp.TestService", "Service created")

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "test_service_channel")
            .setContentTitle("Test Service Running")
            .setContentText("This is a test notification for the foreground service.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
        Log.d("com.example.yeezlemobileapp.TestService", "Foreground service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("com.example.yeezlemobileapp.TestService", "Service started with startId: $startId")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("com.example.yeezlemobileapp.TestService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not used for this test
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "test_service_channel",
                "Test Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "This is a test notification channel for the com.example.yeezlemobileapp.TestService."
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("com.example.yeezlemobileapp.TestService", "Notification channel created")
        }
    }
}
