package com.csci448.slittle.harmonize

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse

class PlatformConnectActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "PlatformConnectActivity"
        val CLIENT_ID = "96fb37843a5e4e92a1a8c4c5168e3371"
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        val SPOTIFY_LOGIN_REQUEST_CODE = 1
        // can be anything really
        val REDIRECT_URI = "com.csci448.slittle.harmonize://callback"
        val CLIENT_SECRET = "84ce3a2b19c74df7900d1d6d588a14d2"
        lateinit var ACCESS_TOKEN: String
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = PlatformConnectFragment()

    // handling results has to happen here and not in fragment, since passing 'activity'
    // as context to spotify login function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }
//        Log.d(LOG_TAG, "onActivityResult() called: $requestCode")

        // Spotify login activity
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            Log.d(LOG_TAG, response.type.toString())

            when (response.type) {
                // Response was successful and contains auth token
                AuthenticationResponse.Type.TOKEN -> {
                    // Handle successful response
                    Toast.makeText(this, "Connected to Spotify with token: ${response.accessToken}", Toast.LENGTH_SHORT).show()
                    ACCESS_TOKEN = response.accessToken
                    SpotifyClient.ACCESS_TOKEN = response.accessToken

//                    val chooseSourceIntent = ChooseSourceActivity.createIntent(this)
//                    startActivity(chooseSourceIntent)

                    val generateActivityIntent = GeneratePlaylistActivity.createIntent(this)
                    startActivity(generateActivityIntent)
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
}