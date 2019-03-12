package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class PlatformConnectActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "PlatformConnectActivity"
        fun createIntent(baseContext: Context?): Intent {
            val intent = Intent(baseContext, PlatformConnectActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = PlatformConnectFragment()
}