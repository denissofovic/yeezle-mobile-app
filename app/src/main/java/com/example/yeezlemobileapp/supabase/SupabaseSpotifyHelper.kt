package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.data.models.Album
import com.example.yeezlemobileapp.data.models.Track
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.JacksonSerializer


class SupabaseSpotifyHelper {
    val supabase = Supabase().initialize()


    suspend fun insertAlbums(albums: List<Album>) {
        try {
            supabase.from("album").insert(albums)
            Log.d("SupabaseHelper", "Inserted albums successfully")
        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Failed to insert albums: ${e.message}")
        }
    }

    suspend fun insertTracks(tracks: List<Track>, albumId: String) {
        try {

            val trackRecords = tracks.map { track ->
                mapOf(
                    "spotify_id" to track.id,
                    "album" to albumId,
                    "name" to track.name,
                    "artists" to track.artists,
                    "preview_url" to track.preview_url,
                    "duration_ms" to track.duration_ms,
                    "track_number" to track.track_number
                )
            }

            Log.d("SupabaseHelper", trackRecords.toString())

            supabase.from("track").insert(trackRecords)

        } catch (e: Exception) {
            Log.e("SupabaseHelper", "Error inserting tracks: ${e.message}")
        }
    }
}




