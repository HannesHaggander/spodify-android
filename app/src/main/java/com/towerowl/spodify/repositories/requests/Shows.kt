package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.ShowEpisodes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Shows {
    @GET("shows/{id}/episodes")
    fun shows(@Path("id") showId: String): Call<ShowEpisodes>
}