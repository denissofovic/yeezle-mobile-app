package com.example.yeezlemobileapp

import REDIRECT_URI
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import authenticateSpotifyUser
import com.example.yeezlemobileapp.spotify.fetchAlbumTracks
import com.example.yeezlemobileapp.spotify.fetchArtistAlbums
import exchangeCodeForToken



public class SpotifyActivity : AppCompatActivity(){
    lateinit var authSpotifyButton : Button
    private val ARTIST_ID = BuildConfig.ARTIST_ID




    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)
        val intent=authenticateSpotifyUser()
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
                    exchangeCodeForToken(code, this) { result ->
                        runOnUiThread {
                            if (result != null) {
                                Log.d("SpotifyActivity", "Access token: $result")

                                fetchArtistAlbums(ARTIST_ID, result)




                            } else {
                                Log.e("SpotifyActivity", "Failed to retrieve access token.")
                            }
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
