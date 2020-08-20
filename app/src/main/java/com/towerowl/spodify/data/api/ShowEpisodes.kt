package com.towerowl.spodify.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShowEpisodes(
    val href: String,
    val items: List<Episode>,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?
)