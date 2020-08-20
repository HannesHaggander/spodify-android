package com.towerowl.spodify.misc

import android.app.Application
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.database.AppDatabase
import com.towerowl.spodify.di.*
import net.danlew.android.joda.JodaTimeAndroid

class App : Application() {

    companion object {
        @Volatile
        private var mInstance: App? = null

        fun instance(): App = mInstance ?: throw Exception("Instance not instantiated")
    }

    private val databaseModule by lazy { DatabaseModule(AppDatabase.create(this)) }

    val repo: RepositoryComponent by lazy {
        DaggerRepositoryComponent.builder()
            .databaseModule(databaseModule)
            .build()
    }

    val viewModels: ViewModelsComponent by lazy {
        DaggerViewModelsComponent.builder()
            .databaseModule(databaseModule)
            .contextModule(ContextModule(this))
            .build()
    }

    lateinit var spotifyAppRemote: SpotifyAppRemote

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        JodaTimeAndroid.init(this)
    }
}