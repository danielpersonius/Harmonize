package com.csci448.slittle.harmonize

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_view_playlist.*
import kotlinx.android.synthetic.main.app_bar_view_playlist.*
import kotlinx.android.synthetic.main.fragment_view_playlist.*
import kotlinx.android.synthetic.main.playlist_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ViewPlaylistFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOG_TAG = "ViewPlaylistFragment"
    }

    var playlistTitle : String? = "Playlist name"
    var tracks : List<Track> = arrayListOf()
    var tunedParameters : HashMap<String, String> = hashMapOf()
    private lateinit var dbHelper : SpotifyReaderDbHelper
    private var insertId : Long? = null

    private fun savePlaylist() : Long? {
        Log.d(LOG_TAG, "savePlaylist() called")
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedDateTime = sdf.format(Date()) // format current date

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED, formattedDateTime)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME, playlistTitle)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE, 0)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER, SpotifyClient.USER_NAME)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC, 0)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE, "playlist")
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(SpotifyReaderContract.PlaylistEntry.TABLE_NAME, null, values)

        if (newRowId == -1L) {
            // conflict with pre-existing data
            Log.d(LOG_TAG, "new row id = -1. conflict with pre-existing id")
        }

        db.close()
        return newRowId
    }

    private fun saveTrack(track : Track) : Long? {
        Log.d(LOG_TAG, "saveTrack() called")
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.TrackEntry.TRACK_ID,          track._id)
            put(SpotifyReaderContract.TrackEntry.PLAYLIST_ID,       insertId)
            put(SpotifyReaderContract.TrackEntry.TRACK_NAME,        track._name)
            put(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS,     track._artistNames.toString())
            put(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS, track._artistIds.toString())
            put(SpotifyReaderContract.TrackEntry.TRACK_ALBUM,       track._album)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(SpotifyReaderContract.TrackEntry.TABLE_NAME, null, values)

        if (newRowId == -1L) {
            // conflict with pre-existing data
            Log.d(LOG_TAG, "new row id = -1. conflict with pre-existing id")
        }

        return newRowId
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        return inflater.inflate(R.layout.activity_view_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        dbHelper = SpotifyReaderDbHelper(context)

        val toggle = ActionBarDrawerToggle(
            activity, view_playlist_drawer_layout, view_playlist_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        view_playlist_toolbar.inflateMenu(R.menu.view_playlist_options)
        view_playlist_toolbar.setOnMenuItemClickListener {
            Log.d(LOG_TAG, "toolbar export!")
            when (it.itemId) {
                R.id.playlist_menu_export_option -> {
                    Log.d(LOG_TAG, "playlist export!")
                    val exportIntent = ExportActivity.createIntent(context)
                    exportIntent.putExtra("PLAYLIST_NAME", playlistTitle)
                    // get just the ids of the tracks
                    val trackIds = mutableListOf<String>()
                    for (track: Track in tracks) {
                        trackIds.add(track._id)
                    }
                    exportIntent.putExtra("PLAYLIST_TRACK_IDS", ArrayList(trackIds))
                    exportIntent.putExtra("TUNED_PARAMETERS", tunedParameters)
                    if (insertId != -1L) {
                        exportIntent.putExtra("PLAYLIST_INSERT_ID", insertId)
                    }
                    startActivity(exportIntent)
                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }
        view_playlist_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        view_playlist_nav_view.setNavigationItemSelectedListener(this)

        // rotation
        if (savedInstanceState != null) {
            playlistTitle = savedInstanceState.getString("PLAYLIST_NAME")
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistTitle = extras.getString("PLAYLIST_NAME")
                val playlistId = extras.getString("PLAYLIST_ID") ?: null
                tracks = extras.getStringArrayList("PLAYLIST_TRACKS") as List<Track>
                tunedParameters = extras.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
//                if (playlistId != null) {
//                    tracks = SpotifyClient.getPlaylistTracks(playlistId) as MutableList<Track>
//                }
            }
        }

        insertId = savePlaylist()
        playlist_name_banner.text = playlistTitle

        // change name pen icon press
        editable_icon.setOnClickListener {
            Toast.makeText(context, "Change name", Toast.LENGTH_SHORT).show()
            val titleEditTextBox = EditText(context)
            // dialog box for input
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Change playlist name")
            builder.setView(titleEditTextBox)
            builder.setPositiveButton("Done") {_, _ ->
                // todo persist name change
                playlistTitle = titleEditTextBox.text.toString()
                playlist_name_banner.text = playlistTitle
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

            saveTrack(track)

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

            // play song on press
            trackView.setOnClickListener {
                Toast.makeText(context, "Play song", Toast.LENGTH_SHORT).show()
                SpotifyClient.startPlayback(track._id, false)
            }
            trackView.song_info_icon.setOnClickListener {
                val audioFeatures = SpotifyClient.getTrackAudioFeatures(track._id) as MutableMap<String, String>
                track._metadata = audioFeatures
                val intent = TrackCharacteristicsActivity.createIntent(context, track._name, track._metadata)
                startActivity(intent)
            }

            // todo swipe left or right to delete song from list

            tracklist_linearlayout.addView(trackView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.view_playlist_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d(LOG_TAG, "onOptionsItemSelected() called")
        // Handle presses on the action bar menu items
        when (item?.itemId) {
            R.id.playlist_menu_export_option -> {
                Log.d(LOG_TAG, "export!")
                val exportIntent = ExportActivity.createIntent(context)
                exportIntent.putExtra("PLAYLIST_NAME", playlistTitle)
                // get just the ids of the tracks
                val trackIds = mutableListOf<String>()
                for (track: Track in tracks) {
                    trackIds.add(track._id)
                }
                exportIntent.putExtra("PLAYLIST_TRACK_IDS", ArrayList(trackIds))
                exportIntent.putExtra("TUNED_PARAMETERS", tunedParameters)
                if (insertId != -1L) {
                    exportIntent.putExtra("PLAYLIST_INSERT_ID", insertId)
                }
                startActivity(exportIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_generate -> {}
            R.id.nav_connect -> {}
        }

        view_playlist_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
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
        dbHelper.close()
        Log.d(LOG_TAG, "onDestroy() called")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
    }
}