package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
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
        true
//        when (item?.itemId) {
//            R.id.playlist_create_option -> {
//                val generateIntent = GeneratePlaylistActivity.createIntent(context)
//                startActivity(generateIntent)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        return inflater.inflate(R.layout.fragment_choose_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE

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
                val tracks = SpotifyClient.getPlaylistTracks(playlist._id) as MutableList<Track>
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        // hide spinner progress bar
        choose_source_progress_circle.visibility = GONE
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