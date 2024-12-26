package com.example.yeezlemobileapp.activities

import NotificationHelper
import android.Manifest
import com.example.yeezlemobileapp.StepCounterService
import com.example.yeezlemobileapp.TestService
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.data.models.StatItem
import com.example.yeezlemobileapp.utils.StatsAdapter
import com.example.yeezlemobileapp.databinding.ActivityDashboardBinding
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import com.example.yeezlemobileapp.utils.CountdownTimer
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val supabasePlayerHelper = SupabasePlayerHelper()
    private val handler = Handler()
    private val notificationHelper = NotificationHelper(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 100)
            } else {
                startStepCounterService()
            }
        } else {
            startStepCounterService()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
        */




        /*
        val loginSuccess = intent.getBooleanExtra("login_success", false)
        if (loginSuccess) {
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
        }
        */

        binding.fabPlay.setOnClickListener {
            redirectToGameActivity()
        }

        handleNavigation()
        fetchStatsAndUpdateUI()

    }

    override fun onResume() {
        super.onResume()
        handleNavigation()
        fetchStatsAndUpdateUI()
    }

    private fun startStepCounterService() {
        Log.d("DashboardActivity", "Attempting to start StepCounterService")
        val serviceIntent = Intent(this, StepCounterService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }


    private fun stopStepCounterService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startStepCounterService()
        } else {
            Toast.makeText(this, "Permission required for step counter", Toast.LENGTH_SHORT).show()
        }
    }





    @SuppressLint("SetTextI18n")
    private fun fetchStatsAndUpdateUI() {
        lifecycleScope.launch {
            try {
                val username = supabasePlayerHelper.getUsername()
                val statsList = withContext(Dispatchers.IO) {
                    val score = supabasePlayerHelper.getScore()
                    val gamesPlayed = supabasePlayerHelper.getGamesPlayed()
                    val bestStreak = supabasePlayerHelper.getBestStreak()
                    val currentStreak = supabasePlayerHelper.getCurrentStreak()
                    val gamesWon = supabasePlayerHelper.getGamesWon()
                    val gamesLost = maxOf(0, gamesPlayed - gamesWon)

                    listOf(
                        StatItem(R.drawable.ic_dashboard, "Total Score", score),
                        StatItem(R.drawable.ic_dashboard, "Games Played", gamesPlayed),
                        StatItem(R.drawable.ic_dashboard, "Games Won", gamesWon),
                        StatItem(R.drawable.ic_dashboard, "Games Lost", gamesLost),
                        StatItem(R.drawable.ic_dashboard, "Current Streak", currentStreak),
                        StatItem(R.drawable.ic_dashboard, "Best Streak", bestStreak)
                    )
                }

                binding.nicknameText.text = "Welcome, ${username}"
                binding.statsRecyclerView.layoutManager = LinearLayoutManager(this@DashboardActivity)
                binding.statsRecyclerView.adapter = StatsAdapter(statsList)

            } catch (e: Exception) {
                Toast.makeText(
                    this@DashboardActivity,
                    "Failed to load stats: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        handler.post(object : Runnable {
            override fun run() {
                val referenceTimeMillis = CountdownTimer().fetchReferenceTimeFromServer()
                val currentTimeMillis = System.currentTimeMillis()
                val timeElapsed = (currentTimeMillis - referenceTimeMillis) % (24 * 60 * 60 * 1000L)
                val remainingTimeMillis = (24 * 60 * 60 * 1000L) - timeElapsed

                if(remainingTimeMillis <= 1000){
                    SharedPreferencesHelper(this@DashboardActivity).clearGuessItems()
                    notificationHelper.sendNotification("Game time", "New song just got generated")
                    StepCounterService().resetStepCount()
                    StepCounterService().ONCE_FLAG = false
                }

                val hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) % 60

                binding.nextSongTimerText.text = String.format(
                    "Next song in %02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
                )
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun redirectToGameActivity() {
        val intent = Intent(this@DashboardActivity, GameActivity::class.java)
        startActivity(intent)
    }

    private fun handleNavigation(){
        val bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.selectedItemId = R.id.navigation_dashboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    true
                }
                R.id.navigation_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }


}
