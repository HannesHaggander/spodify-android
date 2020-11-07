package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.UsersSavedShows
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface UserLibrary {
    @GET("me/shows")
    fun shows(): Call<UsersSavedShows>
}