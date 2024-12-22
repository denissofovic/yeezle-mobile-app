package com.example.yeezlemobileapp.utils

import android.graphics.Color
import android.net.Uri
import android.util.Log
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

        private  val YELLOW = "#FFC107"
        private  val GREEN = "#8BC34A"
        private val GRAY = "#9E9E9E"
    inner class GuessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songName: TextView = itemView.findViewById(R.id.song_name)
        val albumImageContainer = itemView.findViewById<FrameLayout>(R.id.album_image_container)
        val album: ImageView = itemView.findViewById(R.id.album_image)
        val trackNumber: TextView = itemView.findViewById(R.id.track_number)
        val trackLength: TextView = itemView.findViewById(R.id.track_length)
        val features: TextView = itemView.findViewById(R.id.features)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guess, parent, false)
        return GuessViewHolder(view)
    }

    override fun onBindViewHolder(holder:GuessViewHolder, position: Int) {
        val guess = guessItems[position]
        holder.songName.text = guess.song
        holder.trackNumber.text = guess.track_number.toString()
        holder.trackLength.text = guess.length
        holder.features.text = guess.features

        Glide.with(holder.album.context)
            .load(guess.album)
            .into(holder.album)

        if(guess.correct_album == 1){
            holder.albumImageContainer.setBackgroundColor(Color.parseColor(GREEN))
        }else if(guess.correct_album == 2){
            holder.albumImageContainer.setBackgroundColor(Color.parseColor(YELLOW))
        }else{
            holder.albumImageContainer.setBackgroundColor(Color.parseColor(GRAY))
        }

        if(guess.correct_length == 1){
            holder.trackLength.setTextColor(Color.parseColor(GREEN))
        }else if(guess.correct_length == 2){
            holder.trackLength.setTextColor(Color.parseColor(YELLOW))
        }else{
            holder.trackLength.setTextColor(Color.parseColor(GRAY))
        }

        if(guess.correct_track_number == 1){
            holder.trackNumber.setTextColor(Color.parseColor(GREEN))
        }else if(guess.correct_track_number == 2){
            holder.trackNumber.setTextColor(Color.parseColor(YELLOW))
        }else{
            holder.trackNumber.setTextColor(Color.parseColor(GRAY))
        }

        if(guess.correct_features == 1){
            holder.features.setTextColor(Color.parseColor(GREEN))
        }else if(guess.correct_features == 2){
            holder.features.setTextColor(Color.parseColor(YELLOW))
        }else{
            holder.features.setTextColor(Color.parseColor(GRAY))
            
        }
    }

    override fun getItemCount(): Int = guessItems.size

    fun addGuess(guess: GuessItem) {
        guessItems.add(guess)
        notifyItemInserted(guessItems.size - 1)
    }
}
