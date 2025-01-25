package com.example.yeezlemobileapp.spotify

import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.data.models.AlbumsResponse
import com.example.yeezlemobileapp.data.models.TrackInfoResponse
import com.example.yeezlemobileapp.data.models.TracksResponse
import com.example.yeezlemobileapp.supabase.SupabaseSpotifyHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/artists/{id}/albums")
    fun getArtistAlbums(
        @Path("id") artistId: String,
        @Query("include_groups") includeGroups: String = "album",
        @Query("limit") limit: Int = 20,
        @Header("Authorization") accessToken: String
    ): Call<AlbumsResponse>

    @GET("v1/albums/{album_id}/tracks")
    fun getAlbumTracks(
        @Path("album_id") albumId: String,
        @Query("market") market: String? = "EN",
        @Header("Authorization") accessToken: String
    ): Call<TracksResponse>

    @GET("v1/tracks/{id}")
    fun getTrackInfo(
        @Path("id") trackId: String,
        @Query("market") market: String? = "EN",
        @Header("Authorization") accessToken: String
    ): Call<TrackInfoResponse>
}

object RetrofitClient {
    private const val BASE_URL = BuildConfig.SPOTIFY_BASE_URL

    val instance: SpotifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }
}

fun fetchArtistAlbums(artistId: String, accessToken: String) {
    val service = RetrofitClient.instance
    val supabaseHelper = SupabaseSpotifyHelper()

    service.getArtistAlbums(artistId, accessToken = "Bearer $accessToken", limit = 20)
        .enqueue(createCallback(
            onSuccess = { response ->
                response.items?.forEach { album ->
                    fetchAlbumTracks(album.id, accessToken)
                    Log.d("SpotifyApiService", "Album: ${album.name}, Release Date: ${album.release_date}, ID: ${album.id}")

                    CoroutineScope(Dispatchers.IO).launch {
                        response.items?.let { supabaseHelper.insertAlbums(it) }
                    }
                }
            },
            onFailure = { error -> Log.e("SpotifyApiService", "Error occurred: $error") }
        ))
}

fun fetchAlbumTracks(albumId: String, accessToken: String) {
    val service = RetrofitClient.instance
    val supabaseHelper = SupabaseSpotifyHelper()

    service.getAlbumTracks(albumId, accessToken = "Bearer $accessToken").enqueue(createCallback(
        onSuccess = { response ->
            response.items?.forEach { track ->
                Log.d("SpotifyApiService", "Track: ${track.name}, Duration: ${track.duration_ms}ms")
            }

            CoroutineScope(Dispatchers.IO).launch {
                response.items?.let { supabaseHelper.insertTracks(it, albumId) }
            }
        },
        onFailure = { error -> Log.e("SpotifyApiService", "Error occurred: $error") }
    ))
}

fun fetchTrackInfo(trackId: String, accessToken: String) {
    Log.d("SpotifyApiService", "Fetching track info for trackId: $trackId")
    val service = RetrofitClient.instance

    service.getTrackInfo(trackId, accessToken = "Bearer $accessToken").enqueue(createCallback(
        onSuccess = { track -> Log.d("SpotifyApiService", "Track info: $track") },
        onFailure = { error -> Log.e("SpotifyApiService", "Error occurred: $error") }
    ))
}

private fun <T> createCallback(
    onSuccess: (T) -> Unit,
    onFailure: (String) -> Unit
): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                response.body()?.let(onSuccess)
            } else {
                onFailure(response.errorBody()?.string().orEmpty())
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(t.message.orEmpty())
        }
    }
}
