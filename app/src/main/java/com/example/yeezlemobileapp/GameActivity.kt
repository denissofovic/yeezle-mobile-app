package com.example.yeezlemobileapp


import android.R//napraviti custom polje
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeezlemobileapp.data.models.Album
import com.example.yeezlemobileapp.data.models.GuessItem
import com.example.yeezlemobileapp.data.models.Track
import com.example.yeezlemobileapp.databinding.ActivityGameBinding
import com.example.yeezlemobileapp.supabase.SupabaseSpotifyHelper
import com.example.yeezlemobileapp.utils.GuessItemAdapter
import com.example.yeezlemobileapp.utils.TrackAdapter
import kotlinx.coroutines.launch
import kotlin.math.abs


class GameActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val supabaseSpotifyHelper = SupabaseSpotifyHelper()
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
        val guessItems = mutableListOf<GuessItem>()

        val guessItemAdapter = GuessItemAdapter(guessItems)

        val guessRecycleView = binding.guessRecView
        guessRecycleView.layoutManager = LinearLayoutManager(this)
        guessRecycleView.adapter = guessItemAdapter


        binding.guessButton.setOnClickListener{


            if(binding.songInput.text.isEmpty()){
                Toast.makeText(this@GameActivity, "Enter a track name first", Toast.LENGTH_SHORT).show()

            }else{

                val gameWon = checkGuessedTrack(selectedTrack)

                val totalSeconds = selectedTrack?.duration_ms?.div(1000)
                val minutes = totalSeconds?.div(60)
                val seconds = totalSeconds?.rem(60)
                var duration = String.format("%d:%02d", minutes, seconds)
                if(LENGTH_ORDER == 1){
                    duration += "↑"
                }else if(LENGTH_ORDER == 2){
                    duration += "↓"

                }

                var trackNumber = selectedTrack?.track_number.toString()
                if(TRACK_NUMBER_ORDER == 1){
                    trackNumber += "\n ↑"
                }else if(TRACK_NUMBER_ORDER == 2){
                    trackNumber += "\n ↓"

                }

                val album = albums?.find { it.spotify_id == selectedTrack?.album  }
                val album_url = album?.images?.get(2)?.url



                var features = ""
                for(feature in selectedTrack?.artists!!){
                    if(feature.name != "Kanye West"){
                        features += "${feature.name} "
                    }

                }
                if(features.isEmpty()){
                    features = "No features"
                }

                if(album != null){
                    guessItemAdapter.addGuess(GuessItem(selectedTrack!!.name,
                        album_url, duration, trackNumber, features, CORRECT_ALBUM, CORRECT_LENGTH, CORRECT_TRACK_NUMBER,CORRECT_FEATURES ))

                }

                binding.songInput.text.clear()


                if(gameWon || guessItems.size == NUMBER_OF_GUESSES){
                    GAME_OVER = true
                }


            }
        }



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
                    val adapter = TrackAdapter(this@GameActivity,R.layout.simple_dropdown_item_1line,
                        tracks!!
                    )
                    binding.songInput.setAdapter(adapter)

                    binding.songInput.setOnItemClickListener { parent, view, position, id ->
                        selectedTrack = parent.getItemAtPosition(position) as Track
                        binding.songInput.setText(selectedTrack!!.name)

                    }
                }

                runOnUiThread {
                    Toast.makeText(this@GameActivity, "Data fetched and saved successfully", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@GameActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
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


}