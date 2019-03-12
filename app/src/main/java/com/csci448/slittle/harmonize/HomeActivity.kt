package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent

class HomeActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "HomeActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, HomeActivity::class.java)
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = HomeFragment()
}