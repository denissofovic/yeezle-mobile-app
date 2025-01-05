package com.example.yeezlemobileapp.receivers

import com.example.yeezlemobileapp.services.StepCounterService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, StepCounterService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
