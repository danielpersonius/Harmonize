package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class ChooseSourceActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "ChooseSourceActivity"
        fun createIntent(baseContext: Context?): Intent {
            return Intent(baseContext, ChooseSourceActivity::class.java)
        }

    }
    override fun getLogTag() = LOG_TAG
    override fun createFragment() = ChooseSourceFragment()
}