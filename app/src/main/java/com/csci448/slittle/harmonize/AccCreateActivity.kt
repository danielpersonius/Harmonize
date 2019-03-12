package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.createaccount.*

class AccCreateActivity : AppCompatActivity() {
    companion object {
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, AccCreateActivity::class.java)
            return intent
        }
        private const val LOG_TAG = "AccCreateActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)
        new_acc_btn.setOnClickListener{createAccount()}
        Log.d(LOG_TAG, "onCreate() called")
    }

    //Account creation will eventually log the user's account for future logins
    private fun createAccount(){
        Toast.makeText(baseContext, "This will eventually create an account. Taking you to your home page", Toast.LENGTH_SHORT).show()
        val homeIntent = HomeActivity.createIntent( baseContext)
        startActivity(homeIntent)
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