package com.example.yeezlemobileapp;


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.databinding.ActivityResetPasswordBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPasswordActivity: AppCompatActivity(){

        private lateinit var binding:ActivityResetPasswordBinding
        private val supabaseAuthHelper= SupabaseAuthHelper()
        override fun onCreate(savedInstanceState: Bundle?){
                super.onCreate(savedInstanceState)
                binding=ActivityResetPasswordBinding.inflate(layoutInflater)
                setContentView(binding.root)

                var email = ""
                val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                email = sharedPref.getString("email_for_reset", null).toString()

                binding.passwordResetButton.setOnClickListener{
                        val password = binding.password.text.toString()
                        val confirmPassword = binding.passwordConfirm.text.toString()

                        if(password != confirmPassword){
                                Toast.makeText(this@ResetPasswordActivity, "Passwords don't match", Toast.LENGTH_SHORT).show()

                        }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                        val response = supabaseAuthHelper.resetUserPassword(email,password)
                                        if(response){
                                                redirectToMainActivity()
                                        }else{
                                                Toast.makeText(this@ResetPasswordActivity, "Couldn't reset your password", Toast.LENGTH_SHORT).show()
                                        }
                                }

                        }
                }

        }

        private fun redirectToMainActivity() {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("reset_password_success", true)
                startActivity(intent)
                finish()
        }
}
