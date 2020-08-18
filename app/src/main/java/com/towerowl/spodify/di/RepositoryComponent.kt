package com.towerowl.spodify.di

import com.towerowl.spodify.data.AuthenticationDao
import com.towerowl.spodify.repositories.AuthenticationRepository
import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.SpotifyRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [ContentModule::class, DatabaseModule::class, ContextModule::class])
interface RepositoryComponent {
    @Singleton
    fun spotifyRepository(): ContentRetriever

    @Singleton
    fun authenticationRepository(): AuthenticationRepository
}

@Module
class ContentModule {

    @Provides
    fun provideContentRetriever(): ContentRetriever = SpotifyRepository()

    @Provides
    fun provideAuthenticationRepository(authenticationDao: AuthenticationDao): AuthenticationRepository =
        AuthenticationRepository(authenticationDao)

}