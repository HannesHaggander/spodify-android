package com.towerowl.spodify.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spotify.sdk.android.authentication.AuthenticationResponse
import org.joda.time.DateTime

@Entity(tableName = "auth_data")
data class TokenData(
    @PrimaryKey val id: String = ID,
    val accessToken: String,
    val expiresAt: DateTime,
    val createdAt: DateTime = DateTime.now()
) {
    companion object {
        const val ID = "token"

        fun fromAuthenticationResponse(authenticationResponse: AuthenticationResponse): TokenData {
            return TokenData(
                ID,
                authenticationResponse.accessToken,
                DateTime.now().plusSeconds(authenticationResponse.expiresIn),
                DateTime.now()
            )
        }


    }

    fun isTokenValid(): Boolean = expiresAt.isAfterNow

    fun isDataValid(): Boolean = isTokenValid() && accessToken.isNotEmpty() && id == ID

}