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
        @QueryMap optional: Map<String, String> = mapOf(
            QUERY_OFFSET_DEFAULT,
            QUERY_LIMIT_DEFAULT
        )
    ): Call<ShowEpisodes>

    companion object {
        const val QUERY_OFFSET = "offset"
        const val QUERY_LIMIT = "limit"
        val QUERY_LIMIT_DEFAULT = QUERY_LIMIT to "10"
        val QUERY_OFFSET_DEFAULT = QUERY_OFFSET to "0"
    }
}