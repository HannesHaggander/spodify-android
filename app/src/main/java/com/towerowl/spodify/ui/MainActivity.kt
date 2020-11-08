package com.towerowl.spodify.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.towerowl.spodify.R
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.ext.asVisibility
import com.towerowl.spodify.ext.millisToSec
import com.towerowl.spodify.ext.secondsToReadable
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private var playbackTimerJob: Job? = null
        set(value) {
            field?.cancel("Another timer started")
            field = value
        }

    private var previousNavigation: NavDestination? = null

    private val mainNavController by lazy { findNavController(R.id.main_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tokenObserver()
        setupAppNavigation()
        main_progress_container.setOnClickListener {
            mainNavController.navigate(R.id.nav_player_detail)
        }
    }

    private fun setupAppNavigation() {
        main_bottom_navigation.setupWithNavController(mainNavController)
        mainNavController.addOnDestinationChangedListener { navController, destination, _ ->
            if (previousNavigation?.id == destination.id) return@addOnDestinationChangedListener
            lifecycleScope.launch(Main) {
                main_bottom_navigation.visibility =
                    (destination.id != R.id.nav_auth).asVisibility()
                main_progress_container.visibility =
                    (destination.id != R.id.nav_player_detail).asVisibility()
            }
            previousNavigation = navController.currentDestination
        }
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
            .observe(this) { token ->
                if (token == null || !token.isTokenValid()) {
                    //token missing from database go and authenticate
                    if (mainNavController.currentDestination?.id != R.id.nav_auth) {
                        mainNavController.navigate(R.id.nav_auth)
                    }
                    return@observe
                }

                // token is OK to use for retrieving data
                lifecycleScope.launch(IO) {
                    App.instance().repo.spotifyRepository().setToken(token.accessToken)
                    if (mainNavController.currentDestination?.id != R.id.nav_home) {
                        mainNavController.navigate(R.id.action_nav_auth_to_nav_home)
                    }
                }
            }
    }

    override fun onBackPressed() {
        if (!mainNavController.popBackStack()) {
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
            if (mainNavController.currentDestination?.id != R.id.nav_player_detail)
                main_progress_container.visibility = isPodcast.asVisibility()
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
                    main_current_progress.progress += PROGRESS_INCREMENT
                    main_current_playback.text =
                        "${main_current_progress.progress.secondsToReadable()}/${main_current_progress.max.secondsToReadable()}"
                }
                delay(SECOND_DELAY)
            }
        }.also { playbackTimerJob = it }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PROGRESS_INCREMENT = 1
        private const val SECOND_DELAY = 1000L
    }
}