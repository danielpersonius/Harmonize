package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_choose_source.*
import kotlinx.android.synthetic.main.app_bar_choose_source.*
import kotlinx.android.synthetic.main.fragment_choose_source.*

class ChooseSourceFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_choose_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE

        val toggle = ActionBarDrawerToggle(
            activity, choose_source_drawer_layout, choose_source_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        choose_source_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        choose_source_nav_view.setNavigationItemSelectedListener(this)

        // todo 50 is the max limit, so look into paginating
        val playlists = SpotifyClient.getUserPlaylists(SpotifyClient.ACCESS_TOKEN, 50, 0) as List<Playlist>
        for (playlist : Playlist in playlists) {
            val playlistTextView = TextView(context)
            playlistTextView.text = playlist._name
            playlistTextView.textSize = 24.0f
            playlistTextView.ellipsize = TextUtils.TruncateAt.END
            playlistTextView.maxLines = 1

            playlistTextView.setOnClickListener {
                // need to be IDs, not names
                val allArtists = mutableSetOf<String>()
                val tracks = SpotifyClient.getPlaylistTracks(playlist._id ?: "")
                for (track : Track in tracks) {
                    allArtists.addAll(track._artistIds)
                }
                val tuneParametersIntent = TuneParametersActivity.createIntent(context, playlist._name)
                tuneParametersIntent.putExtra("PLAYLIST_NAME", playlist._name)
                tuneParametersIntent.putExtra("PLAYLIST_ID", playlist._id)
                tuneParametersIntent.putExtra("PLAYLIST_ARTISTS", ArrayList(allArtists))
                tuneParametersIntent.putExtra("PLAYLIST_TRACKS", arrayListOf(tracks))
                // show spinner progress bar
                choose_source_progress_circle.visibility = VISIBLE
                startActivity(tuneParametersIntent)
            }
            spotify_choose_playlist_scrollview_linearlayout.addView(playlistTextView)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FragmentHelper.handleNavItems(item, this, context as Context)
        choose_source_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE
    }
}