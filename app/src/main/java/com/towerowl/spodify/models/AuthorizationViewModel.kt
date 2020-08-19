package com.towerowl.spodify.models

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.towerowl.spodify.R
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.misc.App

class AuthorizationViewModel(private val context: Context) : ViewModel() {

    companion object {
        private const val TAG = "AuthorizationViewModel"
        const val AUTHENTICATION_REQUEST_CODE = 1337
    }

    fun spotifyLogin(activity: FragmentActivity) {
        AuthenticationRequest.Builder(
            context.getString(R.string.spotify_client_id),
            AuthenticationResponse.Type.TOKEN,
            context.getString(R.string.spodify_redirect_url)
        ).setScopes(arrayOf("streaming", "user-library-read"))
            .build()
            .run {
                AuthenticationClient.openLoginActivity(
                    activity,
                    AUTHENTICATION_REQUEST_CODE,
                    this
                )
            }
    }

    fun handleAuthResponse(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onSuccess: (AuthenticationResponse) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (requestCode != AUTHENTICATION_REQUEST_CODE) return

        AuthenticationClient.getResponse(resultCode, data).run {
            when (type) {
                AuthenticationResponse.Type.TOKEN -> {
                    onSuccess(this)
                    return
                }

                AuthenticationResponse.Type.ERROR -> {
                    onError(Exception(this.error))
                    return
                }

                else -> {
                    onError(Exception("Non handled authentication type"))
                    return
                }
            }
        }
    }

    suspend fun storeToken(tokenData: TokenData) = App.instance()
        .repo
        .authenticationRepository()
        .insert(tokenData)

    suspend fun getToken(): TokenData? = App.instance()
        .repo
        .authenticationRepository()
        .getToken()

}