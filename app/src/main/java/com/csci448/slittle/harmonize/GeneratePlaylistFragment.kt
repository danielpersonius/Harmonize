package com.csci448.slittle.harmonize
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_generate.*
import kotlinx.android.synthetic.main.app_bar_generate.*
import kotlinx.android.synthetic.main.fragment_generate_playlist.*

class GeneratePlaylistFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    // temporary intent creator since each button on this page launches TuneParametersActivity
    private fun launchIntent(){
        startActivity(ChooseSourceActivity.createIntent(context))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) { return }
        if (data == null) { return }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generate_playlist_progress_circle.visibility = GONE

        val toggle = ActionBarDrawerToggle(
            activity, generate_drawer_layout, generate_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        generate_toolbar.inflateMenu(R.menu.platform_connect_options)
        generate_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.logout_spotify_option -> {
                }
                else -> super.onOptionsItemSelected(it)
            }
            true
        }

        generate_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        generate_nav_view.setNavigationItemSelectedListener(this)

        generate_from_spotify_button.setOnClickListener {
            generate_playlist_progress_circle.visibility = VISIBLE
                launchIntent()
        }
        generate_from_soundcloud_button.setOnClickListener {
            // noop for now
        }
        generate_from_apple_button.setOnClickListener {
            // noop for now
        }
        generate_from_pandora_button.setOnClickListener {
            // noop for now
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FragmentHelper.handleNavItems(item, this, context as Context)
        generate_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        generate_playlist_progress_circle.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        generate_playlist_progress_circle.visibility = GONE
    }
}