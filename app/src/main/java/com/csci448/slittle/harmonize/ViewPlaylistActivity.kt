package com.csci448.slittle.harmonize

class ViewPlaylistActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "ViewPlaylistActivity"
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ViewPlaylistFragment()
}