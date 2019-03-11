package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class ViewPlaylistActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "ViewPlaylistActivity"
        /*
         * not passing a playlist object or any data other than the playlist name
         * since the view playlist fragment will query the database to retrieve
         * all that data. This frees up the home fragment from having to retrieve
         * all the data of each playlist up front and then having to passing it all
         * to this intent
         */
        fun createIntent(context: Context?, playlistName : String) : Intent {
            val intent = Intent(context, ViewPlaylistActivity::class.java)
            intent.putExtra("PLAYLIST_NAME", playlistName)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ViewPlaylistFragment()
}