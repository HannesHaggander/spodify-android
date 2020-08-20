package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.ShowEpisodes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface Shows {
    @GET("shows/{id}/episodes")
    fun shows(
        @Path("id") showId: String,
        @QueryMap optional: Map<String, String> = mapOf()
    ): Call<ShowEpisodes>

    companion object {
        const val QUERY_OFFSET = "offset"
        const val QUERY_LIMIT = "limit"
    }
}