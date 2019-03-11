package com.csci448.slittle.harmonize

class PlatformConnectActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "PlatformConnectActivity"
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = PlatformConnectFragment()
}