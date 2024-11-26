package com.example.yeezlemobileapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.yeezlemobileapp.databinding.ActivityDashboardBinding
import com.example.yeezlemobileapp.databinding.ActivityProfileSetupBinding


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
    }

    private fun redirectToGameActivity() {
        val intent = Intent(this@DashboardActivity, GameActivity::class.java)
        startActivity(intent)
    }
}

