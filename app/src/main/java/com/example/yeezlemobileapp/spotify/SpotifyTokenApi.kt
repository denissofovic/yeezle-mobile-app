import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.yeezlemobileapp.db.DatabaseHelper
import com.example.yeezlemobileapp.data.models.AccessTokenResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import android.util.Log
import com.example.yeezlemobileapp.BuildConfig

private const val CLIENT_ID = BuildConfig.CLIENT_ID
private const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
const val REDIRECT_URI = BuildConfig.REDIRECT_URI

interface SpotifyTokenApi {
    @FormUrlEncoded
    @POST("api/token") // Note: Base URL is set in Retrofit
    fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<AccessTokenResponse>
}



fun authenticateSpotifyUser(): Intent {

    val authUrl = "https://accounts.spotify.com/authorize" +
            "?client_id=$CLIENT_ID" +
            "&response_type=code" +
            "&redirect_uri=$REDIRECT_URI" +
            "&scope=user-read-private"

    return Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
}


fun exchangeCodeForToken(authorizationCode: String, context: Context, callback: (String) -> Unit) {
    Log.d("SpotifyTokenApi", "Starting exchange code for token with authorization code: $authorizationCode")

    val retrofit = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tokenApi = retrofit.create(SpotifyTokenApi::class.java)
    val call = tokenApi.getAccessToken(
        grantType = "authorization_code",
        code = authorizationCode,
        redirectUri = REDIRECT_URI,
        clientId = CLIENT_ID,
        clientSecret = CLIENT_SECRET
    )

    Log.d("SpotifyTokenApi", "Making network request to get access token...")

    call.enqueue(object : Callback<AccessTokenResponse> {
        override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
            Log.d("SpotifyTokenApi", "Response received. Status code: ${response.code()}")

            if (response.isSuccessful) {
                val accessToken = response.body()?.access_token
                Log.d("SpotifyTokenApi", "Access token retrieved: $accessToken")

                val dbHelper = DatabaseHelper(context)
                accessToken?.let {
                    dbHelper.insertAccessToken(it)
                    callback(accessToken)
                } ?: run {
                    Log.e("SpotifyTokenApi", "Failed to retrieve access token from response body.")
                    callback("Failed to retrieve access token.")
                }
            } else {
                Log.e("SpotifyTokenApi", "Error retrieving access token. Status code: ${response.code()} - ${response.message()}")
                callback("Error retrieving access token.")
            }
        }

        override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
            Log.e("SpotifyTokenApi", "Network request failed: ${t.message}")
            callback("Network request failed: ${t.message}")
        }
    })
}
