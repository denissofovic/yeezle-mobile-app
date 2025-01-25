package com.example.yeezlemobileapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.yeezlemobileapp.data.models.GuessItem

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferencesGame: SharedPreferences =
        context.getSharedPreferences(GAME_PREFS, Context.MODE_PRIVATE)

    private val sharedPreferencesLogin: SharedPreferences =
        context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val GAME_PREFS = "game_prefs"
        private const val LOGIN_PREFS = "login_prefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_GUESS_ITEMS = "guess_items"
    }

    fun saveLoginInfo(email: String, password: String) {
        sharedPreferencesLogin.edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getEmail(): String? = sharedPreferencesLogin.getString(KEY_EMAIL, null)

    fun getPassword(): String? = sharedPreferencesLogin.getString(KEY_PASSWORD, null)

    fun clearLoginInfo() {
        sharedPreferencesLogin.edit().clear().apply()
    }

    fun saveGuessItems(guessItems: List<GuessItem>) {
        val json = gson.toJson(guessItems)
        sharedPreferencesGame.edit().putString(KEY_GUESS_ITEMS, json).apply()
    }

    fun getGuessItems(): List<GuessItem> {
        val json = sharedPreferencesGame.getString(KEY_GUESS_ITEMS, null)
        val type = object : TypeToken<List<GuessItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearGuessItems() {
        sharedPreferencesGame.edit().remove(KEY_GUESS_ITEMS).apply()
    }
}
