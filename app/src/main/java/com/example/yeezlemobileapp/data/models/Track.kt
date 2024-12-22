package com.example.yeezlemobileapp.data.models
import com.fasterxml.jackson.annotation.JsonProperty
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.yeezlemobileapp.utils.ArtistListConverter


data class TracksResponse(
    val items: List<Track>
)




@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: String,
    val album: String,
    val name: String,
    @TypeConverters(ArtistListConverter::class) val artists: List<Artist>,
    val track_number: Int,
    val preview_url: String?,
    val duration_ms: Int,
    val spotify_id: String
)


data class Artist(
    val id: String,
    val name: String
)

@Entity(
    tableName = "guessing_tracks",
    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = ["id"],
            childColumns = ["track"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GuessingTrack(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val track: Int
)
