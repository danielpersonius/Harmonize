package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.csci448.slittle.harmonize.PlatformConnectActivity.Companion.SPOTIFY_LOGIN_REQUEST_CODE
import com.spotify.sdk.android.authentication.AuthenticationClient
import kotlinx.android.synthetic.main.activity_connect.*
import kotlinx.android.synthetic.main.app_bar_connect.*
import kotlinx.android.synthetic.main.fragment_connect.*

class PlatformConnectFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private var goto : String? = "home"
        // if the user is not logged into the Spotify app when they try to play a song
        // on the playlist view, they are redirected here. this is the id of the
        // playlist they are viewing so onActivityResult in PlatformConnectActivity
        // knows where to send the user back to
        var playlistRowId = 0L
    }

    private fun loginToSpotify() {
        AuthenticationClient.openLoginActivity(activity, SPOTIFY_LOGIN_REQUEST_CODE, SpotifyClient.getAuthenticationRequest())
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connect_progress_circle.visibility = GONE

        // rotation
        if (savedInstanceState != null) {
            goto = savedInstanceState.getString("GOTO")
            playlistRowId = savedInstanceState.getLong("PLAYLIST_ROW_ID")
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                goto = extras.getString("GOTO")
                if (extras.containsKey("PLAYLIST_ROW_ID")) {
                    playlistRowId = extras.getLong("PLAYLIST_ROW_ID")
                }
            }
        }

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
            loginToSpotify()
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
        FragmentHelper.handleNavItems(item, this, context as Context)
        connect_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        connect_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        connect_progress_circle.visibility = GONE
    }
}