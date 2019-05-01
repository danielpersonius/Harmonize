package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.characteristics.*

class TrackCharacteristicsFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "CharacteristicsFragment"
        fun createFragment() : Fragment {
            val arguments = Bundle()
            val trackCharacteristicsFragment = TrackCharacteristicsFragment()
            trackCharacteristicsFragment.arguments = arguments
            return trackCharacteristicsFragment
        }
    }

    private lateinit var trackName : String
    private var audioFeatures = mutableMapOf<String, String>()

    /**
     * @todo check for very small values, like instrumentalness
     */
    private fun convertValueToPercentage(decimal : String?) : String {
        var percentage = "undefined"
        if (decimal != null && decimal != "undefined") {
            percentage = "%.2f".format((decimal.toFloat() * 100))
        }
        return percentage
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
        return inflater.inflate(R.layout.characteristics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        // rotation
        if (savedInstanceState != null) {
            trackName = savedInstanceState.getString("PLAYLIST_NAME")

        }

        // extras would overwrite values from saved instance state
        else {
            val intent = activity?.intent
            val extras = intent?.extras
            if (extras != null) {
                trackName = extras.getString("track_name") ?: "Song"

                audioFeatures["danceability"]     = extras.getString("danceability")     ?: "undefined"
                audioFeatures["energy"]           = extras.getString("energy")           ?: "undefined"
                audioFeatures["loudness"]         = extras.getString("loudness")         ?: "undefined"
                audioFeatures["speechiness"]      = extras.getString("speechiness")      ?: "undefined"
                audioFeatures["acousticness"]     = extras.getString("acousticness")     ?: "undefined"
                audioFeatures["instrumentalness"] = extras.getString("instrumentalness") ?: "undefined"
                audioFeatures["liveness"]         = extras.getString("liveness")         ?: "undefined"
                audioFeatures["valence"]          = extras.getString("valence")          ?: "undefined"
                audioFeatures["tempo"]            = extras.getString("tempo")            ?: "undefined"
                audioFeatures["valence"]          = extras.getString("valence")          ?: "undefined"
                audioFeatures["time_signature"]   = extras.getString("time_signature")   ?: "undefined"
            }
        }

        val banner = trackName + " " + getString(R.string.characteristics_string)
        track_characteristics_label.text = banner
        danceability_value.text     = convertValueToPercentage(audioFeatures["danceability"])
        energy_value.text           = convertValueToPercentage(audioFeatures["energy"])
        loudness_value.text         = audioFeatures["loudness"]
        speechiness_value.text      = convertValueToPercentage(audioFeatures["speechiness"])
        acousticness_value.text     = convertValueToPercentage(audioFeatures["acousticness"])
        instrumentalness_value.text = audioFeatures["instrumentalness"]
        liveness_value.text         = convertValueToPercentage(audioFeatures["liveness"])
        valence_value.text          = convertValueToPercentage(audioFeatures["valence"])
        tempo_value.text            = audioFeatures["tempo"]
        time_signature_value.text   = audioFeatures["time_signature"]
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