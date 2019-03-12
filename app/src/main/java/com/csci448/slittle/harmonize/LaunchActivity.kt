package com.csci448.slittle.harmonize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.harmo_login.*

class LaunchActivity : AppCompatActivity() {
    companion object {
        private const val LOG_TAG = "LaunchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.harmo_login)
        Log.d(LOG_TAG, "onCreate() called")
        login_button.setOnClickListener {LaunchLogin()}
        acc_create_button.setOnClickListener{LaunchAccCreate()}
    }


    fun LaunchLogin(){
        val loginIntent = LoginActivity.createIntent( baseContext)
        startActivity(loginIntent)

    }
    fun LaunchAccCreate(){
        val accountIntent = AccCreateActivity.createIntent( baseContext)
        startActivity(accountIntent)
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
