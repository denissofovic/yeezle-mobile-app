package com.example.yeezlemobileapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleNavigation()

    }


    private fun handleNavigation(){
        val bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.selectedItemId = R.id.navigation_about

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
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
                    true
                }
                else -> false
            }
        }
    }



}
