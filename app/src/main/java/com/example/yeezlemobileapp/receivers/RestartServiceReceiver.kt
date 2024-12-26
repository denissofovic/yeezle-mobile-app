package com.example.yeezlemobileapp.receivers

import com.example.yeezlemobileapp.StepCounterService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class RestartServiceReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.startForegroundService(serviceIntent)
    }
}
