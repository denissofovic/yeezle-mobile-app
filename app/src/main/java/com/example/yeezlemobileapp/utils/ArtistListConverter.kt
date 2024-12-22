package com.example.yeezlemobileapp.utils

import androidx.room.TypeConverter
import com.example.yeezlemobileapp.data.models.Artist
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class ArtistListConverter {

    private val objectMapper = jacksonObjectMapper()

    @TypeConverter
    fun fromArtistList(artists: List<Artist>): String {
        return objectMapper.writeValueAsString(artists)
    }

    @TypeConverter
    fun toArtistList(artistsString: String): List<Artist> {
        return objectMapper.readValue(artistsString)
    }
}
