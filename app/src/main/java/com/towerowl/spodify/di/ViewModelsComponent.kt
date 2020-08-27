package com.towerowl.spodify.di

import android.content.Context
import com.towerowl.spodify.models.AuthorizationViewModel
import com.towerowl.spodify.models.ShowViewModel
import com.towerowl.spodify.repositories.AuthenticationRepository
import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.SpotifyRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [ViewModelsModule::class])
interface ViewModelsComponent {
    @Singleton
    fun authorizationViewModel(): AuthorizationViewModel

    @Singleton
    fun showViewModel(): ShowViewModel
}

@Module(includes = [ContextModule::class, ContentModule::class])
class ViewModelsModule {

    @Volatile
    private var authorizationViewModel: AuthorizationViewModel? = null

    @Provides
    fun provideAuthorizationViewModel(
        context: Context,
        authenticationRepository: AuthenticationRepository
    ): AuthorizationViewModel = authorizationViewModel ?: synchronized(this) {
        authorizationViewModel ?: AuthorizationViewModel(context, authenticationRepository)
            .also { authorizationViewModel = it }
    }

    @Volatile
    private var showViewModel: ShowViewModel? = null

    @Provides
    fun provideShowViewModel(contentRetriever: ContentRetriever): ShowViewModel =
        showViewModel ?: ShowViewModel(contentRetriever).also { showViewModel = it }

}