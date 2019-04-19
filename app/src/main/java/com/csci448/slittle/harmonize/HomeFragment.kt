package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.util.Log
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "HomeFragment"
        fun createFragment() : Fragment {
            val arguments = Bundle()
            val homeFragment = HomeFragment()
            homeFragment.arguments = arguments
            return homeFragment
        }
    }

    var playlists = mutableListOf(
        Playlist("href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), "some album"))),// mapOf("BPM" to "1000")))),
        Playlist("href", "1",  "playlist #1",  false, "owner", false, "playlist", "uri", listOf(Track("id","some song", listOf("some artist"), "some album")))// mapOf("BPM" to "1000"))))
        )
    private var adapter: PlaylistAdapter? = null
//    private lateinit var drawerLayout: DrawerLayout

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
            //val image = view.findViewById<ImageView>(R.id.playlist_grid_playlist_image)
            val name  = view.findViewById<TextView>(R.id.playlist_grid_playlist_name)

            name.text = getItem(position)._name

            view.setOnClickListener {
                val viewPlaylistIntent = ViewPlaylistActivity.createIntent(context, getItem(position)._name)
                startActivity(viewPlaylistIntent)
            }

            return view
        }

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.home_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
    // Handle presses on the action bar menu items
        when (item?.itemId) {
            R.id.playlist_create_option -> {
                val generateIntent = GeneratePlaylistActivity.createIntent(context)
                startActivity(generateIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter()
        home_playlist_grid.adapter = adapter
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
        Log.d(LOG_TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
    }
}