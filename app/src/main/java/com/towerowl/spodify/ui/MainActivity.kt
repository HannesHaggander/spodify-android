package com.towerowl.spodify.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.R
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.misc.App
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.main_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectToSpotifyRemote()
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
                        navController.navigate(R.id.nav_goto_home)
                    }
                }
            })
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
                        }

                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                            spotifyAppRemote?.run {
                                App.instance().spotifyAppRemote = this
                            } ?: throw Exception("Missing Spotify app remote")
                        }
                    })
            }
    }
}