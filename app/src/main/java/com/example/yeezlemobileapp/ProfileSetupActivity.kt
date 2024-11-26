package com.example.yeezlemobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityProfileSetupBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileSetupActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val MINIMAL_USERNAME_LENGTH = 5
    private val supabasePlayerHelper = SupabasePlayerHelper()
    private val supabaseAuthHelper = SupabaseAuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectButton.setOnClickListener{
            val username = binding.username.text.toString()
            if(username.isEmpty() || username.length < MINIMAL_USERNAME_LENGTH){
                Toast.makeText(this@ProfileSetupActivity, "Please choose valid username ", Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).launch {

                    val response = supabasePlayerHelper.changeUsername(username)

                    if(response){
                        redirectToMainActivity()
                        Toast.makeText(this@ProfileSetupActivity, "Username successfully updated", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@ProfileSetupActivity, "There was a problem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)


    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("signup_success", true)  // Add an extra to track the source
        startActivity(intent)
        finish()
    }



}
