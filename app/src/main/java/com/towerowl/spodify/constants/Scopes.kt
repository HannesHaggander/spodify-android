package com.towerowl.spodify.constants

class Scopes {
    //source: https://developer.spotify.com/documentation/general/guides/scopes/#user-library-read
    companion object {
        const val STREAMING = "streaming"
        const val USER_LIBRARY_READ = "user-library-read"
        const val PLAYLIST_MODIFY_PRIVATE = "playlist-modify-private"
        const val PLAYLIST_MODIFY_PUBLIC = "playlist-modify-public"
        const val UGC_IMAGE_UPLOAD = "ugc-image-upload"
        const val USER_READ_PLAYBACK_STATE = "user-read-playback-state"
        const val USER_READ_EMAIL = "user-read-email"
        const val PLAYLIST_READ_COLLABORATIVE = "playlist-read-collaborative"
        const val USER_MODIFY_PLAYBACK_STATE = "user-modify-playback-state"
        const val USER_READ_PRIVATE = "user-read-private"
        const val USER_LIBRARY_MODIFY = "user-library-modify"
        const val USER_TOP_READ = "user-top-read"
        const val USER_READ_PLAYBACK_POSITION = "user-read-playback-position"
        const val USER_READ_CURRENTLY_PLAYING = "user-read-currently-playing"
        const val PLAYLIST_READ_PRIVATE = "playlist-read-private"
        const val USER_FOLLOW_READ = "user-follow-read"
        const val APP_REMOTE_CONTROL = "app-remote-control"
        const val USER_READ_RECENTLY_PLAYED = "user-read-recently-played"
        const val USER_FOLLOW_MODIFY = "user-follow-modify"
    }
}