package com.towerowl.spodify.repositories

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.towerowl.spodify.data.api.SearchResults
import com.towerowl.spodify.repositories.requests.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface ContentRetriever {
    fun usersProfile(): UsersProfile

    fun browse(): Browse

    fun userLibrary(): UserLibrary

    fun shows(): Shows

    fun setToken(token: String)

    fun search(): Search

}

class SpotifyRepository : ContentRetriever {

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

    override fun userLibrary(): UserLibrary = retrofit.create(UserLibrary::class.java)

    override fun shows(): Shows = retrofit.create(Shows::class.java)

    override fun search(): Search = retrofit.create(Search::class.java)

    override fun setToken(token: String) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())
        )
        .client(okHttpClient(token))
        .build()
        .run { retrofit = this }

}

