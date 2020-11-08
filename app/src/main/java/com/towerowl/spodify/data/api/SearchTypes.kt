package com.towerowl.spodify.data.api

data class EpisodeSearchType(
    val href: String?,
    val items: List<Episode>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)

data class ShowSearchType(
    val href: String?,
    val items: List<Show>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)