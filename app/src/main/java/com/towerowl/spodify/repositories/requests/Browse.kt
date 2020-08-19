package com.towerowl.spodify.repositories.requests

import com.towerowl.spodify.data.api.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Browse {
    @GET("browse/categories/{category_id}")
    fun categories(@Path("category_id") categoryId: String): Call<Category>
}