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
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header

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
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SpotifyApiService::class.java)
    }
}
    fun fetchArtistAlbums(artistId: String, accessToken: String)  {
        val service = RetrofitClient.instance
        val supabaseHelper = SupabaseSpotifyHelper()
        service.getArtistAlbums(artistId, accessToken = "Bearer $accessToken", limit = 20)
            .enqueue(object : Callback<AlbumsResponse> {
                override fun onResponse(
                    call: Call<AlbumsResponse>,
                    response: Response<AlbumsResponse>
                ) {
                    if (response.isSuccessful) {
                        val albums = response.body()?.items
                        albums?.forEach { album ->
                            fetchAlbumTracks(album.id, accessToken)
                            Log.d(
                                "SpotifyApiService",
                                "Album: ${album.name}, Release Date: ${album.release_date}, ID: ${album.id}"
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                albums.let {
                                    supabaseHelper.insertAlbums(it)
                                }
                            }

                        }


                    } else {
                        Log.e(
                            "SpotifyApiService",
                            "Error occurred: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<AlbumsResponse>, t: Throwable) {
                    Log.e("SpotifyApiService", "Failure occurred: ${t.message}")
                }
            })


    }


fun fetchAlbumTracks(albumId: String, accessToken: String) {
    val service = RetrofitClient.instance
    val supabaseHelper = SupabaseSpotifyHelper()
    service.getAlbumTracks(albumId, accessToken = "Bearer $accessToken").enqueue(object : Callback<TracksResponse> {
        override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
            if (response.isSuccessful) {
                val tracks = response.body()?.items
                tracks?.forEach { track ->
                    Log.d("SpotifyApiService", "Track: ${track.name}, Duration: ${track.duration_ms}ms")
                }

                CoroutineScope(Dispatchers.IO).launch {
                    tracks?.let {
                        supabaseHelper.insertTracks(it, albumId)
                    }
                }
            } else {
                Log.e("SpotifyApiService", "Error occurred: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
            Log.e("SpotifyApiService", "Failure occurred: ${t.message}")
        }
    })

}

fun fetchTrackInfo(trackId: String, accessToken: String) {
    Log.d("SpotifyApiService", "fetchTrackInfo called with trackId: $trackId and accessToken: $accessToken")

    val service = RetrofitClient.instance

    service.getTrackInfo(trackId, accessToken = "Bearer $accessToken").enqueue(object : Callback<TrackInfoResponse> {
        override fun onResponse(call: Call<TrackInfoResponse>, response: Response<TrackInfoResponse>) {
            if (response.isSuccessful) {
                val track = response.body()

                Log.d("SpotifyApiService", "$track")


            } else {
                Log.e("SpotifyApiService", "Error occurred: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<TrackInfoResponse>, t: Throwable) {
            Log.e("SpotifyApiService", "Failure occurred: ${t.message}")
        }})


}
