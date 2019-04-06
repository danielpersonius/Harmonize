package com.csci448.slittle.harmonize

import android.util.Log
import android.widget.Toast
import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Klaxon
import khttp.get
import kotlinx.coroutines.*

class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        lateinit var ACCESS_TOKEN : String

        fun getUserPlaylists(accessToken : String, limit : Int, offset : Int) : Any? = runBlocking {
            Log.d(LOG_TAG, "getUserPlaylists() called")

            withContext(Dispatchers.IO) {
                val response =
                    get("https://api.spotify.com/v1/me/playlists?access_token=$accessToken&limit=$limit&offset=$offset")
                try {
                    val result = Klaxon()
                        .parse<ApiPlaylistData>(response.text)
                    result
                }
                catch (e: JsonParsingException) {
                    Log.d(LOG_TAG, "Could not parse json: ${e.message}")
                    null
                }
            }
        }

        fun getUserInformation(userId : String) {
            // todo Implement me
        }

        fun getPlaylistTracks(playlistId : String) : Any? = runBlocking {
            Log.d(LOG_TAG, "getPlaylistTracks() called")

            withContext(Dispatchers.IO) {
                try {
                    val response =
                        get("https://api.spotify.com/v1/playlists/$playlistId/tracks?fields=items(track)", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN")) //access_token=$ACCESS_TOKEN
                    response.text
                }
                catch (e: Exception) {
                    Log.d(LOG_TAG, e.message)
                    null
                }
//                val result = Klaxon()
//                    .parse<ApiPlaylistData>(response.text)
//                Log.d(LOG_TAG, result.toString())
            }
        }

        fun getTrackAudioFeatures(trackId : String) {
            Log.d(LOG_TAG, "getTrackAudioFeatures() called")
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    get("GET https://api.spotify.com/v1/audio-features/$trackId?access_token=$ACCESS_TOKEN")
                Log.d(LOG_TAG, "audio features: ${response.text}")
//                val result = Klaxon()
//                    .parse<ApiPlaylistData>(response.text)
//                Log.d(LOG_TAG, result.toString())
            }
        }

        fun generatePlaylist(sourcePlaylistName : String,
                             artist_similarity : Int,
                             energy : Int,
                             danceability : Int,
                             speechiness : Int,
                             loudness : Int,
                             valence : Int) {
            Log.d(LOG_TAG, "generatePlaylist() called")
            CoroutineScope(Dispatchers.IO).launch {
//                val response =
//                    get("https://api.spotify.com/v1/me/playlists?access_token=$ACCESS_TOKEN")
//                // todo: error handling, since app crashes if the json response cannot map to an ApiPlaylistData object
//                // parse JSON into playlist objects
//                val result = Klaxon()
//                    .parse<ApiPlaylistData>(response.text)
//                Log.d(LOG_TAG, result.toString())
            }
        }
    }


}