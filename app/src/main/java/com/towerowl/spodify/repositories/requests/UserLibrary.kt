package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.UsersSavedShows
import retrofit2.Call
import retrofit2.http.GET

interface UserLibrary {
    @GET("me/shows")
    fun shows(): Call<UsersSavedShows>
}