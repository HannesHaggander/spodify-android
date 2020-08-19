package com.towerowl.spodify.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersSavedShows(
    val href: String,
    val items: List<Item>
) {
    data class Item(
        @Json(name = "added_at")
        val addedAt: String,
        val show: Show,
        val limit: Int?,
        val next: Any?,
        val offset: Int?,
        val previous: Any?,
        val total: Int?
    )
}