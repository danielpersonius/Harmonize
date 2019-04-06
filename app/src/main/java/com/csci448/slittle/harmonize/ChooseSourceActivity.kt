package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
//import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.menu_drawer.*

class ChooseSourceActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "ChooseSourceActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, ChooseSourceActivity::class.java)
            return intent
        }

    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = ChooseSourceFragment()
}