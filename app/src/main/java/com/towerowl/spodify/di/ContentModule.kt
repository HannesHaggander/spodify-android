package com.towerowl.spodify.di

import com.towerowl.spodify.data.AuthenticationDao
import com.towerowl.spodify.repositories.AuthenticationRepository
import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.SpotifyRepository
import dagger.Module
import dagger.Provides

@Module(includes = [DatabaseModule::class])
class ContentModule {

    private val spotifyRepository: SpotifyRepository by lazy { SpotifyRepository() }

    @Provides
    fun provideContentRetriever(): ContentRetriever = spotifyRepository

    @Volatile
    private var authenticationRepository: AuthenticationRepository? = null

    @Provides
    fun provideAuthenticationRepository(authenticationDao: AuthenticationDao): AuthenticationRepository =
        authenticationRepository ?: synchronized(this) {
            authenticationRepository ?: AuthenticationRepository(authenticationDao)
                .also { authenticationRepository = it }
        }

}