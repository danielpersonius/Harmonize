package com.csci448.slittle.harmonize

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.app_bar_connect.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_connect_two.*


class PlatformConnectActivity : SingleFragmentActivity()  {

    companion object {
        private const val LOG_TAG = "PlatformConnectActivity"

        fun createIntent(baseContext: Context?): Intent {
            val intent = Intent(baseContext, PlatformConnectActivity::class.java)
            return intent
        }

        val CLIENT_ID = "96fb37843a5e4e92a1a8c4c5168e3371"
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        val SPOTIFY_LOGIN_REQUEST_CODE = 1
        // can be anything really
        val REDIRECT_URI = "com.csci448.slittle.harmonize://callback"
        val CLIENT_SECRET = "84ce3a2b19c74df7900d1d6d588a14d2"
        lateinit var ACCESS_TOKEN: String
    }

    private lateinit var dbHelper : SpotifyReaderDbHelper

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = PlatformConnectFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(connect_toolbar)

//        val toggle = ActionBarDrawerToggle(
//            this, connect_drawer_layout, connect_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        )
//        connect_drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)
    }
    // handling results has to happen here and not in fragment, since passing 'activity'
    // as context to spotify login function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }

        dbHelper = SpotifyReaderDbHelper(baseContext)

        // Spotify login activity
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            Log.d(LOG_TAG, response.type.toString())

            when (response.type) {
                // Response was successful and contains auth token
                AuthenticationResponse.Type.TOKEN -> {
                    // Handle successful response
                    ACCESS_TOKEN = response.accessToken
                    SpotifyClient.ACCESS_TOKEN = response.accessToken
                    // get user info like ID and Name
                    SpotifyClient.getUserInformation()
                    // this
                    storeSpotifyUser(SpotifyClient.USER_ID, SpotifyClient.USER_NAME, SpotifyClient.ACCESS_TOKEN)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.platform_connect_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.logout_spotify_option -> {
//                logoutFromSpotify()
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun storeSpotifyUser(userId : String,
                                 userName : String,
                                 accessToken : String) {
        Log.d(LOG_TAG, "storeSpotifyUser() called")
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.UserEntry.USER_ID, userId)
            put(SpotifyReaderContract.UserEntry.USER_NAME, userName)
            put(SpotifyReaderContract.UserEntry.PLATFORM, "Spotify")
            put(SpotifyReaderContract.UserEntry.ACCESS_TOKEN, accessToken)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(SpotifyReaderContract.UserEntry.TABLE_NAME, null, values)

        if (newRowId == -1L) {
            // conflict with pre-existing data
            Log.d(LOG_TAG, "new row id = -1. conflict with pre-existing id")
        }

        db.close()
    }
}