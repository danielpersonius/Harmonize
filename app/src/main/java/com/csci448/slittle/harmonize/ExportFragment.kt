package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.export.*

class ExportFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "ExportFragment"
    }

    private lateinit var playlistTitle : String
    private var trackIds = arrayListOf<String>()
    private var tunedParameters = mutableMapOf<String, String>()


    private fun openOtherApp(uri : String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context!!.packageName))
        startActivity(intent)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
        return inflater.inflate(R.layout.export, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        // rotation
        if (savedInstanceState != null) {
            playlistTitle = savedInstanceState.getString("PLAYLIST_NAME")
            trackIds = savedInstanceState.getStringArrayList("PLAYLIST_TRACK_IDS")
            tunedParameters = savedInstanceState.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistTitle = if (extras.containsKey("PLAYLIST_NAME")) extras.getString("PLAYLIST_NAME") else "suggested playlist"
                trackIds = extras.getStringArrayList("PLAYLIST_TRACK_IDS")
                tunedParameters = extras.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
            }
        }

        export_spotify_button.setOnClickListener {
            Toast.makeText(context, "Exporting to Spotify...", Toast.LENGTH_SHORT).show()
            val playlistId = SpotifyClient.exportPlaylist(playlistTitle,
                                                                 trackIds.toList(),
                                                                 tunedParameters) as String?

            if (playlistId != null) {
                val redirectURI = "spotify:user:${SpotifyClient.USER_ID}:playlist:$playlistId"
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