package com.csci448.slittle.harmonize

class HomeActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "HomeActivity"
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = HomeFragment()
}