package com.example.yeezlemobileapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.databinding.ActivityDashboardBinding
import com.example.yeezlemobileapp.services.StepCounterService
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import com.example.yeezlemobileapp.utils.CountdownTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val supabasePlayerHelper = SupabasePlayerHelper()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePermissions()
        setupNavigation()
        updateStatsAndUI()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
        updateStatsAndUI()
    }

    private fun initializePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermission(
                Manifest.permission.ACTIVITY_RECOGNITION,
                onGranted = { startStepCounterService() },
                onDenied = {
                    Toast.makeText(this, "Permission required for step counter", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            startStepCounterService()
        }
    }

    private fun requestPermission(permission: String, onGranted: () -> Unit, onDenied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        } else {
            onGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startStepCounterService()
        } else {
            Toast.makeText(this, "Permission required for step counter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startStepCounterService() {
        Log.d("DashboardActivity", "Attempting to start StepCounterService")
        val serviceIntent = Intent(this, StepCounterService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun updateStatsAndUI() {
        lifecycleScope.launch {
            try {
                val stats = fetchStats()
                updateUI(stats)
            } catch (_: Exception) { }
        }
        startCountdownTimer()
    }

    private suspend fun fetchStats(): List<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        val score = supabasePlayerHelper.getScore()
        val gamesPlayed = supabasePlayerHelper.getGamesPlayed()
        val bestStreak = supabasePlayerHelper.getBestStreak()
        val currentStreak = supabasePlayerHelper.getCurrentStreak()
        val gamesWon = supabasePlayerHelper.getGamesWon()
        val gamesLost = maxOf(0, gamesPlayed - gamesWon)

        listOf(
            Pair(R.id.totalScoreValue, score),
            Pair(R.id.gamesPlayedValue, gamesPlayed),
            Pair(R.id.gamesWonValue, gamesWon),
            Pair(R.id.gamesLostValue, gamesLost),
            Pair(R.id.currentStreakValue, currentStreak),
            Pair(R.id.bestStreakValue, bestStreak)
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI( stats: List<Pair<Int, Int>>) {

        stats.forEach { (viewId, value) ->
            findViewById<TextView>(viewId).text = value.toString()
        }
    }

    private fun startCountdownTimer() {
        handler.post(object : Runnable {
            override fun run() {
                val referenceTimeMillis = CountdownTimer().fetchReferenceTimeFromServer()
                val remainingTimeMillis = calculateRemainingTime(referenceTimeMillis)

                updateCountdownUI(remainingTimeMillis)
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun calculateRemainingTime(referenceTimeMillis: Long): Long {
        val currentTimeMillis = System.currentTimeMillis()
        val timeElapsed = (currentTimeMillis - referenceTimeMillis) % TimeUnit.DAYS.toMillis(1)
        return TimeUnit.DAYS.toMillis(1) - timeElapsed
    }

    private fun updateCountdownUI(remainingTimeMillis: Long) {
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) % 60

        binding.nextSongTimerText.text = String.format(
            "Next track in %02d:%02d:%02d",
            hours, minutes, seconds
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupNavigation() {
        binding.fabPlay.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, GameActivity::class.java))
        }

        binding.bottomNavigationView.apply {
            selectedItemId = R.id.navigation_dashboard
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> true
                    R.id.navigation_leaderboard -> navigateTo(LeaderboardActivity::class.java)
                    R.id.navigation_profile -> navigateTo(ProfileActivity::class.java)
                    R.id.navigation_about -> navigateTo(AboutActivity::class.java)
                    else -> false
                }
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) = true.also {
        startActivity(Intent(this, activityClass))
        overridePendingTransition(0, 0)
    }
}