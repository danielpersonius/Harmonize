package com.csci448.slittle.harmonize

import android.app.Activity
import android.app.AlertDialog
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
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_view_playlist.*
import kotlinx.android.synthetic.main.app_bar_view_playlist.*
import kotlinx.android.synthetic.main.fragment_view_playlist.*
import kotlinx.android.synthetic.main.playlist_item.view.*

class ViewPlaylistFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private var playlist : Playlist? = null
    private var playlistTitle : String? = "Playlist name"
    var tracks : List<Track> = listOf()
    var tunedParameters : HashMap<String, String>? = hashMapOf()
    private var playlistRowId : Long? = null

    private fun changePlaylistName(rowId : Long, newName : String) : Int {
        val values = ContentValues().apply {
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME, newName)
        }

        val selection = "${BaseColumns._ID}=?"
        val selectionArgs = arrayOf(rowId.toString())
        return DbInstance.writableDb.update(SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_view_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // rotation
        if (savedInstanceState != null) {
            playlistTitle = savedInstanceState.getString("PLAYLIST_NAME")
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                if (extras.containsKey("PLAYLIST_ROW_ID")) {
                    playlistRowId = extras.getLong("PLAYLIST_ROW_ID")
                }
                tunedParameters =
                    if (extras.containsKey("TUNED_PARAMETERS"))
                        extras.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
                    else
                        null
            }
        }

        if (playlistRowId != null) {
            playlist = SpotifyClient.getPlaylistFromDb(playlistRowId as Long)
            playlistTitle = playlist?._name ?: playlistTitle
            tracks = SpotifyClient.getPlaylistTracksFromDb(playlistRowId as Long)

        }

        val toggle = ActionBarDrawerToggle(
            activity, view_playlist_drawer_layout, view_playlist_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        view_playlist_toolbar.inflateMenu(R.menu.view_playlist_options)
        view_playlist_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.playlist_menu_export_option -> {
                    val exportIntent = ExportActivity.createIntent(context)
                    exportIntent.putExtra("PLAYLIST_NAME", playlistTitle)
                    // get just the ids of the tracks
                    val trackIds = mutableListOf<String>()
                    for (track: Track in tracks) {
                        trackIds.add(track._id)
                    }
                    exportIntent.putExtra("PLAYLIST_TRACK_IDS", ArrayList(trackIds))
                    exportIntent.putExtra("TUNED_PARAMETERS", tunedParameters)
                    if (playlistRowId != null) {
                        exportIntent.putExtra("PLAYLIST_ROW_ID", playlistRowId as Long)
                    }
                    startActivity(exportIntent)
                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }
        // add 'View on Spotify' option if already exported
        // already exported if db row does not have null uri
        if (playlistRowId != null) {
            val playlist = SpotifyClient.getPlaylistFromDb(playlistRowId as Long)
            if (playlist?._id != null) {
                // remove fragment_export option
                view_playlist_toolbar.menu.removeItem(R.id.playlist_menu_export_option)

                val viewPlaylistMenuItem = view_playlist_toolbar.menu.add(
                    Menu.NONE, // groupId
                    1, // itemId
                    2, // order
                    "View in Spotify" // title
                )
                viewPlaylistMenuItem?.setOnMenuItemClickListener {
                    val redirectURI = "spotify:user:${SpotifyClient.USER_ID}:playlist:${playlist._id}"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(redirectURI)
                    intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context!!.packageName))
                    startActivity(intent)
                    true
                }
            }
        }

        view_playlist_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        view_playlist_nav_view.setNavigationItemSelectedListener(this)

        playlist_name_banner.text = playlistTitle

        // change name pen icon press
        playlist_name_banner.setOnClickListener {
            val titleEditTextBox = EditText(context)
            // dialog box for input
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Change playlist name")
            builder.setView(titleEditTextBox)
            builder.setPositiveButton("Done") {_, _ ->
                playlistTitle = titleEditTextBox.text.toString()
                playlist_name_banner.text = playlistTitle
                // update in db
                changePlaylistName(playlistRowId as Long, playlistTitle as String)
            }

            builder.setNegativeButton("Cancel") {_, _ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        tracks.forEach {
            // it is name of iterator
            val track = it
            val inflater        = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val trackView             = inflater.inflate(R.layout.playlist_item,null)
            val songNameTextView   = trackView.findViewById(R.id.playlist_song_name) as TextView
            val artistNameTextView = trackView.findViewById(R.id.playlist_artist_name) as TextView
            val albumNameTextView  = trackView.findViewById(R.id.playlist_album_name) as TextView

            songNameTextView.text   = track._name
            val artists = track._artistNames
            var artistsText = ""
            for (i in 0 until artists.size) {
                if (i < artists.size - 1) {
                    artistsText += "${artists[i]}, "
                }
                else {
                    artistsText += artists[i]
                }
            }
            artistNameTextView.text = artistsText
            albumNameTextView.text  = track._album

            // try to play song on press
            trackView.setOnClickListener {
                if (SpotifyClient.userIsLoggedIn()) {
                    if (SpotifyClient.startPlayback(track._id, false) as Boolean) {
                        Toast.makeText(context, "Playing song!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(context, "need to log into Spotify app first", Toast.LENGTH_SHORT).show()
                    val connectPlatformIntent = PlatformConnectActivity.createIntent(context, "view")
                    connectPlatformIntent.putExtra("PLAYLIST_ROW_ID", playlistRowId)
                    startActivity(connectPlatformIntent)
                }
            }
            trackView.song_info_icon.setOnClickListener {
                val audioFeatures = SpotifyClient.getTrackAudioFeatures(track._id, activity as Activity)
                track._metadata = audioFeatures
                val intent = TrackCharacteristicsActivity.createIntent(context, track._name, track._metadata)
                startActivity(intent)
            }

            // todo swipe left or right to delete song from list
            tracklist_linearlayout.addView(trackView)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FragmentHelper.handleNavItems(item, this, context as Context)
        view_playlist_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}