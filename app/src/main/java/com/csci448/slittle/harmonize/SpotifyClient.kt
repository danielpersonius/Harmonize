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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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
                val playlists = mutableListOf<Playlist>()
                val response =
                    get("https://api.spotify.com/v1/me/playlists?access_token=$accessToken&limit=$limit&offset=$offset")
                val result = JSONObject(response.text)
                val p = JSONArray(result.getString("items"))
                for(i in 0 until p.length()-1) {
                    val playlistData = JSONObject(p[i].toString())
                    val href = playlistData.getString("href")
                    val id = playlistData.getString("id")
                    val name = playlistData.getString("name")
                    val collaborative = playlistData.getBoolean("collaborative")
                    val owner = playlistData.getString("owner")
                    val public = playlistData.getBoolean("public")
                    val type = playlistData.getString("type")
                    val uri = playlistData.getString("uri")
                    playlists.add(
                        Playlist(href, id, name, collaborative, owner, public, type, uri, null)
                    )
                }
                playlists
            }
        }

        fun getUserInformation(userId : String) {
            // todo Implement me
        }

        fun getPlaylistTracks(playlistId : String) : Any? = runBlocking {
            Log.d(LOG_TAG, "getPlaylistTracks() called")
            val items = URLEncoder.encode("items(track(album, artists, id, name, type))", "utf-8")

            withContext(Dispatchers.IO) {
                val tracks = mutableListOf<Track>()
                val response =
                        get("https://api.spotify.com/v1/playlists/$playlistId/tracks?fields=$items", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                val result = JSONObject(response.text)
                val itemsData = JSONArray(result.getString("items"))
                for(i in 0 until itemsData.length()) {
                    val trackData = JSONObject(JSONObject(itemsData[i].toString()).getString("track"))
                    val trackName = trackData.getString("name")
                    val albumName = JSONObject(trackData.getString("album")).getString("name")
                    val artists = JSONArray(trackData.getString("artists"))
                    val artistNames = mutableListOf<String>()
                    for(j in 0 until artists.length()) {
                        val artist = JSONObject(artists[j].toString())
                        artistNames.add(artist.getString("name"))
                    }

                    tracks.add(Track(trackName, artistNames, albumName))
                }

                tracks
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