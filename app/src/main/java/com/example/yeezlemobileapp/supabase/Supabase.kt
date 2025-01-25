package com.example.yeezlemobileapp.supabase

import com.example.yeezlemobileapp.BuildConfig.SUPABASE_KEY
import com.example.yeezlemobileapp.BuildConfig.SUPABASE_URL
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.JacksonSerializer


object Supabase {
    private val client: SupabaseClient by lazy {
        createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
            defaultSerializer = JacksonSerializer()
            install(Auth)
            install(Postgrest)
        }
    }

    fun getSupabaseClient(): SupabaseClient = client
}
