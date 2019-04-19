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
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_connect.*
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

class PlatformConnectFragment : Fragment() {
    companion object {
        private const val LOG_TAG = "PlatformConnectFragment"
    }

    private fun loginToSpotify() {
        val builder = AuthenticationRequest.Builder(PlatformConnectActivity.CLIENT_ID,
                                                                             AuthenticationResponse.Type.TOKEN,
                                                                             PlatformConnectActivity.REDIRECT_URI)
        builder.setScopes(arrayOf("streaming", "user-library-read", "playlist-read-private"))
        builder.setShowDialog(true)
        val request = builder.build()

        AuthenticationClient.openLoginActivity(activity, PlatformConnectActivity.SPOTIFY_LOGIN_REQUEST_CODE, request)
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
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        connect_progress_circle.visibility = GONE

        connect_spotify_button.setOnClickListener {
//            Toast.makeText(context, "Connecting Spotify!", Toast.LENGTH_SHORT).show()
            connect_progress_circle.visibility = VISIBLE
            loginToSpotify()
        }
        connect_apple_button.setOnClickListener {
            Toast.makeText(context, "Connecting Apple Music!", Toast.LENGTH_SHORT).show()
        }
        connect_soundcloud_button.setOnClickListener {
            Toast.makeText(context, "Connecting Soundcloud!", Toast.LENGTH_SHORT).show()
        }
        connect_pandora_button.setOnClickListener {
            Toast.makeText(context, "Connecting Pandora!", Toast.LENGTH_SHORT).show()
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