package com.example.yeezlemobileapp.data.models
import com.fasterxml.jackson.annotation.JsonProperty
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.yeezlemobileapp.utils.ImageListConverter

data class AlbumsResponse(
    val items: List<Album>
)



@Entity(tableName = "albums")
data class Album(
    @PrimaryKey val id: String,
    val name: String,
    val release_date: String?,
    @TypeConverters(ImageListConverter::class) val images: List<Image>?,
    val spotify_id: String,
)


data class Image(
    val url: String,
    val height: Int,
    val width: Int
)