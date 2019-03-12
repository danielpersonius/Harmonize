package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.menu_drawer.*

class HomeActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "HomeActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, HomeActivity::class.java)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, home_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_view_list -> {
                
            }
            R.id.nav_new_list -> {

            }
            R.id.nav_export -> {

            }
            R.id.nav_edit -> {

            }
            R.id.nav_add -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun getLogTag() = LOG_TAG

    override fun createFragment() = HomeFragment()
}