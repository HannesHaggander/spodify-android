package com.towerowl.spodify.data.api

data class SearchItem(
    val href: String?,
    val items: List<Any>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)