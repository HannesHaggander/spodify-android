package com.towerowl.spodify.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spotify.sdk.android.authentication.AuthenticationResponse
import org.joda.time.DateTime

@Entity(tableName = "auth_data")
data class TokenData(
    @PrimaryKey val accessToken: String,
    val expiresAt: DateTime,
    val createdAt: DateTime = DateTime.now()
) {
    companion object {
        fun fromAuthenticationResponse(authenticationResponse: AuthenticationResponse): TokenData {
            return TokenData(
                authenticationResponse.accessToken,
                DateTime.now().plusSeconds(authenticationResponse.expiresIn),
                DateTime.now()
            )
        }
    }

    fun isTokenValid(): Boolean = expiresAt.isAfterNow

}