package com.csci448.slittle.harmonize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class LaunchActivity : AppCompatActivity() {
    companion object {
        private const val LOG_TAG = "LaunchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")
        // initialize singleton db
        val launchIntent = MainActivity.createIntent(baseContext)
        startActivity(launchIntent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy() called")
    }
}