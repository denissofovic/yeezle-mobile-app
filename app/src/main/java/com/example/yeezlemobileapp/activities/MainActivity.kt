
package com.example.yeezlemobileapp.activities
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yeezlemobileapp.utils.SharedPreferencesHelper


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        val email = SharedPreferencesHelper(this).getEmail()
        val password = SharedPreferencesHelper(this).getPassword()


        if (email == null || password == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }


    }

    override fun onStart() {
        super.onStart()

    }





}