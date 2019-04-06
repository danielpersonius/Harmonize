package com.csci448.slittle.harmonize

import android.util.Log
import com.beust.klaxon.Klaxon
import khttp.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        fun getUserPlaylists(accessToken : String, limit : Int, offset : Int) {
            Log.d(LOG_TAG, "getUserPlaylists() called")
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    get("https://api.spotify.com/v1/me/playlists?access_token=$accessToken&limit=$limit&offset=$offset")
                // todo: error handling, since app crashes if the json response cannot map to an ApiPlaylistData object
                // parse JSON
                val result = Klaxon()
                    .parse<ApiPlaylistData>(response.text)
                Log.d(LOG_TAG, result.toString())
            }
        }
    }
}