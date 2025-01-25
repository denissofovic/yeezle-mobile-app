package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.data.models.Album
import com.example.yeezlemobileapp.data.models.GuessingTrack
import com.example.yeezlemobileapp.data.models.Track
import io.github.jan.supabase.postgrest.from

class SupabaseSpotifyHelper {

    private val supabase = Supabase.getSupabaseClient()

    suspend fun insertAlbums(albums: List<Album>) {
        try {
            supabase.from("album").insert(albums)
            Log.d("SupabaseSpotifyHelper", "Inserted albums successfully")
        } catch (e: Exception) {
            Log.e("SupabaseSpotifyHelper", "Failed to insert albums: ${e.message}")
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

            Log.d("SupabaseSpotifyHelper", trackRecords.toString())
            supabase.from("track").insert(trackRecords)
        } catch (e: Exception) {
            Log.e("SupabaseSpotifyHelper", "Error inserting tracks: ${e.message}")
        }
    }

    suspend fun getTracks(): List<Track>? {
        return try {
            val tracks = supabase.from("track").select().decodeList<Track>()
            Log.d("SupabaseSpotifyHelper", tracks.toString())
            tracks
        } catch (e: Exception) {
            Log.e("SupabaseSpotifyHelper", "Error getting tracks: ${e.message}")
            null
        }
    }

    suspend fun getAlbums(): List<Album>? {
        return try {
            val albums = supabase.from("album").select().decodeList<Album>()
            Log.d("SupabaseSpotifyHelper", albums.toString())
            albums
        } catch (e: Exception) {
            Log.e("SupabaseSpotifyHelper", "Error getting albums: ${e.message}")
            null
        }
    }

    suspend fun getGuessingTrack(): Track? {
        return try {
            val res = supabase.from("guessing_track").select().decodeSingle<GuessingTrack>()
            val trackId = res.track

            supabase.from("track").select {
                filter { eq("id", trackId) }
            }.decodeSingle<Track>()
        } catch (e: Exception) {
            Log.e("SupabaseSpotifyHelper", "Error getting guessing track: ${e.message}")
            null
        }
    }
}
