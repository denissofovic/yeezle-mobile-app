package com.example.yeezlemobileapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.yeezlemobileapp.data.models.GuessItem

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferencesGame: SharedPreferences =
        context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    private val sharedPreferencesLogin: SharedPreferences =
        context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveLoginInfo(email: String, password: String) {
        sharedPreferencesLogin.edit()
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun getEmail(): String? {
        return sharedPreferencesLogin.getString("email", null)
    }

    fun getPassword(): String? {
        return sharedPreferencesLogin.getString("password", null)
    }

    fun clearLoginInfo() {
        sharedPreferencesLogin.edit().clear().apply()
    }

    fun saveGuessItems(guessItems: List<GuessItem>) {
        val gson = Gson()
        val json = gson.toJson(guessItems)
        sharedPreferencesGame.edit().putString("guess_items", json).apply()
    }

    fun getGuessItems(): List<GuessItem> {
        val gson = Gson()
        val json = sharedPreferencesGame.getString("guess_items", null)
        val type = object : TypeToken<List<GuessItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearGuessItems() {
        sharedPreferencesGame.edit().clear().apply()

    }


}
