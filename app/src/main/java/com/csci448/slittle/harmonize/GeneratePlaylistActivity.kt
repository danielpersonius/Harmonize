package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class GeneratePlaylistActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "GeneratePlaylistActivity"
        fun createIntent(baseContext: Context?): Intent {
            val intent = Intent(baseContext, GeneratePlaylistActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = GeneratePlaylistFragment()
}