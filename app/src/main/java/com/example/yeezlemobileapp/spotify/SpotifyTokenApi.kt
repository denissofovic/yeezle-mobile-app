import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import com.example.yeezlemobileapp.data.models.AccessTokenResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val CLIENT_ID = BuildConfig.CLIENT_ID
private const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
const val REDIRECT_URI = BuildConfig.REDIRECT_URI

interface SpotifyTokenApi {
    @FormUrlEncoded
    @POST("api/token")
    fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<AccessTokenResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://accounts.spotify.com/"

    val instance: SpotifyTokenApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyTokenApi::class.java)
    }
}

fun authenticateSpotifyUser(redirectUri: String): Intent {
    val authUrl = "https://accounts.spotify.com/authorize" +
            "?client_id=$CLIENT_ID" +
            "&response_type=code" +
            "&redirect_uri=$redirectUri" +
            "&scope=user-read-private"

    return Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
}

fun exchangeCodeForToken(
    authorizationCode: String,
    redirectUri: String,
    context: Context,
    callback: (String) -> Unit
) {
    Log.d("SpotifyTokenApi", "Starting exchange code for token with authorization code: $authorizationCode")

    val tokenApi = RetrofitClient.instance
    val call = tokenApi.getAccessToken(
        grantType = "authorization_code",
        code = authorizationCode,
        redirectUri = redirectUri,
        clientId = CLIENT_ID,
        clientSecret = CLIENT_SECRET
    )

    Log.d("SpotifyTokenApi", "Making network request to get access token...")

    call.enqueue(object : Callback<AccessTokenResponse> {
        override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
            Log.d("SpotifyTokenApi", "Response received. Status code: ${response.code()}")

            if (response.isSuccessful) {
                val accessToken = response.body()?.access_token
                if (!accessToken.isNullOrEmpty()) {
                    Log.d("SpotifyTokenApi", "Access token retrieved successfully: $accessToken")
                    callback(accessToken)
                } else {
                    Log.e("SpotifyTokenApi", "Access token is null or empty.")
                    callback("Error: Access token is null or empty.")
                }
            } else {
                val errorMessage = "Error retrieving access token. Status code: ${response.code()} - ${response.message()}"
                Log.e("SpotifyTokenApi", errorMessage)
                callback(errorMessage)
            }
        }

        override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
            val failureMessage = "Network request failed: ${t.message}"
            Log.e("SpotifyTokenApi", failureMessage)
            callback(failureMessage)
        }
    })
}
