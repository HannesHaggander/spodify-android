package com.towerowl.spodify.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Episode(
    @Json(name = "audio_preview_url")
    val audioPreviewUrl: String,
    val description: String,
    @Json(name = "duration_ms")
    val durationMs: Long,
    val explicit: Boolean,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    @Json(name = "is_externally_hosted")
    val isExternallyHosted: Boolean,
    @Json(name = "is_playable")
    val isPlayable: Boolean,
    val language: String,
    val languages: List<String>,
    val name: String,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "release_date_precision")
    val releaseDatePrecision: String,
    val type: String,
    val uri: String
)