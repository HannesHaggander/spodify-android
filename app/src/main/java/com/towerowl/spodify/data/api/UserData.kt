package com.towerowl.spodify.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls,
    val followers: Follower,
    val href: String,
    val id: String,
    val images: List<Image>,
    val type: String,
    val uri: String
) {
    data class Follower(
        val href: String?,
        val total: Int
    )
}
