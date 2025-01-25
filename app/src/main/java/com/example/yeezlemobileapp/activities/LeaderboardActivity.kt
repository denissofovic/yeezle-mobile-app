package com.example.yeezlemobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.adapters.LeaderboardAdapter
import com.example.yeezlemobileapp.databinding.ActivityLeaderboardBinding
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val supabasePlayerHelper = SupabasePlayerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLeaderboard()
        setupNavigation()
    }

    private fun setupLeaderboard() {
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchLeaderboardData()
    }

    private fun fetchLeaderboardData() {
        lifecycleScope.launch {
            try {
                val topPlayerList = withContext(Dispatchers.IO) {
                    supabasePlayerHelper.getTopPlayers()
                }
                binding.leaderboardRecyclerView.adapter = LeaderboardAdapter(topPlayerList)
            } catch (e: Exception) {
                showToast("Failed to load leaderboard: ${e.message}")
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.apply {
            selectedItemId = R.id.navigation_leaderboard
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_dashboard -> navigateTo(DashboardActivity::class.java)
                    R.id.navigation_profile -> navigateTo(ProfileActivity::class.java)
                    R.id.navigation_about -> navigateTo(AboutActivity::class.java)
                    R.id.navigation_leaderboard -> true
                    else -> false
                }
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) = true.also {
        startActivity(Intent(this, activityClass))
        overridePendingTransition(0, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
