package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.fragment_generate_playlist.*

class GeneratePlaylistFragment : Fragment() {

    companion object {
        private const val LOG_TAG = "GenerateFragment"
    }

    // temporary intent creator since each button on this page launches TuneParametersActivity
    private fun launchIntent(){
//        val intent = TuneParametersActivity.createIntent(context)
        val intent = ChooseSourceActivity.createIntent(context)
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
        return inflater.inflate(R.layout.fragment_generate_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        generate_playlist_progress_circle.visibility = GONE

        generate_from_spotify_button.setOnClickListener {
            generate_playlist_progress_circle.visibility = VISIBLE
                launchIntent()
        }
        generate_from_soundcloud_button.setOnClickListener {
            // noop for now
            //generate_playlist_progress_circle.visibility = VISIBLE
            //launchIntent()
        }
        generate_from_apple_button.setOnClickListener {
            // noop for now
            //generate_playlist_progress_circle.visibility = VISIBLE
            // launchIntent()
        }
        generate_from_pandora_button.setOnClickListener {
            // noop for now
            //generate_playlist_progress_circle.visibility = VISIBLE
            // launchIntent()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOG_TAG, "onActivityCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
        generate_playlist_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        generate_playlist_progress_circle.visibility = GONE
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