package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class ExportActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "ExportActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, ExportActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ExportFragment()
}