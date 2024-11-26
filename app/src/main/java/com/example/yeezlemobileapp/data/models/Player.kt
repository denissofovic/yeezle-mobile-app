package com.example.yeezlemobileapp.data.models

data class Player(
    val username:String,
    val score: Int,
    val current_streak: Int,
    val best_streak: Int,
    val games_played: Int
)
