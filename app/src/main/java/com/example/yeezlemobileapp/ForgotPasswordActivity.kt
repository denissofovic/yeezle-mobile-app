package com.example.yeezlemobileapp

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityForgotPasswordBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity: AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.passwordResetButton.setOnClickListener{
            val email = binding.email.text.toString()
            if(email.isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity, "Please enter a valid email", Toast.LENGTH_SHORT).show()

            }else{
                val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                sharedPref.edit().putString("email_for_reset", email).apply()
                CoroutineScope(Dispatchers.IO).launch {
                    val response = supabaseAuthHelper.sendPasswordResetLink(email)
                    if(response){
                        Toast.makeText(this@ForgotPasswordActivity, "Reset link sent, please chech your inbox", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@ForgotPasswordActivity, "Couldn't sent reset email", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        binding.backToLoginButton.movementMethod = LinkMovementMethod.getInstance()
        binding.backToLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }



}
