
package com.example.yeezlemobileapp
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityMainBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val resetPasswordSuccess = intent.getBooleanExtra("reset_password_success", false)
        if(resetPasswordSuccess){
            Toast.makeText(this, "Password successfully changed", Toast.LENGTH_SHORT).show()
        }

        val signupSuccess = intent.getBooleanExtra("signup_success", false)
        if(resetPasswordSuccess){
            Toast.makeText(this, "All done, welcome", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val response = supabaseAuthHelper.logOutUser()
                if(response){
                    val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        clear()
                        apply()
                    }
                    redirectToLoginActivity()
                    finish()

                }else{
                    Toast.makeText(this@MainActivity, "You are not logged in", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    private fun redirectToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("logout_success", true)  // Add an extra to track the source
        startActivity(intent)
        finish()
    }




}