package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
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
    private var newPlaylistData : Pair<String?, String?>? = null
    private var playlistRowId : Long? = null

    private fun updatePlaylistInDb(rowId : Long, href : String, id : String) : Int {
        Log.d(LOG_TAG, "updatePlaylistInDb() called")
        // Gets the data repository in write mode

        val values = ContentValues().apply {
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_HREF, href)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_ID, id)
        }

        val selection = "${BaseColumns._ID}=?"
        val selectionArgs = arrayOf(rowId.toString())
        return DbInstance.writableDb.update(SpotifyReaderContract.PlaylistEntry.TABLE_NAME,
                         values,
                         selection,
                         selectionArgs)
    }
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

        // return from spotify
        Log.d(LOG_TAG, "return data: $data")
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
            playlistTitle = savedInstanceState.getString("PLAYLIST_NAME") as String
            playlistRowId = savedInstanceState.getLong("PLAYLIST_ROW_ID")
            trackIds = savedInstanceState.getStringArrayList("PLAYLIST_TRACK_IDS") as ArrayList
            tunedParameters = savedInstanceState.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
            playlistRowId = savedInstanceState.getLong("PLAYLIST_ID")
        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistTitle =
                    if (extras.containsKey("PLAYLIST_NAME"))
                        extras.getString("PLAYLIST_NAME") as String
                    else "suggested playlist"
                playlistRowId = extras.getLong("PLAYLIST_ROW_ID")
                trackIds = extras.getStringArrayList("PLAYLIST_TRACK_IDS") as ArrayList<String>
                tunedParameters =
                        if (extras.containsKey("TUNED_PARAMETERS")) {
                            extras.getSerializable("TUNED_PARAMETERS") as HashMap<String, String>
                        }
                        else {
                            mutableMapOf()
                        }
            }
        }

        export_spotify_button.setOnClickListener {
            Toast.makeText(context, "Exporting to Spotify...", Toast.LENGTH_SHORT).show()
            newPlaylistData = SpotifyClient.exportPlaylist(playlistTitle,
                                                           trackIds.toList(),
                                                           tunedParameters)
            if (newPlaylistData != null) {
                // update db with new href and uri
                val newPlaylistId = newPlaylistData?.first
                val newPlaylistHref = newPlaylistData?.second
                if (playlistRowId != null && newPlaylistId != null && newPlaylistHref != null) {
                    val affectedRows = updatePlaylistInDb(playlistRowId as Long, newPlaylistHref, newPlaylistId)
                    if (affectedRows != 1) {
                        Log.e(LOG_TAG, "affected rows from update: $affectedRows. Could not update database")
                    }
                }
                // open spotify app
                val redirectURI = "spotify:user:${SpotifyClient.USER_ID}:playlist:$newPlaylistId"
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