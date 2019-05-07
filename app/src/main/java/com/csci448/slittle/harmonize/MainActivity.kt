package com.csci448.slittle.harmonize

import android.app.Notification
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
        const val NOTIFICATION_CHANNEL_ID = "1"
        fun createIntent(baseContext: Context): Intent {
            return Intent(baseContext, MainActivity::class.java)
        }
    }

    var playlists = mutableListOf<Playlist>()
    private lateinit var adapter  : PlaylistAdapter

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
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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
                if (playlist._rowId != null) {
                    // authorize or reauthorize to play tracks and fragment_export
                    val viewPlaylistIntent = ViewPlaylistActivity.createIntent(baseContext)
                    viewPlaylistIntent.putExtra("PLAYLIST_ROW_ID", playlist._rowId)
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
            // todo - bad spaghetti
            var needToConnect = false
            // not a very good check - the refresh token will pretty much always be
            // initialized so this only succeeds because || is greedy and the first check will
            // almost always succeed
            if (!SpotifyClient.userIsLoggedIn() || !SpotifyClient.refreshTokenIsInitialized()) {
                needToConnect = true
                val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext, "generate")
                startActivity(connectPlatformIntent)
            }
            else if (!SpotifyClient.accessTokenIsInitialized()) {
                SpotifyClient.authorize(false)
            }
            if (!needToConnect) {
                val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
                startActivity(generatePlaylistIntent)
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        playlists = SpotifyClient.getPlaylistsFromDb()
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
        when (item.itemId) {
            R.id.nav_generate -> {
                if (!SpotifyClient.refreshTokenIsInitialized() || !SpotifyClient.userIsLoggedIn()) {
                    val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext, "generate")
                    startActivity(connectPlatformIntent)
                }
                else if (!SpotifyClient.accessTokenIsInitialized()) {
                    SpotifyClient.authorize(false)
                }
                val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
                startActivity(generatePlaylistIntent)
            }
            R.id.nav_connect -> {
                val connectPlatformIntent = PlatformConnectActivity.createIntent(baseContext, "generate")
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