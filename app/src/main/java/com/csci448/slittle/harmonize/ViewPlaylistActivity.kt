package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class ViewPlaylistActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "ViewPlaylistActivity"
        fun createIntent(context: Context?) : Intent {
            return Intent(context, ViewPlaylistActivity::class.java)
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ViewPlaylistFragment()
}