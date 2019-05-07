package com.csci448.slittle.harmonize
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_export.*
import kotlinx.android.synthetic.main.app_bar_export.*
import kotlinx.android.synthetic.main.fragment_export.*

class ExportFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOG_TAG = "ExportFragment"
    }

    private lateinit var playlistTitle : String
    private var trackIds = arrayListOf<String>()
    private var tunedParameters = mutableMapOf<String, String>()
    private var newPlaylistData : Pair<String?, String?>? = null
    private var playlistRowId : Long? = null

    private fun updatePlaylistInDb(rowId : Long, href : String, id : String) : Int {
        val values = ContentValues().apply {
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF, href)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID, id)
        }

        val selection = "${BaseColumns._ID}=?"
        val selectionArgs = arrayOf(rowId.toString())
        return DbInstance.writableDb.update(SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                         values,
                         selection,
                         selectionArgs)
    }

    private fun openOtherApp(uri : String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context!!.packageName))
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_export, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toggle = ActionBarDrawerToggle(
            activity, export_drawer_layout, export_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        export_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        export_nav_view.setNavigationItemSelectedListener(this)

        // rotation
        if (savedInstanceState != null) {
            playlistTitle = savedInstanceState.getString("PLAYLIST_NAME") as String
            playlistRowId = savedInstanceState.getLong("PLAYLIST_ROW_ID")
            trackIds = savedInstanceState.getStringArrayList("PLAYLIST_TRACK_IDS") as ArrayList
            tunedParameters = savedInstanceState.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
            playlistRowId = savedInstanceState.getLong("PLAYLIST_ID")
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistTitle =
                    if (extras.containsKey("PLAYLIST_NAME"))
                        extras.getString("PLAYLIST_NAME") as String
                    else "suggested playlist"
                playlistRowId = extras.getLong("PLAYLIST_ROW_ID")
                trackIds = extras.getStringArrayList("PLAYLIST_TRACK_IDS") as ArrayList<String>
                tunedParameters =
                        if (extras.containsKey("TUNED_PARAMETERS") && extras.getSerializable("TUNED_PARAMETERS") != null) {
                            extras.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
                        }
                        else {
                            mutableMapOf()
                        }
            }
        }

        export_spotify_button.setOnClickListener {
            Toast.makeText(context, "Exporting to Spotify...", Toast.LENGTH_SHORT).show()
            newPlaylistData = SpotifyClient.exportPlaylist(playlistTitle,
                                                           trackIds.toList(),
                                                           tunedParameters)
            if (newPlaylistData != null) {
                // update db with new href and uri
                val newPlaylistId = newPlaylistData?.first
                val newPlaylistHref = newPlaylistData?.second
                if (playlistRowId != null && newPlaylistId != null && newPlaylistHref != null) {
                    val affectedRows = updatePlaylistInDb(playlistRowId as Long, newPlaylistHref, newPlaylistId)
                }
                // open spotify app
                val redirectURI = "spotify:user:${SpotifyClient.USER_ID}:playlist:$newPlaylistId"
                openOtherApp(redirectURI)
            }
        }
        export_apple_button.setOnClickListener {
            Toast.makeText(context, "no action", Toast.LENGTH_SHORT).show()
        }
        export_soundcloud_button.setOnClickListener {
            Toast.makeText(context, "no action", Toast.LENGTH_SHORT).show()
        }
        export_pandora_button.setOnClickListener {
            Toast.makeText(context, "no action", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FragmentHelper.handleNavItems(item, this, context as Context)
        export_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}