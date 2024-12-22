package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.data.models.BestStreak
import com.example.yeezlemobileapp.data.models.CurrentStreak
import com.example.yeezlemobileapp.data.models.GamesPlayed
import com.example.yeezlemobileapp.data.models.Score
import com.example.yeezlemobileapp.data.models.Username
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


class SupabasePlayerHelper {
    val supabase = Supabase().initialize()

    suspend fun incrementScore(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentScore = supabase.from("player").select(columns = Columns.list("score")) {
                filter {
                    eq("user_id", currentUser.id)
                }
            }.decodeSingle<Int>()

            supabase.from("player").update(
                {
                    set("score", currentScore + 1)
                }
            ) {
                filter {

                    eq("user_id", currentUser.id)
                }
            }
            true

        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during score increment:${e.message}")
            false
        }

    }

    suspend fun setScore(score: Int): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()


            supabase.from("player").update(
                {
                    set("score", score)
                }
            ) {
                filter {

                    eq("user_id", currentUser.id)
                }
            }
            true

        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during score increment:${e.message}")
            false
        }

    }


    suspend fun incrementStreak(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentStreak =
                supabase.from("player").select(columns = Columns.list("current_streak")) {
                    filter {
                        eq("user_id", currentUser.id)
                    }
                }.decodeSingle<Int>()

            supabase.from("player").update(
                {
                    set("current_streak", currentStreak + 1)
                }
            ) {
                filter {

                    eq("user_id", currentUser.id)
                }
            }
            true

        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during streak increment:${e.message}")
            false
        }

    }

    suspend fun resetStreak(): Boolean {
        val reset: Int = 0
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()

            supabase.from("player").update(
                {
                    set("current_streak", reset)
                }
            ) {
                filter {

                    eq("user_id", currentUser.id)
                }
            }
            true

        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during streak reset:${e.message}")
            false
        }

    }

    suspend fun incrementGamesPlayed(): Boolean {
        return try {
            val currentUser = supabase.auth.retrieveUserForCurrentSession()
            val currentGamesPlayed =
                supabase.from("player").select(columns = Columns.list("games_played")) {
                    filter {
                        eq("user_id", currentUser.id)
                    }
                }.decodeSingle<Int>()

            supabase.from("player").update(
                {
                    set("games_played", currentGamesPlayed + 1)
                }
            ) {
                filter {

                    eq("user_id", currentUser.id)
                }
            }
            true

        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during games played increment:${e.message}")
            false
        }

    }


    suspend fun changeUsername(newUsername: String): Boolean {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
            if (currentUser != null) {
                supabase.from("player").update(
                    {
                        set("username", newUsername)
                    }
                ) {
                    filter {
                        eq("user_id", currentUser.id)
                    }
                }

            }

            Log.e("SupabasePlayerHelper", "Status:${currentUser?.id}")
            true
        } catch (e: Exception) {
            Log.e("SupabasePlayerHelper", "Error during changing username:${e.message}")
            false
        }

    }

    suspend fun getScore() : Int{
        try{
            val currentUser = supabase.auth.currentUserOrNull()

            var score = 0
            if (currentUser != null) {
                val res = supabase.from("player")
                    .select(columns = Columns.list("score")){
                        filter {
                            eq("user_id", currentUser.id.toString())
                        }
                    }.decodeSingle<Score>()

                score = res.score


            }

            return score
        }catch (e: Exception){
            Log.e("SupabasePlayerHelper", "Error during catching:${e.message}")
            return 0

        }
    }


    suspend fun getGamesPlayed() : Int{
        try{
            val currentUser = supabase.auth.currentUserOrNull()
            var gamesPlayed = 0
            if (currentUser != null) {
                val res = supabase.from("player")
                    .select(columns = Columns.list("games_played")){
                        filter {
                            eq("user_id", currentUser.id.toString())
                        }
                    }.decodeSingle<GamesPlayed>()

                gamesPlayed = res.games_played


            }

            return gamesPlayed
        }catch (e: Exception){
            Log.e("SupabasePlayerHelper", "Error during catching:${e.message}")
            return 0

        }
    }

    suspend fun getBestStreak() : Int{
        try{
            val currentUser = supabase.auth.currentUserOrNull()
            var bestStreak = 0
            if (currentUser != null) {
                val res = supabase.from("player")
                    .select(columns = Columns.list("best_streak")){
                        filter {
                            eq("user_id", currentUser.id.toString())
                        }
                    }.decodeSingle<BestStreak>()

                bestStreak = res.best_streak


            }

            return bestStreak
        }catch (e: Exception){
            Log.e("SupabasePlayerHelper", "Error during catching:${e.message}")
            return 0

        }
    }

    suspend fun getCurrentStreak() : Int{
        try{
            val currentUser = supabase.auth.currentUserOrNull()
            var currentStreak = 0
            if (currentUser != null) {
                val res = supabase.from("player")
                    .select(columns = Columns.list("current_streak")){
                        filter {
                            eq("user_id", currentUser.id.toString())
                        }
                    }.decodeSingle<CurrentStreak>()

                currentStreak = res.current_streak


            }

            return currentStreak
        }catch (e: Exception){
            Log.e("SupabasePlayerHelper", "Error during catching:${e.message}")
            return 0

        }
    }

    suspend fun getUsername() : String{
        try{
            val currentUser = supabase.auth.currentUserOrNull()
            var username = "player"
            if (currentUser != null) {
                val res = supabase.from("player")
                    .select(columns = Columns.list("username")){
                        filter {
                            eq("user_id", currentUser.id.toString())
                        }
                    }.decodeSingle<Username>()

                username = res.username

            }

            return username

        }catch (e: Exception){
            Log.e("SupabasePlayerHelper", "Error during catching:${e.message}")
            return "player"

        }

    }

}