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

    val repo: RepositoryComponent by lazy {
        DaggerRepositoryComponent.builder()
            .databaseModule(DatabaseModule(AppDatabase.create(this)))
            .build()
    }

    val viewModels: ViewModelsComponent by lazy {
        DaggerViewModelsComponent.builder()
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