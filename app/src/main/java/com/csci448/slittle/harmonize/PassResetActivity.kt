package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.pass_reset.*

class PassResetActivity : AppCompatActivity() {
    companion object {
        fun createIntent(baseContext: Context): Intent {
            val intent = Intent(baseContext, PassResetActivity::class.java)
            return intent
        }
        private const val LOG_TAG = "PassResetActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pass_reset)
        reset_button.setOnClickListener{resetPassword()}
        Log.d(LOG_TAG, "onCreate() called")
    }

    //Password reset will eventually send an email to the user for reset
    private fun resetPassword(){
        Toast.makeText(baseContext, "This will eventually send a reset email. Taking you to your home page", Toast.LENGTH_SHORT).show()
        val homeIntent = HomeActivity.createIntent( baseContext)
        startActivity(homeIntent)
    }
}