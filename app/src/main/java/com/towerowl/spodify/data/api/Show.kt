package com.towerowl.spodify.data.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Show(
    @Json(name = "available_markets")
    val availableMarkets: List<String>,
    val copyrights: List<String>,
    val description: String,
    val explicit: Boolean,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    @Json(name = "is_externally_hosted")
    val isExternallyHosted: Boolean,
    val languages: List<String>,
    @Json(name = "media_type")
    val mediaType: String,
    val name: String,
    val publisher: String,
    @Json(name = "total_episodes")
    val totalEpisodes: Int,
    val type: String,
    val uri: String
) : Parcelable