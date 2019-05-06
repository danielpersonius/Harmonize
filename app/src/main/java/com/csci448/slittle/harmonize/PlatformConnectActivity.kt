package com.csci448.slittle.harmonize

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
                    storeSpotifyUser(SpotifyClient.USER_ID, SpotifyClient.USER_NAME)

                    when (gotoPage) {
                        "generate" -> {
                            val generateActivityIntent = GeneratePlaylistActivity.createIntent(this)
                            startActivity(generateActivityIntent)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(LOG_TAG, "activity onCreateOptionsMenu() called")
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun storeSpotifyUser(userId : String,
                                 userName : String) {
        Log.d(LOG_TAG, "storeSpotifyUser() called")
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.UserEntry.USER_ID, userId)
            put(SpotifyReaderContract.UserEntry.USER_NAME, userName)
            put(SpotifyReaderContract.UserEntry.PLATFORM, "Spotify")
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = DbInstance.writableDb.insert(SpotifyReaderContract.UserEntry.TABLE_NAME, null, values)

        if (newRowId == -1L) {
            // conflict with pre-existing data
            Log.d(LOG_TAG, "new row id = -1. conflict with pre-existing id")
        }
    }
}