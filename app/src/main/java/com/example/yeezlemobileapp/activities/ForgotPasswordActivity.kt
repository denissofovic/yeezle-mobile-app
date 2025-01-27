package com.example.yeezlemobileapp.activities

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

                CoroutineScope(Dispatchers.IO).launch {
                    val response = supabaseAuthHelper.sendPasswordResetLink(email)
                    if(response) {
                        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                        intent.putExtra("reset_link_sent", true)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Couldn't send reset email", Toast.LENGTH_SHORT).show()
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
