package com.towerowl.spodify.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.R
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_authentication.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationFragment : Fragment() {

    companion object {
        private const val TAG = "AuthenticationFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_authentication, container, false)

    override fun onStart() {
        super.onStart()

        if (!SpotifyAppRemote.isSpotifyInstalled(requireContext())) {
            authentication_message.text = getString(
                R.string.error_spotify_not_installed,
                getString(R.string.app_name)
            )
        } else {
            connectToSpotify()
        }
    }

    private fun connectToSpotify() {
        ConnectionParams.Builder(getString(R.string.spotify_client_id))
            .showAuthView(true)
            .setRedirectUri(getString(R.string.spodify_redirect_url))
            .build()
            .run {
                SpotifyAppRemote.connect(
                    context,
                    this,
                    object : Connector.ConnectionListener {
                        override fun onFailure(error: Throwable?) {
                            Log.w(
                                TAG,
                                "onFailure: Error connecting to spotify app",
                                Exception(error)
                            )
                        }

                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                            spotifyAppRemote?.run {
                                App.instance().spotifyAppRemote = this
                                lifecycleScope.launch(IO) {
                                    App.instance().repo.authenticationRepository().getToken()?.run {
                                        // found existing token
                                        withContext(Main) {
                                            if (isTokenValid()) {
                                                findNavController().navigate(R.id.nav_goto_home)
                                            } else {
                                                startLogin()
                                            }
                                        }
                                    } ?: withContext(Main) {
                                        // try to get new token
                                        startLogin()
                                    }
                                }


                            } ?: throw Exception("Missing Spotify app remote")
                        }
                    })
            }
    }

    private fun startLogin() = App.instance()
        .viewModels
        .authorizationViewModel()
        .spotifyLogin(requireActivity())

}