package com.example.yeezlemobileapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yeezlemobileapp.activities.DashboardActivity
import com.example.yeezlemobileapp.receivers.RestartServiceReceiver

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    private val STEP_TRESHOLD = 15
    var ONCE_FLAG = false

    override fun onCreate() {
        super.onCreate()
        Log.d("StepCounterService", "onCreate called")
        try {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, "step_counter_channel")
                .setContentTitle("Step Counter Service")
                .setContentText("Counting your steps...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            startForeground(1, notification)
            Log.d("StepCounterService", "Foreground started")

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepSensor != null) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
                Log.d("StepCounterService", "Sensor registered")
            } else {
                Log.d("StepCounterService", "No step sensor available")
                stopSelf()
            }
        } catch (e: Exception) {
            Log.e("StepCounterService", "Error in onCreate: ${e.message}")
            stopSelf()
        }
    }


    private var initialStepCount = -1

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentStepCount = event.values[0].toInt()

            if (initialStepCount == -1) {
                initialStepCount = currentStepCount
            }

            stepCount = currentStepCount - initialStepCount

            if (stepCount > STEP_TRESHOLD && ONCE_FLAG == false) {
                onStepsExceeded(this)
                ONCE_FLAG = true
            }

            Log.d("StepCounterService", "Total steps counted: $stepCount")
        }
    }

    fun resetStepCount() {
        Log.d("StepCounterService", "Step count reset")
        initialStepCount = -1
        stepCount = 0
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "step_counter_channel",
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Channel for step counter notifications"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }




    private fun onStepsExceeded(context:Context) {
        val intent = Intent("STEP_COUNT_GOAL_ACHIEVED")
        context.sendBroadcast(intent)

        val builder = NotificationCompat.Builder(this, "step_counter_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Goal Achieved!")
            .setContentText("You have unlocked special clue for today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(2, builder.build())

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }
}
