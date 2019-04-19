package com.csci448.slittle.harmonize

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*

import kotlinx.android.synthetic.main.fragment_view_playlist.*


class ViewApiPlaylistFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "ViewPlaylistFragment"
    }

    var playlistTitle : String? = "Playlist name"
    // example list of songs
    var playlist = mutableListOf(
        Track("id","Divinity",     listOf("Porter Robinson"), "Worlds"),//, mapOf("BPM" to "90")),
        Track("id","Pink + White", listOf("Frank Ocean"), "Blonde"),//, mapOf("BPM" to "160")),
        Track("id","All is Lost",  listOf("Getter"), "Visceral"),//, mapOf("BPM" to "75")),
        Track("id","Childish",     listOf("aiwake"), "Childish"),//, mapOf("BPM" to "70")),
        Track("id","Falls - Golden Features Remix", listOf("ODESZA, Sasha Sloan, Golden Features"), "Falls (Remixes)"),//, mapOf("BPM" to "125")),
        Track("id","Alamo", listOf("Boombox Cartel, Shoffy"), "Cartel"),//, mapOf("BPM" to "87")),
        Track("id","Fears", listOf("MTNS"), "Salvage"),//, mapOf("BPM" to "70")),
        Track("id","Sleepless", listOf("Flume, Jezzabell Doran"), "Flume"),//, "Flume", mapOf("BPM" to "80")),
        Track("id","Past Life", listOf("Ekali", "Opia"), "Past Life")//, mapOf("BPM" to "97"))
    )

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
        return inflater.inflate(R.layout.fragment_view_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(context, "press to play, long press to view characteristics", Toast.LENGTH_LONG).show()

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
            }
        }

        playlist_name_banner.text = playlistTitle

        // change name on long press
        playlist_name_banner.setOnLongClickListener {
            Toast.makeText(context, "Change name", Toast.LENGTH_SHORT).show()
            val titleEditTextBox = EditText(context)
            // dialog box for input
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Change playlist name")
            builder.setView(titleEditTextBox)
            builder.setPositiveButton("Done") {_, _ ->
                // todo persist name change
                playlist_name_banner.text = titleEditTextBox.text.toString()
            }

            builder.setNegativeButton("Cancel") {_, _ ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }

        playlist.forEach {
            val track = it
            val inflater        = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val trackView             = inflater.inflate(R.layout.playlist_item,null)
            val songNameTextView   = trackView.findViewById<TextView>(R.id.playlist_song_name)
            val artistNameTextView = trackView.findViewById<TextView>(R.id.playlist_artist_name)
            val albumNameTextView  = trackView.findViewById<TextView>(R.id.playlist_album_name)

            // it is name of iterator
            songNameTextView.text   = track._name
            // change to loop
            artistNameTextView.text = track._artists.toString()
            albumNameTextView.text  = track._album

            // play song on press
            trackView.setOnClickListener {
                Toast.makeText(context, "Play song", Toast.LENGTH_SHORT).show()
            }
            // metadata(e.g. song characteristics) on long press
            trackView.setOnLongClickListener {
                val intent = TrackCharacteristicsActivity.createIntent(context, track._name, track._metadata)
                startActivity(intent)
                true
            }

            // todo swipe left or right to delete song from list

            tracklist_linearlayout.addView(trackView)
        }



        // test get playlist tracks call
//        SpotifyClient.getPlaylistTracks("0npkStKEjy4tCUGUVHGSS2")
        // test audio features call
//        SpotifyClient.getTrackAudioFeatures()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.playlist_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
    // Handle presses on the action bar menu items
        when (item?.itemId) {
            R.id.playlist_menu_export_option -> {
                val exportIntent = ExportActivity.createIntent(context)
                startActivity(exportIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
    }
}