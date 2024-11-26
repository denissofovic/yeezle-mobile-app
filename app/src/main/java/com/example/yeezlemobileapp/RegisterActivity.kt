package com.example.yeezlemobileapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityLoginBinding
import com.example.yeezlemobileapp.databinding.ActivityRegisterBinding
import com.example.yeezlemobileapp.spotify.fetchArtistAlbums
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
        binding.signUpButton.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.password.text.toString()

            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_LONG).show()
            }else if(password != confirmPassword){
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
            }else{

                CoroutineScope(Dispatchers.IO).launch {
                val success = supabaseAuthHelper.signUpUser(email,password)
                withContext(Dispatchers.Main) {
                    if (success) {
                        redirectToProfileSetupActivity()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Sign up failed. Try again.", Toast.LENGTH_LONG).show()
                    }
                }



                }
        }

        }
    }


    private fun redirectToVerificationActivity(email: String) {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.putExtra("resend_email", email)
        startActivity(intent)
        finish()
    }

    private fun redirectToProfileSetupActivity() {
        val intent = Intent(this, ProfileSetupActivity::class.java)
        startActivity(intent)
        finish()
    }


}