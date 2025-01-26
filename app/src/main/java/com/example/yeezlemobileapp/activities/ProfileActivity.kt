package com.example.yeezlemobileapp.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.databinding.ActivityProfileBinding
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProfileBinding
    private val supabaseAuthHelper = SupabaseAuthHelper()
    private val supabasePlayerHelper = SupabasePlayerHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleNavigation()


        CoroutineScope(Dispatchers.IO).launch {
            val username = supabasePlayerHelper.getUsername()
            withContext(Dispatchers.Main){
                binding.nicknameText.setText(username)

            }
        }


        binding.logoutButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val response = supabaseAuthHelper.logOutUser()
                if(response){
                    SharedPreferencesHelper(this@ProfileActivity).clearLoginInfo()
                    redirectToLoginActivity()
                    finish()

                }else{
                    Toast.makeText(this@ProfileActivity, "You are not logged in", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.editNicknameButton.setOnClickListener {
            val newNickname = binding.nicknameText.text.toString().trim()

            when {
                newNickname.isEmpty() -> {
                    Toast.makeText(this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                newNickname.length > 20 -> {
                    Toast.makeText(this, "Nickname too long (max 20 characters)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }


            CoroutineScope(Dispatchers.Main).launch {
                editUserNickname(newNickname)
                Toast.makeText(this@ProfileActivity, "Username changed successfully", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private suspend fun editUserNickname(newUsername : String) : Boolean {
        val success = supabasePlayerHelper.changeUsername(newUsername)
        binding.nicknameText.setText(newUsername)

        return success
    }


    private fun redirectToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("logout_success", true)
        startActivity(intent)
        finish()
    }

    private fun handleNavigation(){
        val bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_leaderboard -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                R.id.navigation_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }




}