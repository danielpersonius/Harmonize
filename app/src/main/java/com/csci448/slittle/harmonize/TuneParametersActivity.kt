package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class TuneParametersActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "TuneParametersActivity"
        fun createIntent(baseContext: Context?): Intent {
            val intent = Intent(baseContext, TuneParametersActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = TuneParametersFragment()
}