package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.towerowl.spodify.R
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_authentication.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AuthenticationFragment : Fragment() {

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
        }

        lifecycleScope.launch(IO) {
            with(App.instance().viewModels.authorizationViewModel()) {
                if (getToken() == null) {
                    spotifyLogin(requireActivity())
                }
            }
        }
    }

}