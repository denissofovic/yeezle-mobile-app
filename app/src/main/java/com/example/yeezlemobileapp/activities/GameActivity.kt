package com.example.yeezlemobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.data.models.Album
import com.example.yeezlemobileapp.data.models.GuessItem
import com.example.yeezlemobileapp.data.models.Track
import com.example.yeezlemobileapp.databinding.ActivityGameBinding
import com.example.yeezlemobileapp.supabase.SupabasePlayerHelper
import com.example.yeezlemobileapp.supabase.SupabaseSpotifyHelper
import com.example.yeezlemobileapp.utils.GuessItemAdapter
import com.example.yeezlemobileapp.utils.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs



class GameActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val supabaseSpotifyHelper = SupabaseSpotifyHelper()
    private val supabasePlayerHelper = SupabasePlayerHelper()
    private var tracks : List<Track>? = null
    private var albums : List<Album>? = null
    private var guessingTrack : Track? = null
    private var selectedTrack: Track? = null


    private var GAME_WON = false
    private var CORRECT_ALBUM = 0
    private var CORRECT_TRACK_NUMBER = 0
    private var CORRECT_LENGTH = 0
    private var CORRECT_FEATURES = 0

    private var LENGTH_ORDER = 0
    private var TRACK_NUMBER_ORDER = 0


    private var GAME_OVER = false
    private var NUMBER_OF_GUESSES = 8 //zavisi od koraka



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()
        handleNavigation()
        val guessItems = mutableListOf<GuessItem>()


        val guessItemAdapter = GuessItemAdapter(guessItems)
        val guessRecycleView = binding.guessRecView
        guessRecycleView.layoutManager = LinearLayoutManager(this)
        guessRecycleView.adapter = guessItemAdapter



        val correctTrackName = tracks?.any { it.name == binding.songInput.text.toString() }
        binding.guessButton.setOnClickListener {
            if (binding.songInput.text.isEmpty()) {
                Toast.makeText(this@GameActivity, "Enter a track name first", Toast.LENGTH_SHORT)
                    .show()
            } else if (correctTrackName == false) {
                Log.d("GameActivity", "Track name not found: ${binding.songInput.text}")
                Toast.makeText(this@GameActivity, "Track name not found", Toast.LENGTH_SHORT).show()
            } else {
                if (selectedTrack != null) {
                    val gameWon = checkGuessedTrack(selectedTrack)

                    val totalSeconds = selectedTrack?.duration_ms?.div(1000)
                    val minutes = totalSeconds?.div(60)
                    val seconds = totalSeconds?.rem(60)
                    var duration = String.format("%d:%02d", minutes, seconds)
                    if (LENGTH_ORDER == 1) {
                        duration += "↑"
                    } else if (LENGTH_ORDER == 2) {
                        duration += "↓"
                    }

                    var trackNumber = selectedTrack?.track_number.toString()
                    if (TRACK_NUMBER_ORDER == 1) {
                        trackNumber += "\n ↑"
                    } else if (TRACK_NUMBER_ORDER == 2) {
                        trackNumber += "\n ↓"
                    }

                    val album = albums?.find { it.spotify_id == selectedTrack?.album }
                    val albumUrl = album?.images?.getOrNull(2)?.url

                    var features = ""
                    for (feature in selectedTrack?.artists!!) {
                        if (feature.name != "Kanye West") {
                            features += "${feature.name} "
                        }
                    }
                    if (features.isEmpty()) {
                        features = "No features"
                    }

                    if (album != null) {
                        guessItemAdapter.addGuess(
                            GuessItem(
                                selectedTrack!!.name,
                                albumUrl,
                                duration,
                                trackNumber,
                                features,
                                CORRECT_ALBUM,
                                CORRECT_LENGTH,
                                CORRECT_TRACK_NUMBER,
                                CORRECT_FEATURES
                            )
                        )
                    }

                    binding.songInput.text.clear()

                    if (gameWon || guessItems.size == NUMBER_OF_GUESSES) {
                        CoroutineScope(Dispatchers.IO).launch {
                            updateStats(gameWon, guessItems.size)
                        }
                        showGameEndScreen()
                        GAME_OVER = true
                    }
                } else {
                    Toast.makeText(
                        this@GameActivity,
                        "Please select a track first",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }




    }



    }



    private fun showGameEndScreen() {
        val gameEndDialogView = layoutInflater.inflate(R.layout.dialog_game_end, null)
        val gameEndTitle = gameEndDialogView.findViewById<TextView>(R.id.gameEndTitle)
        val albumImage = gameEndDialogView.findViewById<ImageView>(R.id.gameEndImage)
        val songTitle = gameEndDialogView.findViewById<TextView>(R.id.songTitle)
        if(GAME_WON){
            gameEndTitle.text = "Congrats!"

        }else{
            gameEndTitle.text = "Better luck next time!"
        }

        val album = albums?.find { it.spotify_id == guessingTrack?.album  }
        val albumUrl = album?.images?.get(1)?.url

        Glide.with(this)
            .load(albumUrl)
            .into(albumImage)

        songTitle.text = guessingTrack?.name
        val dialog = AlertDialog.Builder(this)
            .setView(gameEndDialogView)
            .setCancelable(false)
            .create()

        val closeButton = gameEndDialogView.findViewById<Button>(R.id.gameEndCloseButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private suspend fun updateStats(gameWon : Boolean, guesses : Int){
        if(gameWon == true){


            val newScore = supabasePlayerHelper.getScore() + 1000 - (100 * guesses)
            supabasePlayerHelper.incrementStreak()
            supabasePlayerHelper.incrementGamesWon()
            supabasePlayerHelper.setScore(newScore)

        }else{
            supabasePlayerHelper.resetStreak()

        }
        supabasePlayerHelper.incrementGamesPlayed()

    }

    private fun checkGuessedTrack(guess: Track?) : Boolean {
        if(guessingTrack != null && guess != null) {

            return if (guessingTrack!!.id == guess.id) {
                GAME_WON = true
                CORRECT_ALBUM = 1
                CORRECT_TRACK_NUMBER = 1
                CORRECT_LENGTH = 1
                CORRECT_FEATURES = 1
                GAME_WON
            } else {
                checkCorrectAlbum(guess, guessingTrack!!)
                checkCorrectTrackNumber(guess, guessingTrack!!)
                checkCorrectLength(guess, guessingTrack!!)
                checkCorrectFeatures(guess, guessingTrack!!)
                GAME_WON
            }



        }

        return false

    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                tracks = supabaseSpotifyHelper.getTracks()
                albums = supabaseSpotifyHelper.getAlbums()
                guessingTrack = supabaseSpotifyHelper.getGuessingTrack()

                if (tracks != null) {
                    val adapter = TrackAdapter(
                        this@GameActivity,
                        R.layout.simple_dropdown_item_1line,
                        tracks!!
                    )
                    binding.songInput.setAdapter(adapter)

                    binding.songInput.setOnItemClickListener { parent, view, position, id ->
                        selectedTrack = parent.getItemAtPosition(position) as Track
                        binding.songInput.setText(selectedTrack!!.name)
                    }

                    binding.songInput.setOnFocusChangeListener { v, hasFocus ->
                        if (!hasFocus) {
                            val enteredText = binding.songInput.text.toString()
                            selectedTrack = adapter.getCustomTrackName(enteredText)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun checkCorrectAlbum(guess: Track, guessingTrack: Track){
        val guessAlbum = albums?.find { it.spotify_id == guess.album }
        val guessingTrackAlbum = albums?.find { it.spotify_id == guessingTrack.album }

        if (guessAlbum != null && guessingTrackAlbum != null) {
            CORRECT_ALBUM = if(guessAlbum.id == guessingTrackAlbum.id){
                1
            }else if(abs(guessAlbum.id.toInt() - guessingTrackAlbum.id.toInt()) <= 2){
                2
            }else{
                0
            }


        }
    }


    private fun checkCorrectTrackNumber(guess: Track, guessingTrack: Track){

        CORRECT_TRACK_NUMBER = if(guess.track_number == guessingTrack.track_number){
            1
        }else if(abs(guess.track_number - guessingTrack.track_number) <= 2){
            2
        }else{
            0
        }

        TRACK_NUMBER_ORDER = if(guess.track_number < guessingTrack.track_number){
            1
        }else if(guess.track_number > guessingTrack.track_number){
            2
        }else{
            0
        }
    }

    private fun checkCorrectLength(guess: Track, guessingTrack: Track){

        CORRECT_LENGTH = if(guess.duration_ms == guessingTrack.duration_ms){
            1
        }else if(abs(guess.duration_ms - guessingTrack.duration_ms) <= 30000){
            2
        }else{
            0
        }

        LENGTH_ORDER = if(guess.duration_ms < guessingTrack.duration_ms){
            1
        }else if(guess.duration_ms > guessingTrack.duration_ms){
            2
        }else{
            0
        }
    }

    private fun checkCorrectFeatures(guess: Track, guessingTrack: Track){
        val commonFeatures = guess.artists.intersect(guessingTrack.artists.toSet())
        CORRECT_FEATURES = if(guess.artists == guessingTrack.artists){
            1
        }else if(commonFeatures.size > 1){
            2
        }else{
            0
        }
    }


    private fun handleNavigation(){
        val bottomNavigationView = binding.bottomNavigationView


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
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
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