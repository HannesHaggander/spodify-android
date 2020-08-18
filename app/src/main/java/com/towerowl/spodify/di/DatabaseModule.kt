package com.towerowl.spodify.di

import android.content.Context
import androidx.room.Room
import com.towerowl.spodify.database.AppDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class DatabaseModule {

    private var appDatabase: AppDatabase? = null

    private fun getRoom(context: Context): AppDatabase = appDatabase ?: synchronized(this) {
        appDatabase ?: Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build().also { appDatabase = it }
    }

    @Provides
    fun provideAuthenticationDao(context: Context) = getRoom(context).authenticationDao()
}