package com.example.yeezlemobileapp.activities

import REDIRECT_URI
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import authenticateSpotifyUser
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.spotify.fetchArtistAlbums
import exchangeCodeForToken



public class SpotifyActivity : AppCompatActivity(){
    lateinit var authSpotifyButton : Button
    private val ARTIST_ID = BuildConfig.ARTIST_ID

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)
        val intent=authenticateSpotifyUser(REDIRECT_URI)
        authSpotifyButton = findViewById(R.id.authorizeSpotifyButton)
        authSpotifyButton.setOnClickListener{
            startActivity(intent)

        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            Log.d("SpotifyActivity", "onNewIntent called with URI: $uri")

            if (uri.toString().startsWith(REDIRECT_URI)) {
                val authorizationCode = uri.getQueryParameter("code")
                Log.d("SpotifyActivity", "Authorization code: $authorizationCode")

                authorizationCode?.let { code ->
                    exchangeCodeForToken(code, REDIRECT_URI,this) { result ->

                        runOnUiThread {
                            Log.d("SpotifyActivity", "Access token: $result")
                            fetchArtistAlbums(ARTIST_ID, result)
                        }
                    }
                } ?: run {
                    Log.e("SpotifyActivity", "Failed to retrieve authorization code.")
                }
            } else {
                Log.e("SpotifyActivity", "URI does not match REDIRECT_URI: $uri")
            }
        }
    }


}
