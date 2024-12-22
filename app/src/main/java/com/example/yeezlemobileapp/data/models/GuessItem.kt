package com.example.yeezlemobileapp.data.models

data class GuessItem(
    val song: String,
    val album: String?,
    val length: String,
    val track_number: String,
    val features : String?,
    val correct_album : Int,
    val correct_length : Int,
    val correct_track_number : Int,
    val correct_features : Int

)
