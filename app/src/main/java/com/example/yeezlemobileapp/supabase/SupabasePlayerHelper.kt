package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.data.models.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class SupabasePlayerHelper {

    private val supabase = Supabase.getSupabaseClient()

    suspend fun incrementGamesWon(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentScore = supabase.from("player").select(columns = Columns.list("games_won")) {
                filter { eq("user_id", currentUser.id) }
            }.decodeSingle<Map<String, Int>>()
                .get("games_won") ?: 0

            supabase.from("player").update(
                { set("games_won", currentScore + 1) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during games won increment: ${e.message}")
            false
        }
    }

    suspend fun setScore(score: Int): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            supabase.from("player").update(
                { set("score", score) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during score set: ${e.message}")
            false
        }
    }

    suspend fun incrementStreak(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentStreak = supabase.from("player").select(columns = Columns.list("current_streak")) {
                filter { eq("user_id", currentUser.id) }
            }.decodeSingle<Map<String, Int>>()
                .get("current_streak") ?: 0

            supabase.from("player").update(
                { set("current_streak", currentStreak + 1) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during streak increment: ${e.message}")
            false
        }
    }

    suspend fun resetStreak(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            supabase.from("player").update(
                { set("current_streak", 0 as Int) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during streak reset: ${e.message}")
            false
        }
    }

    suspend fun incrementGamesPlayed(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentGamesPlayed = supabase.from("player").select(columns = Columns.list("games_played")) {
                filter { eq("user_id", currentUser.id) }
            }.decodeSingle<Map<String, Int>>()
                .get("games_played") ?: 0

            supabase.from("player").update(
                { set("games_played", currentGamesPlayed + 1) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during games played increment: ${e.message}", e)
            false
        }
    }

    suspend fun changeUsername(newUsername: String): Boolean {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            if (currentUser != null) {
                supabase.from("player").update(
                    { set("username", newUsername) }
                ) {
                    filter { eq("user_id", currentUser.id) }
                }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during changing username: ${e.message}")
            false
        }
    }

    suspend fun getScore(): Int {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("score")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<Score>().score
            } ?: 0
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching score: ${e.message}")
            0
        }
    }

    suspend fun getGamesPlayed(): Int {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("games_played")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<GamesPlayed>().games_played
            } ?: 0
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching games played: ${e.message}")
            0
        }
    }

    suspend fun getBestStreak(): Int {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("best_streak")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<BestStreak>().best_streak
            } ?: 0
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching best streak: ${e.message}")
            0
        }
    }

    suspend fun getCurrentStreak(): Int {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("current_streak")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<CurrentStreak>().current_streak
            } ?: 0
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching current streak: ${e.message}")
            0
        }
    }

    suspend fun getUsername(): String {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("username")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<Username>().username
            } ?: "player"
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching username: ${e.message}")
            "player"
        }
    }

    suspend fun getGamesWon(): Int {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            currentUser?.let {
                supabase.from("player").select(columns = Columns.list("games_won")) {
                    filter { eq("user_id", it.id.toString()) }
                }.decodeSingle<GamesWon>().games_won
            } ?: 0
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching games won: ${e.message}")
            0
        }
    }

    suspend fun getTopPlayers(n: Int = 10): List<LeaderboardItem> {
        return try {
            supabase.from("player").select(columns = Columns.list("username", "score")) {
                order("score", order = Order.DESCENDING)
                limit(n.toLong())
            }.decodeList<LeaderboardItem>()
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching top players: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAlreadyPlayed(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            supabase.from("player").select(columns = Columns.list("already_played")) {
                filter { eq("user_id", currentUser.id) }
            }.decodeSingle<Map<String, Boolean>>()
                .get("already_played") ?: false
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during fetching already played: ${e.message}", e)
            false
        }
    }

    suspend fun setAlreadyPlayed(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            supabase.from("player").update(
                { set("already_played", true) }
            ) {
                filter { eq("user_id", currentUser.id) }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during setting already played: ${e.message}", e)
            false
        }
    }
}
