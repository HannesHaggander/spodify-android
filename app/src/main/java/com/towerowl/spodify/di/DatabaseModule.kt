package com.towerowl.spodify.di

import com.towerowl.spodify.database.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(private val appDatabase: AppDatabase) {

    @Provides
    fun provideAuthenticationDao() = appDatabase.authenticationDao()

    @Provides
    fun provideQueueDao() = appDatabase.queueDao()
}