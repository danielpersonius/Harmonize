package com.csci448.slittle.harmonize

import android.app.Activity
import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Base64
import android.util.Log
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import khttp.get
import khttp.post
import khttp.put
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.math.min

class SpotifyClient {
    companion object {
        private const val LOG_TAG = "SpotifyClient"
        lateinit var ACCESS_TOKEN : String
        lateinit var USER_ID : String
        lateinit var USER_NAME : String
        lateinit var REFRESH_TOKEN : String
        private var LOGGED_IN = false
        var HAS_PREMIUM = false
        private const val CLIENT_ID = "96fb37843a5e4e92a1a8c4c5168e3371"
        // can be anything really
        private const val REDIRECT_URI = "com.csci448.slittle.harmonize://callback"
        private const val CLIENT_SECRET = "84ce3a2b19c74df7900d1d6d588a14d2"

        /**
         * since ::isInitialized can't be used outside of this class
         */
        fun accessTokenIsInitialized() : Boolean {
            return ::ACCESS_TOKEN.isInitialized
        }
        fun refreshTokenIsInitialized() : Boolean {
            return ::REFRESH_TOKEN.isInitialized
        }

        fun userIsLoggedIn() : Boolean {
            return LOGGED_IN
        }

        fun setUserIsLoggedIn(userIsLoggedIn : Boolean) {
            LOGGED_IN = userIsLoggedIn
        }
        /**
         * gets a new access token by either getting a new refresh token
         * or using existing refresh token
         *
         * @param getNewRefreshToken - true if have auth code and need refresh token, false if have refresh token
         * @param authCode - the auth code needed to get new refresh token, if needed
         */
        fun authorize(getNewRefreshToken : Boolean, authCode : String? = null) : Any? = runBlocking {
            val requestData : String
            if (getNewRefreshToken) {
                requestData = "grant_type=authorization_code&code=$authCode&redirect_uri=$REDIRECT_URI"
            }
            else {
                if (!::REFRESH_TOKEN.isInitialized) {
                    getUserFromDb()
                }
                requestData = "grant_type=refresh_token&refresh_token=$REFRESH_TOKEN"
            }
            withContext(Dispatchers.IO) {
                val response = post("https://accounts.spotify.com/api/token",
                    data = requestData,
                    headers = mapOf(
                        "Content-Type"  to "application/x-www-form-urlencoded",
                        "Authorization" to "Basic ${Base64.encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray(), Base64.NO_WRAP).trim()}".trim()
                    )
                )
                when(response.statusCode) {
                    200 -> {
                        val result = JSONObject(response.text)
                        ACCESS_TOKEN = result.getString("access_token")
                        if (getNewRefreshToken) {
                            REFRESH_TOKEN = result.getString("refresh_token")
                        } else {} // needed for some reason, even though it's empty

                        // no user found, so retrieve data and store it
                        if (!getUserFromDb()) {
                            getUserInformation()
                            storeSpotifyUser(USER_ID, USER_NAME, REFRESH_TOKEN, HAS_PREMIUM)
                        } else {}
                    }
                    else ->
                        Log.d(LOG_TAG, "${response.statusCode} ${response.text}")
                }
            }

        }

        fun getAuthenticationRequest() : AuthenticationRequest {
            val builder = AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.CODE,
                REDIRECT_URI)
            builder.setShowDialog(true)
            builder.setScopes(arrayOf("user-read-playback-state", "user-read-private", "streaming", "user-library-modify", "playlist-modify-private", "playlist-modify-public", "user-library-read", "playlist-read-private"))
            return builder.build()
        }

        fun getUserPlaylists(accessToken : String, limit : Int, offset : Int) : MutableList<Playlist> = runBlocking {
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
                        Playlist(null, href, id, name, collaborative, owner, public, type, uri, null)
                    )
                }
                playlists
            }
        }

        private fun getUserInformation() : Any? = runBlocking {
            withContext(Dispatchers.IO) {
                val response =
                    get("https://api.spotify.com/v1/me", headers=mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                when (response.statusCode) {
                    200 -> {
                        val result = JSONObject(response.text)
                        HAS_PREMIUM = result.getString("product") == "premium"
                        USER_NAME = result.getString("display_name")
                        USER_ID = result.getString("id")
                    }
                    else -> {
                        Log.d(LOG_TAG, "Something went wrong: ${response.statusCode} ${response.text}")
                    }
                }
            }
        }

        fun getPlaylistTracks(playlistId : String) : MutableList<Track> = runBlocking {
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

        fun getTrackAudioFeatures(trackId : String, activity: Activity) : MutableMap<String, String> = runBlocking {
            Log.d(LOG_TAG, "getTrackAudioFeatures() called")
            val audioFeatures = mutableMapOf<String, String>()

            if (!::ACCESS_TOKEN.isInitialized) {
                authorize(false)
            }
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
            val suggestedTracks = mutableListOf<Track>()

            if (!::ACCESS_TOKEN.isInitialized) {
                authorize(false)
            }
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
            suggestedTracks
        }

        // todo add options, like collaborative, public, etc. as parameters
        // returns a nullable String
        fun exportPlaylist(playlistName    : String,
                           trackIds        : List<String>,
                           tunedParameters : Map<String, String> = mapOf()
                          ) : Pair<String?, String?>? = runBlocking {
            // beautify tuned parameters
            var tunedParametersString = "["
            // for now, tuned parameters may be null
            if (tunedParameters.isNotEmpty()) {
                for ((key, value) in tunedParameters) {
                    if (value == "-1") {
                        tunedParametersString += "$key = None, "
                    }
                    else {
                        tunedParametersString += "$key = $value, "
                    }
                }
                // remove last comma and add square bracket
                tunedParametersString = tunedParametersString.substring(0, tunedParametersString.lastIndexOf(','))
            }
            tunedParametersString += "]"

            var playlistId : String? = null
            var playlistHref : String? = null
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
                                // get new playlist id and href
                                playlistId   = JSONObject(createResponse.text).getString("id")
                                playlistHref = JSONObject(createResponse.text).getString("href")
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
                                        val snapshotId = JSONObject(addResponse.text).getString("snapshot_id")
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
            Pair(playlistId, playlistHref)
        }

        // todo - a bit of the last track is heard when playing a new track. tried to fix with volume control, but not working yet
        // todo - change to App Remote SDK, instead of using the Web Api
        // todo - send message back to playlist view if not premium or some other error occured
        fun startPlayback(uri : String,
                          isPaused : Boolean) : Boolean? = runBlocking {
            var playbackSuccess = false
            if (HAS_PREMIUM) {
                val deviceId = getUserDevices()
                if (deviceId != null) {
                    if (transferPlayback(deviceId) == 204) {
                        // Spotify handles transferPlayback request asynchronously, so wait till playback devices activates
                        // works for now, but in some scenarios, may never be true
                        while (!getActivePlayback()) {}
                        withContext(Dispatchers.IO) {
                            var data = ""

                            if (!isPaused) {
                                data = JSONObject(mapOf("uris" to listOf("spotify:track:$uri"))).toString()
                            }

                            val playbackResponse = put("https://api.spotify.com/v1/me/player/play",
                                data = data,
                                headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))

                            when(playbackResponse.statusCode) {
                                204  -> playbackSuccess = true
                                403  -> Log.d(LOG_TAG, "startPlayback 403 forbidden! Premium required")
                                404  -> Log.d(LOG_TAG, "startPlayback 404 not found: ${playbackResponse.text}")
                                else -> Log.d(LOG_TAG, "${playbackResponse.statusCode} Something else went wrong: ${playbackResponse.text}")
                            }
                        }
                    }
                }
            }
            playbackSuccess
        }

        private fun getActivePlayback() : Boolean = runBlocking {
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
        private fun getUserDevices() : String? = runBlocking {
            var deviceId : String? = null
            withContext(Dispatchers.IO) {
                val response = get("https://api.spotify.com/v1/me/player/devices",
                                             headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                when(response.statusCode) {
                    200 -> {
                        val devices = JSONArray(JSONObject(response.text).getString("devices"))
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
        private fun transferPlayback(deviceId : String) : Int = runBlocking {
            withContext(Dispatchers.IO) {
                val response = put("https://api.spotify.com/v1/me/player",
                    data = JSONObject(mapOf("device_ids" to listOf(deviceId), "play" to true)).toString(),
                    headers = mapOf("Authorization" to "Bearer $ACCESS_TOKEN"))
                response.statusCode
            }
        }

        private fun storeSpotifyUser(userId : String,
                                     userName : String,
                                     refreshToken: String,
                                     hasPremium : Boolean) {
            // Gets the data repository in write mode
            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(SpotifyReaderContract.UserEntry.USER_ID, userId)
                put(SpotifyReaderContract.UserEntry.USER_NAME, userName)
                put(SpotifyReaderContract.UserEntry.PLATFORM, "Spotify")
                put(SpotifyReaderContract.UserEntry.REFRESH_TOKEN, refreshToken)
                put(SpotifyReaderContract.UserEntry.HAS_PREMIUM, hasPremium)
            }

            // Insert the new row, returning the primary key value of the new row
            val newRowId = DbInstance.writableDb.insert(SpotifyReaderContract.UserEntry.TABLE_NAME, null, values)
        }

        // todo reduce or eliminate side effects
        private fun getUserFromDb() : Boolean {
            var userId : String? = null
            var userName : String? = null
            var hasPremium : Int? = null
            val projection = arrayOf(
                SpotifyReaderContract.UserEntry.USER_ID,
                SpotifyReaderContract.UserEntry.USER_NAME,
                SpotifyReaderContract.UserEntry.REFRESH_TOKEN,
                SpotifyReaderContract.UserEntry.HAS_PREMIUM
            )

            val selection = "${SpotifyReaderContract.UserEntry.PLATFORM}=?"
            val selectionArgs = arrayOf("Spotify")

            val cursor = DbInstance.readableDb.query(
                SpotifyReaderContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    userId = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.USER_ID))
                    userName = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.USER_NAME))
                    hasPremium = getInt(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.HAS_PREMIUM))
                    val refreshToken = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.REFRESH_TOKEN))
                    if (refreshToken != null) {
                        REFRESH_TOKEN = refreshToken
                    }
                }
            }
            // no user found
            if (userId == null) {
                return false
            }
            USER_ID = userId as String
            USER_NAME = userName as String
            if (hasPremium == 1) {
                HAS_PREMIUM = true
            }
            return true
        }

        fun getPlaylistFromDb(playlistId : Long) : Playlist? {
            var playlist : Playlist? = null
            // specify the columns to retrieve
            val projection = arrayOf(
                BaseColumns._ID,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI)

            val selection = "${BaseColumns._ID}=?"
            val selectionArgs = arrayOf(playlistId.toString())

            val cursor = DbInstance.readableDb.query(
                SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    val rowId       = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val href       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF))
                    val id         = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID))
                    val name       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME))
                    val collaborative = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE))
                    val owner      = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER))
                    val public        = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC))
                    val type       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE))
                    val uri        = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI))
                    playlist = Playlist(rowId, href, id, name, (collaborative == 1), owner, (public == 1), type, uri, null)
                }
            }

            return playlist
        }

        fun getPlaylistsFromDb() : MutableList<Playlist> {
            val playlists = mutableListOf<Playlist>()
            val db = DbInstance.readableDb
            // specify the columns to retrieve
            val projection = arrayOf(
                BaseColumns._ID,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE,
                SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI)

            val sortOrder = "${SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED} DESC"

            val cursor = db.query(
                SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
            )

            with(cursor) {
                while (moveToNext()) {
                    val rowId       = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val href       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF))
                    val id         = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID))
                    val name       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME))
                    val collaborative = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE))
                    val owner      = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER))
                    val public        = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC))
                    val type       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE))
                    val uri        = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI))
                    playlists.add(
                        Playlist(rowId, href, id, name, (collaborative == 1), owner, (public == 1), type, uri, null)
                    )
                }
            }

            return playlists
        }

        fun getPlaylistTracksFromDb(playlistRowId: Long) : List<Track> {
            val tracks = mutableListOf<Track>()
            // specify the columns to retrieve
            val projection = arrayOf(
                BaseColumns._ID,
                SpotifyReaderContract.TrackEntry.TRACK_ID,
                SpotifyReaderContract.TrackEntry.TRACK_NAME,
                SpotifyReaderContract.TrackEntry.TRACK_ARTISTS,
                SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS,
                SpotifyReaderContract.TrackEntry.TRACK_ALBUM)

            val selection = "${SpotifyReaderContract.TrackEntry.PLAYLIST_ID}=?"
            val selectionArgs = arrayOf(playlistRowId.toString())

            val cursor = DbInstance.readableDb.query(
                SpotifyReaderContract.TrackEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    val rowId       = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val id         = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ID))
                    val name       = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_NAME))
                    val artistNames= getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS))
                    val artistIds  = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS))
                    val album      = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ALBUM))

                    tracks.add(
                        Track(id, name, artistNames.split(","), artistIds.split(","), album)
                    )
                }
            }
            return tracks
        }

        fun removeAllGeneratedPlaylistsFromDb() {
            DbInstance.writableDb.delete(
                SpotifyReaderContract.TrackEntry.TABLE_NAME,
                null,
                null)
            DbInstance.writableDb.delete(
                SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                null,
                null)
        }
    }
}