package com.towerowl.spodify.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    val href: String,
    val icons: List<Icon>,
    val id: String,
    val name: String
) {
    data class Icon(
        val height: Int,
        val url: String,
        val width: Int
    )
}