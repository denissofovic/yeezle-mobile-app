package com.example.yeezlemobileapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityVerificationBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VerificationActivity : AppCompatActivity() {

    lateinit var binding : ActivityVerificationBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var email = ""
        val resendEmail = intent.getStringExtra("resend_email")
        if (resendEmail != null) {
                email = resendEmail
        }
        binding.resendButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val success = supabaseAuthHelper.resendEmailConfirmation(email)
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(this@VerificationActivity, "Email sent. Please check your inbox.", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@VerificationActivity, "Email resend failed. Try again later.", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }


    }

}