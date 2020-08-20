package com.towerowl.spodify.di

import android.content.Context
import com.towerowl.spodify.models.AuthorizationViewModel
import com.towerowl.spodify.repositories.AuthenticationRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [ViewModelsModule::class])
interface ViewModelsComponent {
    @Singleton
    fun authorizationViewModel(): AuthorizationViewModel
}

@Module(includes = [ContextModule::class, ContentModule::class])
class ViewModelsModule {

    private var authorizationViewModel: AuthorizationViewModel? = null

    @Provides
    fun provideAuthorizationViewModel(
        context: Context,
        authenticationRepository: AuthenticationRepository
    ): AuthorizationViewModel = authorizationViewModel ?: synchronized(this) {
        authorizationViewModel ?: AuthorizationViewModel(context, authenticationRepository)
            .also { authorizationViewModel = it }
    }

}