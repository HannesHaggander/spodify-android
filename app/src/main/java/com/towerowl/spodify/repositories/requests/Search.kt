package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.SearchResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface Search {
    @GET("search")
    fun query(@QueryMap parameters: Map<String, String>): Call<SearchResults>

    companion object {
        const val QUERY_QUERY = "q"
        const val QUERY_TYPE = "type"
        const val QUERY_MARKET = "market"
        const val QUERY_LIMIT = "limit"
        const val QUERY_OFFSET = "offset"
        const val QUERY_INCLUDE_EXTERNAL = "include_external"

        val QUERY_TYPE_DEFAULT = QUERY_TYPE to "show,episode"
        val QUERY_LIMIT_DEFAULT = QUERY_LIMIT to "5"
        val QUERY_OFFSET_DEFAULT = QUERY_OFFSET to "0"
    }
}