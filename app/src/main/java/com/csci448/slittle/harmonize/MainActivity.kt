package com.csci448.slittle.harmonize

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
        fun createIntent(baseContext: Context): Intent {
            return Intent(baseContext, MainActivity::class.java)
        }
    }

    val playlists = mutableListOf<Playlist>()
    private lateinit var adapter  : PlaylistAdapter

    private fun getPlaylistTracks(playlistRowId: Long) : List<Track> {
        val tracks = mutableListOf<Track>()
        // specify the columns to retrieve
        val projection = arrayOf(
            BaseColumns._ID,
            SpotifyReaderContract.TrackEntry.TRACK_ID,
            SpotifyReaderContract.TrackEntry.TRACK_NAME,
            SpotifyReaderContract.TrackEntry.TRACK_ARTISTS,
            SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS,
            SpotifyReaderContract.TrackEntry.TRACK_ALBUM)

        val selection = "${SpotifyReaderContract.TrackEntry.PLAYLIST_ID}=?"
        val selectionArgs = arrayOf(playlistRowId.toString())

        val cursor = DbInstance.readableDb.query(
            SpotifyReaderContract.TrackEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val rowId       = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val id         = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ID))
                val name       = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_NAME))
                val artistNames= getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS))
                val artistIds  = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS))
                val album      = getString(getColumnIndexOrThrow(SpotifyReaderContract.TrackEntry.TRACK_ALBUM))

                tracks.add(
                    Track(id, name, artistNames.split(","), artistIds.split(","), album)
                )
            }
        }
        return tracks
    }

    private fun getPlaylists() {
        val db = DbInstance.readableDb
        // specify the columns to retrieve
        val projection = arrayOf(
            BaseColumns._ID,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE,
            SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI)

        val sortOrder = "${SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED} DESC"

        val cursor = db.query(
            SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val rowId       = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val href       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF))
                val id         = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID))
                val name       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME))
                val collaborative = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE))
                val owner      = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER))
                val public        = getInt   (getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC))
                val type       = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE))
                val uri        = getString(getColumnIndexOrThrow(SpotifyReaderContract.PlaylistEntry.PLAYLIST_URI))
                playlists.add(
                    Playlist(rowId, href, id, name, (collaborative == 1), owner, (public == 1), type, uri, null)
                )
            }
        }
    }

    private fun deleteGeneratedPlaylist(playlist : Playlist) : Int {
        val db = DbInstance.writableDb
        val selection = "${BaseColumns._ID}=?"
        val selectionArgs = arrayOf(playlist._rowId.toString())
        val affectedRows = db.delete(SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                                        selection,
                                        selectionArgs)
        playlists.remove(playlist)
        adapter.notifyDataSetChanged()
        return affectedRows
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class PlaylistAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return playlists.size
        }

        override fun getItem(position: Int): Playlist {
            return playlists[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = parent?.context?.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.playlist_grid_item,null)
            val name  = view.findViewById(R.id.playlist_grid_playlist_name) as TextView
            val playlist = getItem(position)

            name.text = getItem(position)._name
            view.setOnClickListener {
                // retrieve playlist and tracks from db
                if (playlist._rowId != null) {
                    // authorize or reauthorize to play tracks and export
                    SpotifyClient.authorize(false)
                    val tracks = getPlaylistTracks(playlist._rowId)
                    val viewPlaylistIntent = ViewPlaylistActivity.createIntent(baseContext, getItem(position)._name)
                    viewPlaylistIntent.putExtra("PLAYLIST_NAME", playlist._name)
                    viewPlaylistIntent.putExtra("PLAYLIST_ROW_ID", playlist._rowId)
                    viewPlaylistIntent.putExtra("NEW_PLAYLIST", false)
                    viewPlaylistIntent.putExtra("PLAYLIST_TRACKS", ArrayList(tracks))
                    // todo change to db call
                    viewPlaylistIntent.putExtra("TUNED_PARAMETERS", hashMapOf<String, String>())
                    startActivity(viewPlaylistIntent)
                }
                else {
                    Toast.makeText(baseContext, "null id", Toast.LENGTH_SHORT).show()
                }
            }
            view.setOnLongClickListener {
                if (deleteGeneratedPlaylist(playlist) == 1) {
                    Toast.makeText(baseContext, "Playlist deleted!", Toast.LENGTH_SHORT).show()
                }
                true
            }

            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        DbInstance.createDbInstance(applicationContext)
        createNotificationChannel()

        fab.setOnClickListener {
            // have to connect to spotify each time for now until we persist access token
            val connectIntent = PlatformConnectActivity.createIntent(baseContext, "generate")
            startActivity(connectIntent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        getPlaylists()
        adapter = PlaylistAdapter()
        home_playlist_grid.adapter = adapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_generate -> {
                if (!SpotifyClient.refreshTokenIsInitialized()) {
                    val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext, "generate")
                    startActivity(connectPlatformIntent)
                }
                else if (!SpotifyClient.accessTokenIsInitialized()) {
                    SpotifyClient.authorize(false)
                    val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
                    startActivity(generatePlaylistIntent)
                }
            }
            R.id.nav_connect -> {
                val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext, "home")
                startActivity(connectPlatformIntent)
            }
            R.id.nav_home -> {
                val mainActivityIntent = createIntent(baseContext)
                startActivity(mainActivityIntent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onStart() {
        adapter.notifyDataSetChanged()
        super.onStart()
    }
}