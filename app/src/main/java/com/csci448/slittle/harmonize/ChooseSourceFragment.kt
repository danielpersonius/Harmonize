package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_choose_source.*

class ChooseSourceFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "ChooseSourceFragment"
        fun createFragment() : Fragment {
            val arguments = Bundle()
            val chooseSourceFragment = HomeFragment()
            chooseSourceFragment.arguments = arguments
            return chooseSourceFragment
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
        setHasOptionsMenu(true)
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
        return inflater.inflate(R.layout.fragment_choose_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        // get user playlists
        val playlists = SpotifyClient.getUserPlaylists(SpotifyClient.ACCESS_TOKEN, 5, 5) as ApiPlaylistData
        for (playlist : ApiPlaylist in playlists._playlists) {
            val playlistTextView = TextView(context)
            playlistTextView.text = playlist._name
            playlistTextView.textSize = 24.0f
            playlistTextView.setOnClickListener {
                Toast.makeText(context, "${playlist._name} selected!", Toast.LENGTH_SHORT).show()
                // get this playlists tracks
                val tracks = SpotifyClient.getPlaylistTracks("0npkStKEjy4tCUGUVHGSS2") as String
                Log.d(LOG_TAG, tracks)
            }
            spotify_choose_playlist_scrollview_linearlayout.addView(playlistTextView)
        }

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