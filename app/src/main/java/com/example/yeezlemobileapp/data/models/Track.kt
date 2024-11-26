package com.example.yeezlemobileapp.data.models
import com.fasterxml.jackson.annotation.JsonProperty



data class TracksResponse(
    val items: List<Track>
)


data class Track(
    @JsonProperty("spotify_id") val id: String,
    val album: String,
    val name: String,
    val artists: List<Artist>,
    val track_number: Int,
    val preview_url: String?,
    val duration_ms: Int,

)

data class Artist(
    val id: String,
    val name: String
)
