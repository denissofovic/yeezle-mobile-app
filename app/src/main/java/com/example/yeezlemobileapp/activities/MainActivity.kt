package com.example.yeezlemobileapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.yeezlemobileapp.workers.DailyTaskWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateToNextActivity()
    }

    private fun navigateToNextActivity() {
        scheduleDailyTask(this)
        val sharedPreferences = SharedPreferencesHelper(this)
        val email = sharedPreferences.getEmail()
        val password = sharedPreferences.getPassword()

        val nextActivity = if (email == null || password == null) {
            LoginActivity::class.java
        } else {
            DashboardActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }


    private fun scheduleDailyTask(context: Context) {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 1) // 01:00 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (currentTime.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyTaskWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyTaskWorker",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

}
