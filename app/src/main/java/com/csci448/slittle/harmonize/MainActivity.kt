package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val LOG_TAG = "HomeActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, MainActivity::class.java)
            return intent
        }

    }

    val playlists = mutableListOf<Playlist>(
        //Playlist("href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),// mapOf("BPM" to "1000")))),
        //Playlist("href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album")))// mapOf("BPM" to "1000")))),
    )
    private lateinit var adapter  : PlaylistAdapter
    private lateinit var dbHelper : SpotifyReaderDbHelper

    private fun getGeneratedPlaylists() {
        val db = dbHelper.readableDatabase
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

        val sortOrder = "${SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED} ASC"

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
        db.close()
    }

    private fun deleteGeneratedPlaylist(playlist : Playlist) : Int {
        Log.d(LOG_TAG, "deleteGeneratedPlaylist() called")
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID}=?"
        val selectionArgs = arrayOf(playlist._rowId.toString())
        val affectedRows = db.delete(SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                                        selection,
                                        selectionArgs)
        db.close()
        Log.d(LOG_TAG, "affected rows: $affectedRows")
        playlists.remove(playlist)
        adapter.notifyDataSetChanged()
        return affectedRows
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

            name.text = getItem(position)._name
            view.setOnClickListener {
//                val viewPlaylistIntent = ViewPlaylistActivity.createIntent(baseContext, getItem(position)._name)
//                startActivity(viewPlaylistIntent)
                Toast.makeText(baseContext, "temporarily unavailable. please generate a new playlist.", Toast.LENGTH_SHORT).show()
            }
            view.setOnLongClickListener {
                val affectedRows = deleteGeneratedPlaylist(getItem(position))
                true
            }

            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        dbHelper = SpotifyReaderDbHelper(this)

        fab.setOnClickListener {
            // have to connect to spotify each time for now until we persist access token
            val connectIntent = PlatformConnectActivity.createIntent(baseContext)
            startActivity(connectIntent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        getGeneratedPlaylists()
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
//                val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
//                startActivity(generatePlaylistIntent)
                val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext)
//                startActivity(connectPlatformIntent)
            }
            R.id.nav_connect -> {
                val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext)
//                startActivity(connectPlatformIntent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        super.onResume()
    }
}