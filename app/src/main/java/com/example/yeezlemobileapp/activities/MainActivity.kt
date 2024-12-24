
package com.example.yeezlemobileapp.activities
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.yeezlemobileapp.supabase.SupabaseAuthHelper
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    private val supabaseAuthHelper = SupabaseAuthHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
                val currentUser = getCurrentUser()

                if (currentUser == null) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } else {
                   startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                }

            finish()
        }


    }

    private fun getCurrentUser(): UserInfo? {
        return try {
            supabaseAuthHelper.supabase.auth.currentUserOrNull()
        } catch (e: Exception) {
            null
        }
    }


}