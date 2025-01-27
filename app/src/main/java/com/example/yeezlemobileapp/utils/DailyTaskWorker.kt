package com.example.yeezlemobileapp.workers

import NotificationHelper
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.yeezlemobileapp.services.StepCounterService
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper

class DailyTaskWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            resetDailyState(applicationContext)
            Log.d("DailyTaskWorker", "Task executed successfully at 01:00 AM")
            return Result.success()
        } catch (e: Exception) {
            Log.e("DailyTaskWorker", "Error executing task: ${e.localizedMessage}", e)
            return Result.failure()
        }
    }

    private fun resetDailyState(context: Context) {

        SharedPreferencesHelper(context).clearGuessItems()


        NotificationHelper(context).sendNotification(
            title = "Game time",
            message = "New track just got generated. Click here to play!"
        )

        StepCounterService().apply {
            resetStepCount()
            ONCE_FLAG = false
        }
    }
}

