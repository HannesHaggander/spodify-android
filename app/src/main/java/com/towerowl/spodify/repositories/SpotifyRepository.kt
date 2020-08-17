package com.towerowl.spodify.repositories

import android.util.Log

interface ContentRetriever {
    suspend fun getPlaylists()
}

class SpotifyRepository : ContentRetriever {
    override suspend fun getPlaylists() {
        Log.d("SpotifyRepository", "getPlaylists called")
    }


}

