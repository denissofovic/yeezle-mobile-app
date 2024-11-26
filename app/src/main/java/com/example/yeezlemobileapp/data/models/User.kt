package com.example.yeezlemobileapp.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    val id: String,
    val email: String?,
    val phone: String?,
    val created_at: LocalDateTime?,
    val email_confirmed_at: LocalDateTime?,
    val phone_confirmed_at: LocalDateTime?,
    val last_sign_in_at: LocalDateTime?,
    val app_metadata: Map<String, Any>?,
    val user_metadata: Map<String, Any>?,
    val identities: List<Identity>?
)

@Serializable
data class Identity(
    val id: String,
    val provider: String,
    val created_at: LocalDateTime?
)

