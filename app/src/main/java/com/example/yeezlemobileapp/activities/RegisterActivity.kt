package com.example.yeezlemobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityRegisterBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val username = binding.username.text.toString()

            when {
                email.isEmpty() || username.isEmpty() -> {
                    Toast.makeText(this, "Email and username cannot be empty", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
                !validUsernameCheck(username) -> {
                    Toast.makeText(this, "Username must be 4-20 characters long and contain only letters, numbers, or underscores", Toast.LENGTH_LONG).show()
                }
                else -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val success = supabaseAuthHelper.signUpUser(email, password)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                redirectToVerificationActivity(email)
                            } else {
                                Toast.makeText(this@RegisterActivity, "Sign up failed. Try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding.loginButton.movementMethod = LinkMovementMethod.getInstance()
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun redirectToVerificationActivity(email: String) {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.putExtra("resend_email", email)
        startActivity(intent)
        finish()
    }

    private fun validPasswordCheck(password: String): Boolean {
        val passwordRegex = Regex("^.{8,}\$\n")
        return passwordRegex.matches(password)
    }

    private fun validUsernameCheck(username: String): Boolean {
        val usernameRegex = Regex("^[a-zA-Z0-9_]{4,20}$")
        return usernameRegex.matches(username)
    }
}
