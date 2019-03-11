package com.csci448.slittle.harmonize

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun getLogTag() : String

    protected abstract fun createFragment() : Fragment

    @LayoutRes
    protected open fun getLayoutResId() = R.layout.activity_single_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(getLogTag(), "onCreate() called")
        setContentView( getLayoutResId() )

        var fragment : Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if( fragment == null ) {
            fragment = createFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(getLogTag(), "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(getLogTag(), "onResume() called")
    }

    override fun onPause() {
        Log.d(getLogTag(), "onPause() called")
        super.onPause()
    }

    override fun onStop() {
        Log.d(getLogTag(), "onStop() called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(getLogTag(), "onDestroy() called")
        super.onDestroy()
    }
}