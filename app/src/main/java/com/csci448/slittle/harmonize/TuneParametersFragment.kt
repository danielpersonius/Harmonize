package com.csci448.slittle.harmonize

import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tune_parameters.*
import kotlinx.android.synthetic.main.app_bar_tune_parameters.*
import kotlinx.android.synthetic.main.fragment_tune_parameters.*
import java.text.SimpleDateFormat
import java.util.*

class TuneParametersFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        fun createFragment() : Fragment {
            val arguments = Bundle()
            val tuneParametersFragment = TuneParametersFragment()
            tuneParametersFragment.arguments = arguments
            return tuneParametersFragment
        }
    }

    private var playlistId   : String? = null
    private var playlistName : String  = ""
    private var seedArtists  : ArrayList<String>? = arrayListOf()
    private var seedGenres   = mutableListOf<String>()
    private var seedTracks   = mutableListOf<String>()
    private val featureParameterBuffer = 50
    private val suggestedTrackLimit    = 100

    private fun savePlaylist(playlistTitle : String) : Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedDateTime = sdf.format(Date()) // format current date

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_CREATED, formattedDateTime)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_NAME, playlistTitle)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_COLLABORATIVE, 0)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_OWNER, SpotifyClient.USER_NAME)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_PUBLIC, 0)
            put(SpotifyReaderContract.PlaylistEntry.PLAYLIST_TYPE, "playlist")
        }

        // Insert the new row, returning the primary key value of the new row
        return DbInstance.writableDb.insert(SpotifyReaderContract.PlaylistEntry.TABLE_NAME, null, values)
    }

    private fun saveTrack(track : Track, playlistInsertId : Long) : Long? {
        // Gets the data repository in write mode

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SpotifyReaderContract.TrackEntry.TRACK_ID,          track._id)
            put(SpotifyReaderContract.TrackEntry.PLAYLIST_ID,       playlistInsertId)
            put(SpotifyReaderContract.TrackEntry.TRACK_NAME,        track._name)
            put(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS,     track._artistNames.toString())
            put(SpotifyReaderContract.TrackEntry.TRACK_ARTISTS_IDS, track._artistIds.toString())
            put(SpotifyReaderContract.TrackEntry.TRACK_ALBUM,       track._album)
        }

        // Insert the new row, returning the primary key value of the new row
        return DbInstance.writableDb.insert(SpotifyReaderContract.TrackEntry.TABLE_NAME, null, values)
    }

    private fun showDescription(parameterName : String, description : String) {
        val descriptionTextView = TextView(context)
        descriptionTextView.text = description
        descriptionTextView.setPadding(80, 10, 80, 10)
        // dialog box for input
        val builder = AlertDialog.Builder(context)

        builder.setTitle("$parameterName Description")
        builder.setView(descriptionTextView)
        builder.setPositiveButton("Okay") {_, _ -> }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_tune_parameters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tune_parameters_progress_circle.visibility = View.GONE

        val toggle = ActionBarDrawerToggle(
            activity, tune_parameters_drawer_layout, tune_parameters_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        tune_parameters_toolbar.inflateMenu(R.menu.platform_connect_options)
        tune_parameters_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.logout_spotify_option -> {

                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }

        tune_parameters_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        tune_parameters_nav_view.setNavigationItemSelectedListener(this)

//        // disable artist similarity for now
        artist_similarity_seekbar.isEnabled = false

        // rotation
        if (savedInstanceState != null) {
            playlistId = savedInstanceState.getString("PLAYLIST_ID")
        }
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistName = extras.getString("PLAYLIST_NAME") ?: ""
                playlistId   = extras.getString("PLAYLIST_ID") ?: null
                seedArtists  = extras.getStringArrayList("PLAYLIST_ARTISTS")
                seedGenres   =
                    if (extras.containsKey("PLAYLIST_GENRES"))
                        extras.getStringArrayList("PLAYLIST_GENRES") as MutableList<String>
                    else
                        mutableListOf()
                seedTracks  = extras.getStringArrayList("PLAYLIST_TRACKS") as MutableList<String>
            }
        }

        generate_playlist_button.setOnClickListener {
            if (playlistId != null) {
                tune_parameters_progress_circle.visibility = View.VISIBLE

                // user didn't supply a value, so set to default and exclude from search
                val artistSimilarity =
                    if (artist_similarity_seekbar.progress == 0)
                        -1
                    else
                        artist_similarity_seekbar.progress///2 //Dividing value in half due to Spotify's algorithm not returning results for very high percentages
                val danceability =
                    if (danceability_parameter_seekbar.progress == 0)
                        -1
                    else
                        danceability_parameter_seekbar.progress///2
                val energy =
                    if (energy_parameter_seekbar.progress == 0)
                        -1
                    else
                        energy_parameter_seekbar.progress///2
                val speechiness =
                    if (speechiness_parameter_seekbar.progress == 0)
                        -1
                    else
                        speechiness_parameter_seekbar.progress///2
                val loudness =
                    if (loudness_parameter_seekbar.progress == 0)
                        -1
                    else
                        loudness_parameter_seekbar.progress///2
                val valence =
                    if (valence_parameter_seekbar.progress == 0)
                        -1
                    else
                        artist_similarity_seekbar.progress///2

                val suggestedTracks = SpotifyClient.generatePlaylist(playlistId.toString(),
                                                                                seedArtists!!.toList(),
                                                                                seedGenres,
                                                                                seedTracks,
                                                                                suggestedTrackLimit,
                                                                                artistSimilarity,
                                                                                danceability,
                                                                                energy,
                                                                                speechiness,
                                                                                loudness,
                                                                                valence,
                                                                                featureParameterBuffer) as List<Track>

                val newPlaylistName = "'$playlistName' Suggested"
                val insertId = savePlaylist(newPlaylistName)
                if (insertId != -1L) {
                    for (track : Track in suggestedTracks) {
                        saveTrack(track, insertId)
                    }
                }

                // create notification
                val notificationId = 1
                // Create an explicit intent for an Activity in your app
                val intent = Intent(context, ViewPlaylistActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra("PLAYLIST_ROW_ID", insertId)
                // make values into strings so the spotify client can put them in the post request
                intent.putExtra("TUNED_PARAMETERS", hashMapOf(
                                                             "danceability" to "$danceability",
                                                             "energy"       to "$energy",
                                                             "speechiness"  to "$speechiness",
                                                             "loudness"     to "$loudness",
                                                             "valence"      to "$valence"
                                                         )
                                )
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val builder = NotificationCompat.Builder(context as Context, MainActivity.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.harmonize_launcher)
                    .setContentTitle("Suggested Playlist is ready!")
                    .setContentText("Your new playlist $newPlaylistName has been made")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(context as Context)) {
                    // id is a unique int for each notification that you must define
                    notify(notificationId, builder.build())
                }

                val viewPlaylistIntent = ViewPlaylistActivity.createIntent(context as Context)
                viewPlaylistIntent.putExtra("PLAYLIST_ROW_ID", insertId)
                viewPlaylistIntent.putExtra("TUNED_PARAMETERS", hashMapOf(
                                                                         "danceability" to "$danceability",
                                                                         "energy"       to "$energy",
                                                                         "speechiness"  to "$speechiness",
                                                                         "loudness"     to "$loudness",
                                                                         "valence"      to "$valence"
                                                                     )
                )
                startActivity(viewPlaylistIntent)
            }
        }

        artist_similarity_info_button.setOnClickListener {
            showDescription(getString(R.string.artist_similarity_label), getString(R.string.artist_similarity_parameter_description))
        }
        energy_parameter_info_button.setOnClickListener {
            showDescription(getString(R.string.energy_parameter_label), getString(R.string.energy_parameter_description))
        }
        danceability_parameter_info_button.setOnClickListener {
            showDescription(getString(R.string.danceability_parameter_label), getString(R.string.danceability_parameter_description))
        }
        speechiness_parameter_info_button.setOnClickListener {
            showDescription(getString(R.string.speechiness_parameter_label), getString(R.string.speechiness_parameter_description))
        }
        loudness_parameter_info_button.setOnClickListener {
            showDescription(getString(R.string.loudness_parameter_label), getString(R.string.loudness_parameter_description))
        }
        valence_parameter_info_button.setOnClickListener {
            showDescription(getString(R.string.valence_parameter_label), getString(R.string.valence_parameter_description))
        }
        artist_similarity_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                artist_similarity_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                artist_similarity_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        energy_parameter_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // current progress of SeekBar
                energy_parameter_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                energy_parameter_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        danceability_parameter_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                danceability_parameter_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                danceability_parameter_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        speechiness_parameter_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                speechiness_parameter_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                speechiness_parameter_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        loudness_parameter_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                loudness_parameter_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                loudness_parameter_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        valence_parameter_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                valence_parameter_seekbar_value.text = "$i%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                valence_parameter_seekbar_value.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FragmentHelper.handleNavItems(item, this, context as Context)
        tune_parameters_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        tune_parameters_progress_circle.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        tune_parameters_progress_circle.visibility = View.GONE
    }
}