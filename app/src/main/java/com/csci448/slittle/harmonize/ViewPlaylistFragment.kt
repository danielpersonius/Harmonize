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


class ViewPlaylistFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "ViewPlaylistFragment"
    }

    var playlistTitle : String? = null
    // example list of songs
    var playlist = mutableListOf(
        Track("Divinity", "Porter Robinson", "Worlds", mapOf("BPM" to "90")),
        Track("Pink + White", "Frank Ocean", "Blonde", mapOf("BPM" to "160")),
        Track("All is Lost", "Getter", "Visceral", mapOf("BPM" to "75")),
        Track("Childish", "aiwake", "Childish", mapOf("BPM" to "70")),
        Track("Falls - Golden Features Remix", "ODESZA, Sasha Sloan, Golden Features", "Falls (Remixes)", mapOf("BPM" to "125")),
        Track("Alamo", "Boombox Cartel, Shoffy", "Cartel", mapOf("BPM" to "87")),
        Track("Fears", "MTNS", "Salvage", mapOf("BPM" to "70")),
        Track("Sleepless", "Flume, Jezzabell Doran", "Flume", mapOf("BPM" to "80")),
        Track("Past Life", "Ekali, Opia", "Past Life", mapOf("BPM" to "97"))
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
            val inflater        = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val trackView             = inflater.inflate(R.layout.playlist_item,null)
            val songNameTextView   = trackView.findViewById<TextView>(R.id.playlist_song_name)
            val artistNameTextView = trackView.findViewById<TextView>(R.id.playlist_artist_name)
            val albumNameTextView  = trackView.findViewById<TextView>(R.id.playlist_album_name)

            // it is name of iterator
            songNameTextView.text   = it._name
            artistNameTextView.text = it._artist
            albumNameTextView.text  = it._album

            // play song on press
            trackView.setOnClickListener {
                Toast.makeText(context, "Play song", Toast.LENGTH_SHORT).show()
            }
            // metadata(e.g. song characteristics) on long press
            trackView.setOnLongClickListener {
                val intent = CharActivity.createIntent(context)
                startActivity(intent)
                true
            }

            // todo swipe left or right to delete song from list

            tracklist_linearlayout.addView(trackView)
        }
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