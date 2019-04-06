package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class ViewApiPlaylistActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "ViewApiPlaylistActivity"
        fun createIntent(context: Context?, playlistName : String) : Intent {
            val intent = Intent(context, ViewApiPlaylistActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ViewApiPlaylistFragment()
}