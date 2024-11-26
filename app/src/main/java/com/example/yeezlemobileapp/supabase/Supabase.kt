package com.example.yeezlemobileapp.supabase

import com.example.yeezlemobileapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.JacksonSerializer

class Supabase{
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_KEY = BuildConfig.SUPABASE_KEY


    fun initialize() : SupabaseClient{
        return createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
            defaultSerializer = JacksonSerializer()
            install(Postgrest)
            install(Auth)
        }

    }


}