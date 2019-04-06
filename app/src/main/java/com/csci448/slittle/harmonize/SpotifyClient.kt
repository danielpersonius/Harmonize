package com.csci448.slittle.harmonize

import android.app.Activity
import android.util.Log
import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Klaxon
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import khttp.get
import kotlinx.coroutines.*
import java.net.URLEncoder

class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        lateinit var ACCESS_TOKEN : String

        private fun login(activity : Activity) {
            val builder = AuthenticationRequest.Builder(PlatformConnectActivity.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                PlatformConnectActivity.REDIRECT_URI)
            builder.setScopes(arrayOf("streaming", "user-library-read", "playlist-read-private"))
            builder.setShowDialog(true)
            val request = builder.build()

            AuthenticationClient.openLoginActivity(activity, PlatformConnectActivity.SPOTIFY_LOGIN_REQUEST_CODE, request)
        }

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
            val items = URLEncoder.encode("items(track(artists, id, name, type))", "utf-8")

            withContext(Dispatchers.IO) {
                try {
                    val response =
                        get("https://api.spotify.com/v1/playlists/$playlistId/tracks?fields=$items", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))

                    // get rid of top level json key to avoid extra data classes
                    val trimmedResponse = response.text.substring(response.text.indexOf("[") + 1, response.text.lastIndexOf("]")) + "}"

                    val result = Klaxon()
                    .parse<ApiTrack>(response.text)
                    result
                }
                catch (e: Exception) {
                    Log.d(LOG_TAG, "parsing error: ${e.message}")
                    null
                }
            }
        }

        fun getTrackAudioFeatures(trackId : String) {
            Log.d(LOG_TAG, "getTrackAudioFeatures() called")
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    get("GET https://api.spotify.com/v1/audio-features/$trackId?access_token=$ACCESS_TOKEN")
                Log.d(LOG_TAG, "audio features: ${response.text}")
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
            // todo implement me
        }
    }


}