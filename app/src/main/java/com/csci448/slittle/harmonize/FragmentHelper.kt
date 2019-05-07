package com.csci448.slittle.harmonize

import android.content.Context
import android.support.v4.app.Fragment
import android.view.MenuItem

class FragmentHelper {
    companion object {
        fun handleNavItems(item : MenuItem, fragment : Fragment, context : Context) {
            when (item.itemId) {
                R.id.nav_generate -> {
                    // todo - bad spaghetti
                    var needToConnect = false
                    if (!SpotifyClient.refreshTokenIsInitialized() || !SpotifyClient.userIsLoggedIn()) {
                        val connectPlatformIntent = PlatformConnectActivity.createIntent(context, "generate")
                        fragment.startActivity(connectPlatformIntent)
                    }
                    else if (!SpotifyClient.accessTokenIsInitialized()) {
                        SpotifyClient.authorize(false)
                    }
                    if (!needToConnect) {
                        val generatePlaylistIntent = GeneratePlaylistActivity.createIntent(context)
                        fragment.startActivity(generatePlaylistIntent)
                    }
                }
                R.id.nav_connect -> {
                    val connectPlatformIntent = PlatformConnectActivity.createIntent(context, "home")
                    fragment.startActivity(connectPlatformIntent)
                }
                R.id.nav_home -> {
                    val mainActivityIntent = MainActivity.createIntent(context)
                    fragment.startActivity(mainActivityIntent)
                }
            }
        }
    }
}