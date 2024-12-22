package com.example.yeezlemobileapp.utils

import androidx.room.TypeConverter
import com.example.yeezlemobileapp.data.models.Image
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class ImageListConverter {

    private val objectMapper = jacksonObjectMapper()

    @TypeConverter
    fun fromImageList(images: List<Image>): String {
        return objectMapper.writeValueAsString(images)  // Convert List<Image> to JSON string
    }

    @TypeConverter
    fun toImageList(imagesString: String): List<Image> {
        return objectMapper.readValue(imagesString)  // Convert JSON string back to List<Image>
    }
}
