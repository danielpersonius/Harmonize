package com.csci448.slittle.harmonize

import android.app.Activity
import android.util.Log
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import khttp.get
import khttp.post
import kotlinx.coroutines.*
import java.net.URLEncoder
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.min
import khttp.put
import khttp.responses.Response


class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        lateinit var ACCESS_TOKEN : String
        lateinit var USER_ID : String
        lateinit var USER_NAME : String
        private const val CLIENT_ID = "96fb37843a5e4e92a1a8c4c5168e3371"
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        private const val SPOTIFY_LOGIN_REQUEST_CODE = 1
        // can be anything really
        private const val REDIRECT_URI = "com.csci448.slittle.harmonize://callback"
        private const val CLIENT_SECRET = "84ce3a2b19c74df7900d1d6d588a14d2"

        fun getAuthenticationRequest() : AuthenticationRequest {
            val builder = AuthenticationRequest.Builder(PlatformConnectActivity.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                PlatformConnectActivity.REDIRECT_URI)
            builder.setShowDialog(true)
            builder.setScopes(arrayOf("user-read-playback-state", "user-read-private", "streaming", "user-library-modify", "playlist-modify-private", "playlist-modify-public", "user-library-read", "playlist-read-private"))
            return builder.build()
        }

        fun login(activity : Activity) {
            val builder = AuthenticationRequest.Builder(CLIENT_ID,
                                                        AuthenticationResponse.Type.TOKEN,
                                                        REDIRECT_URI)
            builder.setScopes(arrayOf("user-read-playback-state", "user-read-private", "streaming", "user-library-modify", "playlist-modify-private", "playlist-modify-public", "user-library-read", "playlist-read-private"))
            builder.setShowDialog(true)
            val request = builder.build()

            AuthenticationClient.openLoginActivity(activity, SPOTIFY_LOGIN_REQUEST_CODE, request)
//            AuthenticationClient.openLoginInBrowser(activity, request)
        }

        fun logout() : Boolean = runBlocking {
            Log.d(LOG_TAG, "logout() called")
            var logoutSuccess = false
            withContext(Dispatchers.IO) {
                val response = get("https://accounts.spotify.com")
            }
            logoutSuccess
        }

        fun getUserPlaylists(accessToken : String, limit : Int, offset : Int) : MutableList<Playlist> = runBlocking {
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
                        val memberIsPremium = result.getString("product")
                        USER_NAME = result.getString("display_name")
                        USER_ID = result.getString("id")
                    }
                    else -> {
                        Log.d(LOG_TAG, "Something went wrong: ${response.statusCode}")
                    }
                }
            }
        }

        fun getPlaylistTracks(playlistId : String) : MutableList<Track> = runBlocking {
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

        fun getTrackAudioFeatures(trackId : String) : MutableMap<String, String> = runBlocking {
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
                             buffer : Int) : MutableList<Track> = runBlocking {
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
        fun exportPlaylist(playlistName    : String,
                           trackIds        : List<String>,
                           tunedParameters : Map<String, String> = mapOf()
                          ) : String? = runBlocking {
            // beautify tuned parameters
            var tunedParametersString = "["
            for ((key, value) in tunedParameters) {
                if (value == "-1") {
                    tunedParametersString += "$key = None, "
                }
                else {
                    tunedParametersString += "$key = $value, "
                }
            }
            // remove last comma and add square bracket
            tunedParametersString = tunedParametersString.substring(0, tunedParametersString.lastIndexOf(',')) + "]"

            var playlistId : String? = null
            if (::ACCESS_TOKEN.isInitialized) {
                if (trackIds.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        // create playlist
                        val requestString = "https://api.spotify.com/v1/users/$USER_ID/playlists"
                        // data argument doesn't actually convert map to proper JSON
                        val createResponse = post(requestString,
                                                           data = JSONObject(
                                                               mapOf(
                                                                   "name" to playlistName,
                                                                   "public" to "false",
                                                                   "description" to "suggested from Harmonize with parameters:$tunedParametersString"
                                                               )
                                                           ).toString(),
                                                           headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN", "Content-Type" to "application/json"))
                        when (createResponse.statusCode) {
                            201 -> {
                                // get new playlist name and id
                                playlistId = JSONObject(createResponse.text).getString("id")
                                // add tracks
                                val uris = mutableListOf<String>()
                                for (i in 0 until trackIds.size) {
                                    uris.add("spotify:track:${trackIds[i]}")
                                }
                                val addRequestString = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
                                val addResponse = post(addRequestString,
                                                                data = JSONObject(mapOf("uris" to uris)).toString(),
                                                                headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN", "Content-Type" to "application/json"))
                                when (addResponse.statusCode) {
                                    201 -> {
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
            playlistId
        }

        // todo a bit of the last track is heard when playing a new track. tried to fix with volume control, but not working yet
        // todo change to App Remote SDK, instead of using the Web Api
        fun startPlayback(uri : String,
                          isPaused : Boolean) : Any? = runBlocking {
            val deviceId = getUserDevices()
            if (deviceId != null) {
                if (transferPlayback(deviceId) == 204) {
                    Log.d(LOG_TAG, "transfer 204")
                    // Spotify handles transferPlayback request asynchronously, so wait till playback devices activates
                    // works for now, but in some scenarios, may never be true
                    while (!getActivePlayback()) {}
                    withContext(Dispatchers.IO) {
                        var playbackResponse : Response
                        var data = ""

                        if (!isPaused) {
                            data = JSONObject(mapOf("uris" to listOf("spotify:track:$uri"))).toString()
//                            val volumeResponse = put("https://api.spotify.com/v1/me/player/volume?volume_percent=50",
//                                headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
//                            when(volumeResponse.statusCode) {
//                                204 -> Log.d(LOG_TAG, "204 volume 50")
//                                else -> Log.d(LOG_TAG, "volume response: ${volumeResponse.statusCode} ${volumeResponse.text}")
//                            }
                        }
//                        else {
//                            val volumeResponse = put("https://api.spotify.com/v1/me/player/volume?volume_percent=0",
//                                headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
//                            when(volumeResponse.statusCode) {
//                                204 -> Log.d(LOG_TAG, "204 volume 0")
//                                else -> Log.d(LOG_TAG, "volume response: ${volumeResponse.statusCode} ${volumeResponse.text}")
//                            }
//                        }

                        playbackResponse = put("https://api.spotify.com/v1/me/player/play",
                            data = data,
                            headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))

                        when(playbackResponse.statusCode) {
                            204 -> Log.d(LOG_TAG, "startPlayback 204 Success!")
                            403 -> Log.d(LOG_TAG, "startPlayback 403 forbidden! Premium required")
                            404 -> Log.d(LOG_TAG, "startPlayback 404 not found: ${playbackResponse.text}")
                            else -> Log.d(LOG_TAG, "${playbackResponse.statusCode} Something else went wrong: ${playbackResponse.text}")
                        }
                    }
                }
            }

        }

        fun getActivePlayback() : Boolean = runBlocking {
            withContext(Dispatchers.IO) {
                val response = get("https://api.spotify.com/v1/me/player",
                    headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                when(response.statusCode) {
                    200 -> {
                        val device = JSONObject(response.text).getString("device")
                        val deviceId = JSONObject(device).getString("id")
                        val isActive = JSONObject(device).getBoolean("is_active")
                        isActive
                    }
                    else -> {
                        Log.d(LOG_TAG, "getActivePlayback: ${response.statusCode} ${response.text}")
                        false
                    }
                }
            }
        }

        // todo can't use with startPlayback, since don't know which device is this mobile phone
        fun getUserDevices () : String? = runBlocking {
            var deviceId : String? = null
            withContext(Dispatchers.IO) {
                val response = get("https://api.spotify.com/v1/me/player/devices",
                                             headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                when(response.statusCode) {
                    200 -> {
                        val devices = JSONArray(JSONObject(response.text).getString("devices"))
                        Log.d(LOG_TAG, "devices: $devices")
                        // may not work when user has multiple devices
                        // i do, but only my phone shows up, so maybe it's ok
                        deviceId = JSONObject(devices[0].toString()).getString("id")
                    }
                    else -> Log.d(LOG_TAG, "get devices: ${response.statusCode} ${response.text}")
                }
            }
            deviceId
        }

        // returns the status code of the transfer request
        fun transferPlayback(deviceId : String) : Int = runBlocking {
            // PUT https://api.spotify.com/v1/me/player
            withContext(Dispatchers.IO) {
                val response = put("https://api.spotify.com/v1/me/player",
                    data = JSONObject(mapOf("device_ids" to listOf(deviceId), "play" to true)).toString(),
                    headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                response.statusCode
            }
        }
    }
}