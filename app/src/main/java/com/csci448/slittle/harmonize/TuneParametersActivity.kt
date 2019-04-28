package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class TuneParametersActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "TuneParametersActivity"
        // need to pass playlistName so this activity can pass it along to view playlist
        fun createIntent(baseContext: Context?, playlistName : String): Intent {
            val intent = Intent(baseContext, TuneParametersActivity::class.java)
            intent.putExtra("PLAYLIST_NAME", playlistName)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = TuneParametersFragment()
}