package com.csci448.slittle.harmonize

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.Json
import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Klaxon
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import khttp.get
import khttp.post
import kotlinx.coroutines.*
import java.net.URLEncoder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONStringer
import kotlin.math.min
import kotlin.js.*
import android.content.pm.PackageManager



class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        lateinit var ACCESS_TOKEN : String
        lateinit var USER_ID : String
        private const val CLIENT_ID = "96fb37843a5e4e92a1a8c4c5168e3371"
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        private const val SPOTIFY_LOGIN_REQUEST_CODE = 1
        // can be anything really
        private const val REDIRECT_URI = "com.csci448.slittle.harmonize://callback"
        private const val CLIENT_SECRET = "84ce3a2b19c74df7900d1d6d588a14d2"

        fun login(activity : Activity) {
            val builder = AuthenticationRequest.Builder(CLIENT_ID,
                                                        AuthenticationResponse.Type.TOKEN,
                                                        REDIRECT_URI)
            builder.setScopes(arrayOf("user-read-private", "streaming", "user-library-modify", "playlist-modify-private", "playlist-modify-public", "user-library-read", "playlist-read-private"))
            builder.setShowDialog(true)
            val request = builder.build()

            AuthenticationClient.openLoginActivity(activity, SPOTIFY_LOGIN_REQUEST_CODE, request)
//            AuthenticationClient.openLoginInBrowser(activity, request)
        }

        fun logout() : Any? = runBlocking {
            Log.d(LOG_TAG, "logout() called")
            var logoutSuccess = false
            withContext(Dispatchers.IO) {
                val response = get("https://accounts.spotify.com")
            }
            logoutSuccess
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

        // todo raise error/dialog if user not premium
        fun getUserInformation() : Any? = runBlocking {
            withContext(Dispatchers.IO) {
                val response =
                    get("https://api.spotify.com/v1/me", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                when (response.statusCode) {
                    200 -> {
                        val result = JSONObject(response.text)
//                        Log.d(LOG_TAG, result.toString())
                        val memberIsPremium = result.getString("product")
                        val displayName = result.getString("display_name")
                        USER_ID = result.getString("id")
                    }
                    else -> {
                        Log.d(LOG_TAG, "Something went wrong: ${response.statusCode}")
                    }
                }
            }
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
                    val trackId = trackData.getString("id")
                    val trackName = trackData.getString("name")
                    val albumName = JSONObject(trackData.getString("album")).getString("name")
                    val artists = JSONArray(trackData.getString("artists"))
                    val artistNames = mutableListOf<String>()
                    val artistIds = mutableListOf<String>()
                    for(j in 0 until artists.length()) {
                        val artist = JSONObject(artists[j].toString())
                        artistNames.add(artist.getString("name"))
                        artistIds.add(artist.getString("id"))
                    }

                    tracks.add(Track(trackId, trackName, artistNames, artistIds, albumName))
                }

                tracks
            }
        }

        fun getTrackAudioAnalysis(trackId : String) {
            // todo implement me
        }

        fun getTrackAudioFeatures(trackId : String) : Any? = runBlocking {
            Log.d(LOG_TAG, "getTrackAudioFeatures() called")

            val audioFeatures = mutableMapOf<String, String>()
            if (::ACCESS_TOKEN.isInitialized) {
                withContext(Dispatchers.IO) {
                    val response =
                        get("https://api.spotify.com/v1/audio-features/$trackId", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                    val result = JSONObject(response.text)
                    audioFeatures["danceability"]     = result.getString("danceability")
                    audioFeatures["energy"]           = result.getString("energy")
                    audioFeatures["loudness"]         = result.getString("loudness")
                    audioFeatures["speechiness"]      = result.getString("speechiness")
                    audioFeatures["acousticness"]     = result.getString("acousticness")
                    audioFeatures["instrumentalness"] = result.getString("instrumentalness")
                    audioFeatures["liveness"]         = result.getString("liveness")
                    audioFeatures["valence"]          = result.getString("valence")
                    audioFeatures["tempo"]            = result.getString("tempo")
                    audioFeatures["time_signature"]   = result.getString("time_signature")
                }
            }
            audioFeatures
        }

        // todo vary feature buffer if no results
        // todo maybe make call for each artist, not just 5. will have to see if fast enough
        // possible flow: search by each artist, get all tracks, filter duplicates across all artists

        fun generatePlaylist(sourceId : String,
                             seedArtists : List<String>,
                             seedGenres : List<String>,
                             seedTracks : List<String>,
                             limit : Int,
                             artist_similarity : Int,
                             danceability : Int = -1,
                             energy : Int = -1,
                             speechiness : Int,
                             loudness : Int,
                             valence : Int,
                             buffer : Int) : Any? = runBlocking {
            Log.d(LOG_TAG, "generatePlaylist($danceability, $energy, $speechiness, $loudness, $valence, $buffer) called")
            val suggestedTracks = mutableListOf<Track>()

            if (::ACCESS_TOKEN.isInitialized) {
                withContext(Dispatchers.IO) {
                    var requestString = "https://api.spotify.com/v1/recommendations?limit=$limit"

                    var seedArtistsString = ""
                    // todo - limited to 5 seed values, but will find a workaround
                    for (i in 0 until min(5,seedArtists.size)) {
                        seedArtistsString += seedArtists[i]
                        if (i < seedArtists.size-1) {
                            // url encoded space
                            seedArtistsString += "%2C"
                        }
                    }
                    requestString += "&seed_artists=$seedArtistsString"

                    // same for genres and tracks

                    if (danceability > -1) {
                    requestString += "&min_danceability=${danceability-buffer}&max_danceability=${danceability+buffer}"
                    }
                    if (energy > -1) {
                        requestString += "&min_energy=${energy-buffer}&max_energy=${energy+buffer}"
                    }
                    if (speechiness > -1) {
                        requestString += "&min_speechiness=${danceability-buffer}&max_speechiness=${speechiness+buffer}"
                    }
                    if (loudness > -1) {
                        requestString += "&min_loudness=${loudness-buffer}&max_loudness=${loudness+buffer}"
                    }
                    if (valence > -1) {
                        requestString += "&min_valence=${valence-buffer}&max_valence=${valence+buffer}"
                    }

                    val response = get(requestString, headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                    when(response.statusCode) {
                        200 -> {
                            val result = JSONObject(response.text)
                            val tracks = JSONArray(result.getString("tracks"))
                            for(i in 0 until tracks.length()) {
                                val track = JSONObject(tracks[i].toString())
                                val trackId = track.getString("id")
                                val trackName = track.getString("name")
                                val albumName = JSONObject(track.getString("album")).getString("name")
                                val artists = JSONArray(track.getString("artists"))
                                val artistNames = mutableListOf<String>()
                                val artistIds = mutableListOf<String>()
                                for(j in 0 until artists.length()) {
                                    val artist = JSONObject(artists[j].toString())
                                    artistNames.add(artist.getString("name"))
                                    artistIds.add(artist.getString("id"))
                                }
                                suggestedTracks.add(Track(trackId, trackName, artistNames, artistIds, albumName))
                            }
                        }
                        400 -> Log.d(LOG_TAG, "bad request")
                        else -> Log.d(LOG_TAG, response.statusCode.toString())
                    }
                }
            }
            suggestedTracks
        }

        // todo add options, like collaborative, public, etc. as parameters
        // returns a nullable String
        fun exportPlaylist(playlistName : String,
                           trackIds     : List<String>
                          ) : Any? = runBlocking {
            // true if playlist both created and tracks added
//            var success = false
            var playlistId : String? = null
            if (::ACCESS_TOKEN.isInitialized) {
                if (trackIds.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        // create playlist
                        val requestString = "https://api.spotify.com/v1/users/$USER_ID/playlists"
                        // data argument doesn't actually convert map to proper JSON
                        val createResponse = post(requestString,
                                                           data = JSONObject(mapOf("name" to playlistName, "public" to "false", "description" to "test description")).toString(),
                                                           headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN", "Content-Type" to "application/json"))
                        when (createResponse.statusCode) {
                            201 -> {
                                // get new playlist name and id
                                playlistId = JSONObject(createResponse.text).getString("id")
                                // add tracks
                                val uris = mutableListOf<String>() //                                var addRequestString = "https://api.spotify.com/v1/playlists/$playlistId/tracks?uris="
                                for (i in 0 until trackIds.size) { //
                                    uris.add("spotify:track:${trackIds[i]}")
                                }
                                val addRequestString = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
                                val addResponse = post(addRequestString,
                                                                data = JSONObject(mapOf("uris" to uris)).toString(),
                                                                headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN", "Content-Type" to "application/json"))
                                when (addResponse.statusCode) {
                                    201 -> {
//                                        success = true
//                                        val snapshotId = JSONObject(addResponse.text).getString("snapshot_id")
                                    }
                                    else -> Log.d(LOG_TAG, "Something went wrong when adding tracks: ${addResponse.text}")
                                }
                            }
                            else -> {
                                Log.d(LOG_TAG, "Something went wrong creating a new playlist: ${createResponse.text}")
                            }
                        }

                    }
                }
            }
//            success
            playlistId
        }

//        fun isSpotifyInstalled() {
//            val pm = getActivity().getPackageManager()
//            var isSpotifyInstalled: Boolean
//            try {
//                pm.getPackageInfo("com.spotify.music", 0)
//                isSpotifyInstalled = true
//            } catch (e: PackageManager.NameNotFoundException) {
//                isSpotifyInstalled = false
//            }
//
//        }
    }
}