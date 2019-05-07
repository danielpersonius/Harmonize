package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeActivity : SingleFragmentActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOG_TAG = "HomeActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, HomeActivity::class.java)
            return intent
        }

    }


    var playlists = mutableListOf(
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id", "some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album"))),//, mapOf("BPM" to "1000")))),
        Playlist(null, "href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), listOf("idk"), "some album")))//, mapOf("BPM" to "1000")))),
    )
    private lateinit var adapter: PlaylistAdapter

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
            val image = view.findViewById(R.id.playlist_grid_playlist_image) as ImageView
            val name  = view.findViewById(R.id.playlist_grid_playlist_name) as TextView

            name.text = getItem(position)._name

            view.setOnClickListener {
                val viewPlaylistIntent = ViewPlaylistActivity.createIntent(baseContext, getItem(position)._name)
                startActivity(viewPlaylistIntent)
            }

            return view
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            // generate activity/intent
            val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
            startActivity(generatePlaylistIntent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

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
                val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(baseContext)
                startActivity(generatePlaylistIntent)
            }
            R.id.nav_connect -> {
                val connectPlatformIntent = GeneratePlaylistActivity.createIntent(baseContext)
                startActivity(connectPlatformIntent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = HomeFragment()
}