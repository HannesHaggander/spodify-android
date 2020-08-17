package com.towerowl.spodify.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.R
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_authentication.*

class AuthenticationFragment : Fragment() {

    companion object {
        private const val TAG = "AuthenticationFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_authentication, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    requireContext(),
                    this,
                    object : Connector.ConnectionListener {
                        override fun onFailure(error: Throwable?) {
                            Log.w(TAG, "Failed to connect", error)
                        }

                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                            Log.d(TAG, "Connected!")
                            App.instance().spotifyAppRemote = spotifyAppRemote
                                ?: throw Exception("Missing Spotify app remote")
                            findNavController().navigate(R.id.nav_goto_home)
                        }
                    })
            }
    }

}