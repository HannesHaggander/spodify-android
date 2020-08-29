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

    lateinit var repo: RepositoryComponent
        private set

    lateinit var viewModels: ViewModelsComponent
        private set

    var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        JodaTimeAndroid.init(this)
        initiateDagger()
    }

    private fun initiateDagger() {
        with(ContentModule()) {
            DaggerViewModelsComponent.builder()
                .databaseModule(databaseModule)
                .contextModule(ContextModule(this@App))
                .contentModule(this)
                .build()
                .run { viewModels = this }

            DaggerRepositoryComponent.builder()
                .databaseModule(databaseModule)
                .contentModule(this)
                .build()
                .run { repo = this }
        }

    }
}