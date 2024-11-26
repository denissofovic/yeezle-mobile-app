package com.example.yeezlemobileapp.data.models
import com.fasterxml.jackson.annotation.JsonProperty

data class AlbumsResponse(
    val items: List<Album>
)


data class Album(
    @JsonProperty("spotify_id") val id: String,
    val name: String,
    val release_date: String,
    val images: List<Image>
)

data class Image(
    val url: String,
    val height: Int,
    val width: Int
)