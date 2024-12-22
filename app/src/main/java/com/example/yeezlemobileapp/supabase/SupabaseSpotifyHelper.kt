package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.data.models.Album
import com.example.yeezlemobileapp.data.models.GuessingTrack
import com.example.yeezlemobileapp.data.models.Track
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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

    suspend fun getTracks(): List<Track>?{
        var tracks : List<Track>? = null
        try{
            tracks = supabase.from("track").select().decodeList<Track>()
            Log.d("SupabaseHelper", tracks.toString())

        }catch (e : Exception){
            Log.e("SupabaseHelper", "Error getting tracks: ${e.message}")

        }
        return tracks



    }

    suspend fun getAlbums(): List<Album>?{
        var albums : List<Album>? = null
        try{
            albums = supabase.from("album").select().decodeList<Album>()

        }catch (e : Exception){
            Log.e("SupabaseHelper", "Error getting albums: ${e.message}")

        }
        return albums


    }


    suspend fun getGuessingTrack(): Track?{
        var track: Track? = null

        try{
            val res = supabase.from("guessing_track").select()
                .decodeSingle<GuessingTrack>();

            val trackId = res.track
            track = supabase.from("track").select{
                filter {
                    eq("id",trackId)
                }
            }.decodeSingle<Track>()

        }catch (e : Exception){
            Log.e("SupabaseHelper", "Error getting guessing track: ${e.message}")

        }
        return track


    }
}




