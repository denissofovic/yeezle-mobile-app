package com.example.yeezlemobileapp.data.models

data class TrackInfoResponse(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: Restrictions?,
    val type: String,
    val uri: String,
    val artists: List<Artist>
)

data class ExternalUrls(
    val spotify: String
)

data class Restrictions(
    val reason: String
)



