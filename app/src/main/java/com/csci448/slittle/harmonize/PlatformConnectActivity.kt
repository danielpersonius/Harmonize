package com.csci448.slittle.harmonize

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse

class PlatformConnectActivity : SingleFragmentActivity()  {
    companion object {
        private const val LOG_TAG = "PlatformConnectActivity"
        private var gotoPage : String = "home"
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        const val SPOTIFY_LOGIN_REQUEST_CODE = 1
        /**
         *
         * @param goto - what page to go to after the user connects
         */
        fun createIntent(baseContext: Context?, goto : String): Intent {
            val intent =  Intent(baseContext, PlatformConnectActivity::class.java)
            intent.putExtra("GOTO", goto)
            gotoPage = goto
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG
    override fun createFragment() = PlatformConnectFragment()

    // handling results has to happen here and not in fragment, since passing 'activity'
    // as context to spotify login function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }

        // Spotify login activity
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)

            when (response.type) {
                // Response was successful and contains auth code needed to get refresh token
                AuthenticationResponse.Type.CODE -> {
                    SpotifyClient.authorize(true, response.code)
                    SpotifyClient.setUserIsLoggedIn(true)
                    storeSpotifyUser(SpotifyClient.USER_ID, SpotifyClient.USER_NAME)

                    when (gotoPage) {
                        "generate" -> {
                            val generateActivityIntent = GeneratePlaylistActivity.createIntent(this)
                            startActivity(generateActivityIntent)
                        }
                        "view" -> {
                            val viewPlaylistIntent = ViewPlaylistActivity.createIntent(this)
                            viewPlaylistIntent.putExtra("PLAYLIST_ROW_ID", PlatformConnectFragment.playlistRowId)
                            // todo eliminate this - eventually these parameters will be retrieved on the view playlist page
                            viewPlaylistIntent.putExtra("TUNED_PARAMETERS", hashMapOf<String, String>())
                            startActivity(viewPlaylistIntent)
                        }
                        else -> {
                            val mainActivityIntent = MainActivity.createIntent(this)
                            startActivity(mainActivityIntent)
                        }
                    }
                }

                // Auth flow returned an error
                AuthenticationResponse.Type.ERROR -> {
                    // Handle error response
                    Toast.makeText(this, "Error connecting to Spotify!", Toast.LENGTH_SHORT).show()
                    Log.d(LOG_TAG, response.error)
                }

                else -> {
                    // handle other cases
                    // Most likely auth flow was cancelled
                    Toast.makeText(this, "Something else happened.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun storeSpotifyUser(userId : String,
                                 userName : String) {
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.UserEntry.USER_ID, userId)
            put(SpotifyReaderContract.UserEntry.USER_NAME, userName)
            put(SpotifyReaderContract.UserEntry.PLATFORM, "Spotify")
        }

        // Insert the new row, returning the primary key value of the new row
        DbInstance.writableDb.insert(SpotifyReaderContract.UserEntry.TABLE_NAME, null, values)
    }
}