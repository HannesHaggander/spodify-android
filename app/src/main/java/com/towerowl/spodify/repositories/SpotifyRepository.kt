package com.towerowl.spodify.repositories

import com.towerowl.spodify.data.UserData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ContentRetriever {
    fun usersProfile(): UsersProfile

    fun setToken(token: String)
}

interface BrowseContent {
    @GET("browse/categories")
    suspend fun categories()
}

interface UsersProfile {
    @GET("me")
    suspend fun me(): Call<UserData>

    @GET("users/{users_id}")
    suspend fun usersProfile(@Path("users_id") userId: String)
}

class SpotifyRepository : ContentRetriever {

    companion object {
        private const val API_VERSION = "v1"
    }

    private var token = ""

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/$API_VERSION/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient(token))
        .build()

    private fun updateRetrofit() {
        retrofit = retrofit.newBuilder().client(okHttpClient(token)).build()
    }

    private fun okHttpClient(token: String) = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
                .run { chain.proceed(this) }
        }).build()

    private val usersProfile by lazy { retrofit.create(UsersProfile::class.java) }

    override fun usersProfile(): UsersProfile = usersProfile

    override fun setToken(token: String) {
        this.token = token
        updateRetrofit()
    }

}

