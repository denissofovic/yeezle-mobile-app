package com.example.yeezlemobileapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yeezlemobileapp.R
import com.example.yeezlemobileapp.data.models.LeaderboardItem

class LeaderboardAdapter(private val leaderboardList: List<LeaderboardItem>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankTextView: TextView = itemView.findViewById(R.id.playerRank)
        val playerNameTextView: TextView = itemView.findViewById(R.id.playerName)
        val scoreTextView: TextView = itemView.findViewById(R.id.playerScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        try {
            val entry = leaderboardList[position]

            holder.rankTextView.text = "#${position + 1}"
            holder.playerNameTextView.text = entry.username ?: "Unknown Player"
            holder.scoreTextView.text = entry.score?.toString() ?: "0"
        } catch (e: Exception) {
            Log.e("LeaderboardAdapter", "Error binding data at position $position: ${e.message}", e)
            holder.rankTextView.text = "#${position + 1}"
            holder.playerNameTextView.text = "Error"
            holder.scoreTextView.text = "-"
        }
    }

    override fun getItemCount(): Int = leaderboardList.size
}