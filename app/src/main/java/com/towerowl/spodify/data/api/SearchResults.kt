package com.towerowl.spodify.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResults(
    val shows: SearchItem?,
    val episodes: SearchItem?
)
