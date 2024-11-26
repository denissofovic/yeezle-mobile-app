package com.example.yeezlemobileapp.supabase

import android.util.Log
import com.example.yeezlemobileapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.JacksonSerializer

class SupabaseAdminHelper{
    val supabase = Supabase().initialize()


    suspend fun createUser(eMail : String, passWord:String) : Boolean{
        return try{
            supabase.auth.admin.createUserWithEmail {
                email = eMail
                password = passWord
            }
            true
        }catch (e:Exception){
            Log.d("SupabaseAdminHelper", "Error while creating user: ${e.message}")
            false
        }

    }
}