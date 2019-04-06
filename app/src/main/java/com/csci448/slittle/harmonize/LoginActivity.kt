package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.os.Bundle
import kotlinx.android.synthetic.main.existing_login.*


class LoginActivity : AppCompatActivity(){
    companion object {
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, LoginActivity::class.java)
            return intent
        }
        private const val LOG_TAG = "LoginActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.existing_login)
        forgot_pass_button.setOnClickListener{resetSend()}
        login_button.setOnClickListener{checkLogin()}
        Log.d(LOG_TAG, "onCreate() called")
    }

    //checkLogin will eventually check credentials against a database or similar
    private fun checkLogin(){
        val homeIntent = MainActivity.createIntent( baseContext)
        startActivity(homeIntent)
    }

    private fun resetSend(){
        val passIntent = PassResetActivity.createIntent( baseContext)
        startActivity(passIntent)
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