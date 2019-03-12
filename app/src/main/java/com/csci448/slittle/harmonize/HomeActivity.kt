package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
//import kotlinx.android.synthetic.main.menu_drawer.*
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class HomeActivity : SingleFragmentActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOG_TAG = "HomeActivity"
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, HomeActivity::class.java)
            return intent
        }

    }

//    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//
//        val toggle = ActionBarDrawerToggle(
//            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        )
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)





////        setHasOptionsMenu(true)
//        // Set the toolbar as the action bar
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        // Enable the app bar's "home" button, then change it to the hamburger icon
//        val actionbar: ActionBar? = supportActionBar
//        actionbar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
//        }
////        drawerLayout = findViewById(R.id.drawer_layout)
////        // Listen for open/close events and other state changes
////        drawerLayout.addDrawerListener(
////            object : DrawerLayout.DrawerListener {
////                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
////                    // Respond when the drawer's position changes
////                }
////
////                override fun onDrawerOpened(drawerView: View) {
////                    // Respond when the drawer is opened
////                }
////
////                override fun onDrawerClosed(drawerView: View) {
////                    // Respond when the drawer is closed
////                }
////
////                override fun onDrawerStateChanged(newState: Int) {
////                    // Respond when the drawer motion state changes
////                }
////            }
////        )
////
////
////        val navigationView : NavigationView = findViewById(R.id.nav_view)
////        // menu closes when an item is selected
////        navigationView.setNavigationItemSelectedListener { menuItem ->
////            // set item as selected to persist highlight
////            menuItem.isChecked = true
////            // close drawer when item is tapped
//////            drawerLayout.closeDrawers()
////
////            // Add code here to update the UI based on the item selected
////            // For example, swap UI fragments here
////
////            true
////        }
////        setSupportActionBar(toolbar)
////
////        fab.setOnClickListener { view ->
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show()
////        }
////
////        val toggle = ActionBarDrawerToggle(
////            this, drawer_view, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
////        )
////        drawer_layout.addDrawerListener(toggle)
////        toggle.syncState()
////
////        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_generate -> {
                // Handle the camera action
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    // open the drawer when the user taps on the nav drawer button
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                drawerLayout.openDrawer(GravityCompat.START)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onBackPressed() {
//        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
//            drawer_layout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }


//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        // Handle navigation view item clicks here.
//        when (item.itemId) {
//            R.id.nav_view_list -> {
//
//            }
//            R.id.nav_new_list -> {
//
//            }
//            R.id.nav_export -> {
//
//            }
//            R.id.nav_edit -> {
//
//            }
//            R.id.nav_add -> {
//
//            }
//        }
//
//        drawer_layout.closeDrawer(GravityCompat.START)
//        return true
//    }
    override fun getLogTag() = LOG_TAG

    override fun createFragment() = HomeFragment()
}