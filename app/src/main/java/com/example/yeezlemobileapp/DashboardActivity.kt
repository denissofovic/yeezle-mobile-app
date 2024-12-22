package com.example.yeezlemobileapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeezlemobileapp.data.models.StatItem
import com.example.yeezlemobileapp.utils.StatsAdapter
import com.example.yeezlemobileapp.databinding.ActivityDashboardBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
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

        val loginSuccess = intent.getBooleanExtra("login_success", false)
        if (loginSuccess) {
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
        }

        binding.fabPlay.setOnClickListener {
            redirectToGameActivity()
        }

        fetchStatsAndUpdateUI()
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
                    val gamesLost = gamesPlayed - score

                    listOf(
                        StatItem(R.drawable.ic_dashboard, "Total Score", score),
                        StatItem(R.drawable.ic_dashboard, "Games Played", gamesPlayed),
                        StatItem(R.drawable.ic_dashboard, "Games Won", score),
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


}
