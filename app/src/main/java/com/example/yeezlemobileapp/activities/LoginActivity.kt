package com.example.yeezlemobileapp.activities

import NotificationHelper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.databinding.ActivityLoginBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()
    private val notificationHelper = NotificationHelper(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        notificationHelper.createNotificationChannel()
        if (!notificationHelper.hasNotificationPermission()) {
            notificationHelper.requestNotificationPermission(this)
        }


        val authorizeAndLogin = intent.getBooleanExtra("authorize_and_login", false)
        if(authorizeAndLogin){
            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
        }



        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_LONG).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val success = supabaseAuthHelper.logInUser(email, password)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            SharedPreferencesHelper(this@LoginActivity).saveLoginInfo(email,password)
                            redirectToDashboardActivity()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed. Try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        binding.signUpMessage.movementMethod = LinkMovementMethod.getInstance()
        binding.signUpMessage.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.forgotpassword.movementMethod = LinkMovementMethod.getInstance()
        binding.forgotpassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


    }


    override fun onNewIntent(intent: Intent?) {
        val REDIRECT_AUTH_URL = BuildConfig.REDIRECT_URI_AUTH
        Log.d("LoginActivity", "Received redirect intent: ${intent?.data}")
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.toString().startsWith(REDIRECT_AUTH_URL)) {
                Toast.makeText(this@LoginActivity, "Account successfully verified", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        notificationHelper.handlePermissionResult(requestCode, grantResults)
    }

    private fun redirectToDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("login_success", true)
        startActivity(intent)
        finish()
    }
}
