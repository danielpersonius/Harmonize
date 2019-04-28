package com.csci448.slittle.harmonize

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_tune_parameters.*

class TuneParametersFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "TuneParametersFragment"
        fun createFragment() : Fragment {
            val arguments = Bundle()
            val tuneParametersFragment = TuneParametersFragment()
            tuneParametersFragment.arguments = arguments
            return tuneParametersFragment
        }
    }

    var playlistId : String? = null
    var seedArtists : ArrayList<String>? = arrayListOf()
    var seedGenres = mutableListOf<String>()
    var seedTracks = mutableListOf<String>()
    private val featureParameterBuffer = 50
    private val suggestedTrackLimit = 100

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
        return inflater.inflate(R.layout.fragment_tune_parameters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        // disable artist similarity for now
        artist_similarity_seekbar.isEnabled = false

        // rotation
        if (savedInstanceState != null) {
            playlistId = savedInstanceState.getString("PLAYLIST_ID")
        }
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                playlistId = extras.getString("PLAYLIST_ID") ?: null
                seedArtists = extras.getStringArrayList("PLAYLIST_ARTISTS")
//                seedGenres = extras.getStringArrayList("PLAYLIST_GENRES") as MutableList<String>
                seedTracks = extras.getStringArrayList("PLAYLIST_TRACKS") as MutableList<String>
            }
        }

        generate_playlist_button.setOnClickListener {
            if (playlistId != null) {
                // user didn't supply a value, so set to default and exclude from search
                val artistSimilarity =
                    if (artist_similarity_seekbar.progress == 0)
                        -1
                    else
                        artist_similarity_seekbar.progress
                val danceability =
                    if (danceability_parameter_seekbar.progress == 0)
                        -1
                    else
                        danceability_parameter_seekbar.progress
                val energy =
                    if (energy_parameter_seekbar.progress == 0)
                        -1
                    else
                        energy_parameter_seekbar.progress
                val speechiness =
                    if (speechiness_parameter_seekbar.progress == 0)
                        -1
                    else
                        speechiness_parameter_seekbar.progress
                val loudness =
                    if (loudness_parameter_seekbar.progress == 0)
                        -1
                    else
                        loudness_parameter_seekbar.progress
                val valence =
                    if (valence_parameter_seekbar.progress == 0)
                        -1
                    else
                        artist_similarity_seekbar.progress
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
                val viewPlaylistIntent = ViewPlaylistActivity.createIntent(context, "new playlist")
                viewPlaylistIntent.putExtra("PLAYLIST_TRACKS", ArrayList(suggestedTracks))
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