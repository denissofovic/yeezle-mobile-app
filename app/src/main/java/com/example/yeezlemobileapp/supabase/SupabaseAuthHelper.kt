package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthHelper {

    private val supabase = Supabase.getSupabaseClient()
    private val REDIRECT_AUTH_URL = BuildConfig.REDIRECT_URI_AUTH
    private val REDIRECT_RESET_URL = BuildConfig.REDIRECT_URI_RESET

    suspend fun signUpUser(email: String, password: String, username: String): Boolean {
        return try {
            val metadata = buildJsonObject {
                put("username", username)
            }
            supabase.auth.signUpWith(Email, REDIRECT_AUTH_URL) {
                this.email = email
                this.password = password
                this.data = metadata
            }

            logInUser(email, password)
            true
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during sign up: ${e.message}")
            false
        }
    }

    suspend fun logInUser(email: String, password: String): Boolean {
        return try {
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                Log.d("SupabaseAuthHelper", "Login successful")
                true
            } else {
                logOutUser()
                false
            }
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during login: ${e.message}")
            false
        }
    }

    suspend fun logOutUser(): Boolean {
        return try {
            if (supabase.auth.currentUserOrNull() == null) {
                return false
            }
            supabase.auth.signOut()
            true
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during logout: ${e.message}")
            false
        }
    }

    suspend fun sendPasswordResetLink(email: String): Boolean {
        return try {
            supabase.auth.resetPasswordForEmail(email = email, REDIRECT_RESET_URL)
            true
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during sending reset link: ${e.message}")
            false
        }
    }

    suspend fun resetUserPassword(email: String, newPassword: String): Boolean {
        return try {
            var page = 1
            var userUid: String? = null
            while (userUid == null) {
                val users = supabase.auth.admin.retrieveUsers(page)

                val user = users.firstOrNull { it.email == email }
                if (user != null) {
                    userUid = user.id
                } else if (users.isEmpty()) {
                    break
                }
                page++
            }

            if (userUid != null) {
                supabase.auth.admin.updateUserById(uid = userUid) {
                    password = newPassword
                }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during password reset: ${e.message}")
            false
        }
    }

    suspend fun resendEmailConfirmation(email: String): Boolean {
        return try {
            supabase.auth.resendEmail(OtpType.Email.SIGNUP, email)
            true
        } catch (e: Exception) {
            Log.e("SupabaseAuthHelper", "Error during resend: ${e.message}")
            false
        }
    }
}
