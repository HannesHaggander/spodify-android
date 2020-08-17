package com.towerowl.spodify.misc

import android.app.Application
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.di.DaggerRepositoryComponent
import com.towerowl.spodify.di.RepositoryComponent

class App : Application() {

    companion object {
        @Volatile
        private var mInstance: App? = null

        fun instance(): App =
            mInstance ?: synchronized(this) { mInstance ?: App().also { mInstance = it } }

    }

    lateinit var spotifyAppRemote: SpotifyAppRemote

    val repo: RepositoryComponent by lazy { DaggerRepositoryComponent.create() }

}