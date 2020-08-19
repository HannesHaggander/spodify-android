package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.UserData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface UsersProfile {
    @GET("me")
    fun me(): Call<UserData>

    @GET("users/{users_id}")
    fun usersProfile(@Path("users_id") userId: String)
}