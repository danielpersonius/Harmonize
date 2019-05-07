package com.csci448.slittle.harmonize

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchIntent = MainActivity.createIntent(baseContext)
        startActivity(launchIntent)
        finish()
    }
}