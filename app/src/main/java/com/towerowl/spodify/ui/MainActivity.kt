package com.towerowl.spodify.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.towerowl.spodify.R
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.ext.millisToSec
import com.towerowl.spodify.ext.secondsToReadable
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var playbackTimerJob: Job? = null
        set(value) {
            field?.cancel("Another timer started")
            field = value
        }

    private val navController by lazy { findNavController(R.id.main_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tokenObserver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        App.instance()
            .viewModels
            .authorizationViewModel()
            .run {
                handleAuthResponse(requestCode, resultCode, data,
                    onSuccess = { response ->
                        lifecycleScope.launch(IO) {
                            storeToken(TokenData.fromAuthenticationResponse(response))
                        }
                    },
                    onError = {
                        //todo
                    }
                )
            }
    }

    override fun onStart() {
        super.onStart()
        connectToSpotifyRemote()
    }

    override fun onStop() {
        super.onStop()
        App.instance().spotifyAppRemote?.run { SpotifyAppRemote.disconnect(this) }
    }

    private fun tokenObserver() {
        App.instance()
            .repo
            .authenticationRepository()
            .getTokenLiveData()
            .observe(this, { token ->
                if (token == null || !token.isTokenValid()) {
                    //token missing from database go and authenticate
                    if (navController.currentDestination?.id != R.id.nav_auth) {
                        navController.navigate(R.id.nav_auth)
                    }
                    return@observe
                }

                // token is OK to use for retrieving data
                lifecycleScope.launch(IO) {
                    App.instance().repo.spotifyRepository().setToken(token.accessToken)
                    if (navController.currentDestination?.id != R.id.nav_home) {
                        navController.navigate(R.id.action_nav_auth_to_nav_home)
                    }
                }
            })
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }

    private fun connectToSpotifyRemote() {
        ConnectionParams.Builder(getString(R.string.spotify_client_id))
            .showAuthView(true)
            .setRedirectUri(getString(R.string.spodify_redirect_url))
            .build()
            .run {
                SpotifyAppRemote.connect(
                    this@MainActivity,
                    this,
                    object : Connector.ConnectionListener {
                        override fun onFailure(error: Throwable?) {
                            Log.w(
                                TAG, "Failed to connect to spotify remote", Exception(error)
                            )
                        }

                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                            spotifyAppRemote?.run {
                                App.instance().spotifyAppRemote = this
                                this.playerApi
                                    .subscribeToPlayerState()
                                    .setEventCallback { onPlayerStateUpdate(it) }
                            } ?: throw Exception("Missing Spotify app remote")
                        }
                    })
            }
    }

    private fun onPlayerStateUpdate(playerState: PlayerState) {
        playbackTimerJob?.cancel("Player state updated")
        with(playerState.track) {
            if (!isPodcast) return
            main_current_title.text = this.name
        }

        main_currently_media_control.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (playerState.isPaused) R.drawable.ic_play else R.drawable.ic_pause
            )
        )

        main_currently_media_control.setOnClickListener {
            App.instance().spotifyAppRemote?.run {
                if (playerState.isPaused) this.playerApi.resume()
                else this.playerApi.pause()
            }
        }

        main_current_progress.max = (playerState.track.duration.millisToSec()).toInt()
        main_current_progress.progress = (playerState.playbackPosition.millisToSec()).toInt()
        if (playerState.isPaused) playbackTimerJob?.cancel("Paused")
        else startPlaybackTimer()
    }

    private fun startPlaybackTimer() {
        lifecycleScope.launch(IO) {
            while (isActive) {
                withContext(Main) {
                    main_current_progress.progress += 1
                    main_current_playback.text =
                        "${main_current_progress.progress.secondsToReadable()}/${main_current_progress.max.secondsToReadable()}"
                }
                delay(1000)
            }
        }.also { playbackTimerJob = it }
    }
}