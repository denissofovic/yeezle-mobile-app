package com.example.yeezlemobileapp.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.data.models.GuessItem

class GuessItemAdapter(private val guessItems: MutableList<GuessItem>) :
    RecyclerView.Adapter<GuessItemAdapter.GuessViewHolder>() {

    private companion object {
        const val COLOR_YELLOW = "#FFC107"
        const val COLOR_GREEN = "#8BC34A"
        const val COLOR_GRAY = "#9E9E9E"
    }

    inner class GuessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songName: TextView = itemView.findViewById(R.id.song_name)
        val albumImageContainer: FrameLayout = itemView.findViewById(R.id.album_image_container)
        val album: ImageView = itemView.findViewById(R.id.album_image)
        val trackNumber: TextView = itemView.findViewById(R.id.track_number)
        val trackLength: TextView = itemView.findViewById(R.id.track_length)
        val features: TextView = itemView.findViewById(R.id.features)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guess, parent, false)
        return GuessViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        val guess = guessItems[position]
        holder.apply {
            songName.text = guess.song
            trackNumber.text = guess.track_number.toString()
            trackLength.text = guess.length
            features.text = guess.features

            Glide.with(album.context)
                .load(guess.album)
                .into(album)

            albumImageContainer.setBackgroundColor(getColor(guess.correct_album))
            trackLength.setTextColor(getColor(guess.correct_length))
            trackNumber.setTextColor(getColor(guess.correct_track_number))
            features.setTextColor(getColor(guess.correct_features))
        }
    }

    override fun getItemCount(): Int = guessItems.size

    fun addGuess(guess: GuessItem) {
        guessItems.add(guess)
        notifyItemInserted(guessItems.size - 1)
    }

    private fun getColor(correctness: Int): Int {
        return when (correctness) {
            1 -> Color.parseColor(COLOR_GREEN)
            2 -> Color.parseColor(COLOR_YELLOW)
            else -> Color.parseColor(COLOR_GRAY)
        }
    }
}
