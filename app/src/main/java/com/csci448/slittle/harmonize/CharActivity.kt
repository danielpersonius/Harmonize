package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class CharActivity : SingleFragmentActivity() {

    companion object {
        private const val LOG_TAG = "CharActivity"
        fun createIntent(baseContext: Context?): Intent {
            val intent = Intent(baseContext, CharActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = CharFragment()
}