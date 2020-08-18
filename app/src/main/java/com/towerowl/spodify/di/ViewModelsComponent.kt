package com.towerowl.spodify.di

import android.content.Context
import com.towerowl.spodify.models.AuthorizationViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [ViewModelsModule::class, ContextModule::class])
interface ViewModelsComponent {
    @Singleton
    fun authorizationViewModel(): AuthorizationViewModel
}

@Module(includes = [ContextModule::class])
class ViewModelsModule {

    @Provides
    fun provideAuthorizationViewModel(context: Context): AuthorizationViewModel =
        AuthorizationViewModel(context)

}