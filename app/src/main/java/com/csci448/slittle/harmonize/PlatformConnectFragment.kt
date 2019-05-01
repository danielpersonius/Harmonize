package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.util.Log
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_connect.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_connect.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.app_bar_connect.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_connect_two.*
import kotlinx.android.synthetic.main.fragment_connect_two.connect_drawer_layout

class PlatformConnectFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOG_TAG = "PlatformConnectFragment"
    }

    private lateinit var dbHelper : SpotifyReaderDbHelper

    private fun connectToPlatform(platform : String) {
        Log.d(LOG_TAG, "connectToPlatform() called")
        var userId      : String? = null
        var userName    : String? = null
        var accessToken : String? = null

        val db = dbHelper.readableDatabase
        // specify the columns to retrieve
        val projection = arrayOf(
            BaseColumns._ID,
            SpotifyReaderContract.UserEntry.USER_ID,
            SpotifyReaderContract.UserEntry.USER_NAME,
            SpotifyReaderContract.UserEntry.PLATFORM,
            SpotifyReaderContract.UserEntry.ACCESS_TOKEN)
        val selection = "${SpotifyReaderContract.UserEntry.PLATFORM} = ?"
        val selectionArgs = arrayOf(platform)

        val cursor = db.query(
            SpotifyReaderContract.UserEntry.TABLE_NAME,
            projection,
            selection,     // The columns for the WHERE clause
            selectionArgs, // The values for the WHERE clause
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                //val rowId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                userId = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.USER_ID))
                userName = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.USER_NAME))
                accessToken = getString(getColumnIndexOrThrow(SpotifyReaderContract.UserEntry.ACCESS_TOKEN))
            }
        }

//        if (accessToken == null) {
            loginToSpotify()
//        }
        // user already has an access token
//        else {
//            SpotifyClient.ACCESS_TOKEN = accessToken as String
//            val generateActivityIntent = GeneratePlaylistActivity.createIntent(context)
//            startActivity(generateActivityIntent)
//        }

        cursor.close()
    }

    private fun loginToSpotify() {
        AuthenticationClient.openLoginActivity(activity, PlatformConnectActivity.SPOTIFY_LOGIN_REQUEST_CODE, SpotifyClient.getAuthenticationRequest())
    }

    private fun logoutFromSpotify() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://accounts.spotify.com")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        connect_progress_circle.visibility = GONE
        dbHelper = SpotifyReaderDbHelper(context)

        val toggle = ActionBarDrawerToggle(
            activity, connect_drawer_layout, connect_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        connect_toolbar.inflateMenu(R.menu.platform_connect_options)
        connect_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.logout_spotify_option -> {
                    logoutFromSpotify()
                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }

        connect_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        connect_nav_view.setNavigationItemSelectedListener(this)

        connect_spotify_button.setOnClickListener {
            connect_progress_circle.visibility = VISIBLE
            connectToPlatform("Spotify")
        }
        connect_apple_button.setOnClickListener {
            Toast.makeText(context, "Connecting Apple Music!", Toast.LENGTH_SHORT).show()
        }
        connect_soundcloud_button.setOnClickListener {
            Toast.makeText(context, "Connecting Soundcloud!", Toast.LENGTH_SHORT).show()
        }
        connect_pandora_button.setOnClickListener {
            Toast.makeText(context, "Connecting Pandora!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_generate -> {
//                val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
//                startActivity(generatePlaylistIntent)
                val connectPlatformIntent = PlatformConnectActivity.createIntent(context)
                //startActivity(connectPlatformIntent)
            }
            R.id.nav_connect -> {
                val connectPlatformIntent = PlatformConnectActivity.createIntent(context)
                //startActivity(connectPlatformIntent)
            }
        }

        connect_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
        connect_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        connect_progress_circle.visibility = GONE
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
    }
}