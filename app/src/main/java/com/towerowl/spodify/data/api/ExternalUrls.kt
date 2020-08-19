package com.towerowl.spodify.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalUrls(
    val spotify: String?
)