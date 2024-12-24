package com.example.yeezlemobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.adapters.LeaderboardAdapter
import com.example.yeezlemobileapp.data.models.LeaderboardItem
import com.example.yeezlemobileapp.data.models.StatItem
import com.example.yeezlemobileapp.databinding.ActivityLeaderboardBinding
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaderboardBinding
    private val supabasePlayerHelper = SupabasePlayerHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLeaderboard()
        handleNavigation()







    }

    private fun getLeaderboard(){

        val leaderboardRecycleView = binding.leaderboardRecyclerView
        leaderboardRecycleView.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launch {
            try {
                val topPlayerList = supabasePlayerHelper.getTopPlayers()


                val leaderboardAdapter = LeaderboardAdapter(topPlayerList)
                leaderboardRecycleView.adapter = leaderboardAdapter

            }catch (e: Exception) {
                Toast.makeText(
                    this@LeaderboardActivity,
                    "Failed to load leaderboard: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun handleNavigation(){
        val bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.selectedItemId = R.id.navigation_leaderboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true

                }
                R.id.navigation_leaderboard -> {
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
