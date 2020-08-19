package com.towerowl.spodify.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    val height: Int?,
    val url: String,
    val width: Int?
)