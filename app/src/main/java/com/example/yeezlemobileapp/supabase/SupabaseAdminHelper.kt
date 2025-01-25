
import android.util.Log
import com.example.yeezlemobileapp.supabase.Supabase
import io.github.jan.supabase.auth.auth

class SupabaseAdminHelper {

    private val supabase = Supabase.getSupabaseClient()

    suspend fun createUser(email: String, password: String): Boolean {
        return try {
            supabase.auth.admin.createUserWithEmail {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            Log.d("SupabaseAdminHelper", "Error while creating user: ${e.message}")
            false
        }
    }
}