package com.example.yeezlemobileapp.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yeezlemobileapp.activities.GameActivity
import com.example.yeezlemobileapp.receivers.RestartServiceReceiver
import com.example.yeezlemobileapp.utils.StepGoalAchievedEvent
import org.greenrobot.eventbus.EventBus

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    private var initialStepCount = -1
    private val STEP_THRESHOLD = 15
    var ONCE_FLAG = false

    override fun onCreate() {
        super.onCreate()
        Log.d("StepCounterService", "Service created")

        try {
            setupNotification()
            initializeStepSensor()
        } catch (e: Exception) {
            Log.e("StepCounterService", "Error during service creation: ${e.message}")
            stopSelf()
        }
    }

    private fun setupNotification() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "step_counter_channel")
            .setContentTitle("Yeezle")
            .setContentText("Counting your steps...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "step_counter_channel",
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for step counter notifications"
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun initializeStepSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            Log.d("StepCounterService", "Step sensor registered")
        } else {
            Log.e("StepCounterService", "No step sensor available")
            stopSelf()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            handleStepEvent(event.values[0].toInt())
        }
    }

    private fun handleStepEvent(currentStepCount: Int) {
        if (initialStepCount == -1) {
            initialStepCount = currentStepCount
        }

        stepCount = currentStepCount - initialStepCount
        Log.d("StepCounterService", "Steps counted: $stepCount")

        if (stepCount > STEP_THRESHOLD && !ONCE_FLAG) {
            notifyStepGoalAchieved()
            ONCE_FLAG = true
        }
    }

    fun resetStepCount() {
        Log.d("StepCounterService", "Step count reset")
        initialStepCount = -1
        stepCount = 0
    }

    private fun notifyStepGoalAchieved() {
        EventBus.getDefault().post(StepGoalAchievedEvent())
        sendGoalNotification()
    }

    private fun sendGoalNotification() {
        val intent = Intent(this, GameActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "step_counter_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Goal Achieved!")
            .setContentText("You have unlocked a special clue for today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(2, notification)
        } else {
            Log.w("StepCounterService", "Notification permission not granted")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sendRestartBroadcast()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        sendRestartBroadcast()
    }

    private fun sendRestartBroadcast() {
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }
}