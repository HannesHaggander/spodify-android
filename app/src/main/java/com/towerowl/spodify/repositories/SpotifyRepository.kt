package com.towerowl.spodify.repositories

import com.towerowl.spodify.data.api.Category
import com.towerowl.spodify.data.api.UserData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

interface ContentRetriever {
    fun usersProfile(): UsersProfile

    fun browse(): Browse

    fun setToken(token: String)
}

interface Browse {
    @GET("browse/categories/{category_id}")
    fun categories(@Path("category_id") categoryId: String): Call<Category>
}

interface UsersProfile {
    @GET("me")
    fun me(): Call<UserData>

    @GET("users/{users_id}")
    fun usersProfile(@Path("users_id") userId: String)
}

class SpotifyRepository : ContentRetriever {

    val id = UUID.randomUUID()

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
        const val BASE_URL = "https://api.spotify.com/v1/"
    }

    private lateinit var retrofit: Retrofit

    private fun okHttpClient(token: String) = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response = chain.request()
                .newBuilder()
                .addHeader(AUTHORIZATION, "$BEARER $token")
                .build()
                .run { chain.proceed(this) }
        }).build()

    override fun usersProfile(): UsersProfile = retrofit.create(UsersProfile::class.java)

    override fun browse(): Browse = retrofit.create(Browse::class.java)

    override fun setToken(token: String) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient(token))
        .build()
        .run { retrofit = this }

}

