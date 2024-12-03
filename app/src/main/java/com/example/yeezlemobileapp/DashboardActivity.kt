package com.example.yeezlemobileapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yeezlemobileapp.data.models.StatItem
import com.example.yeezlemobileapp.data.models.StatsAdapter
import com.example.yeezlemobileapp.databinding.ActivityDashboardBinding


class DashboardActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginSuccess = intent.getBooleanExtra("login_success", false)
        if(loginSuccess){
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
        }

        binding.fabPlay.setOnClickListener{
            redirectToGameActivity();
        }

        val statsList = listOf(
            StatItem(R.drawable.ic_dashboard, "Total Score", "2500"),
            StatItem(R.drawable.ic_dashboard, "Games Played", "45"),
            StatItem(R.drawable.ic_dashboard, "Games Won", "30"),
            StatItem(R.drawable.ic_dashboard, "Games Lost", "15"),
            StatItem(R.drawable.ic_dashboard, "Current Streak", "5"),
            StatItem(R.drawable.ic_dashboard, "Best Streak", "10")
        )

        val recyclerView: RecyclerView = findViewById(R.id.statsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StatsAdapter(statsList)

    }

    private fun redirectToGameActivity() {
        val intent = Intent(this@DashboardActivity, GameActivity::class.java)
        startActivity(intent)
    }
}

