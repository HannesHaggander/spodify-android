package com.towerowl.spodify.di

import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.SpotifyRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [ContentModule::class])
interface RepositoryComponent {
    @Singleton
    fun spotifyRepository(): ContentRetriever
}

@Module
class ContentModule {

    @Provides
    fun provideContentRetriever(): ContentRetriever = SpotifyRepository()

}