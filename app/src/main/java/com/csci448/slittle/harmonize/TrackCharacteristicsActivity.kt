package com.csci448.slittle.harmonize

import android.content.Context
import android.content.Intent
import android.util.Log

class TrackCharacteristicsActivity : SingleFragmentActivity() {
    companion object {
        private const val LOG_TAG = "CharacteristicsActivity"
        fun createIntent(context: Context?, audioFeatures : Map<String, String>): Intent {
            Log.d(LOG_TAG, "$audioFeatures")
            val intent = Intent(context, TrackCharacteristicsActivity::class.java)
            for ((featureName, featureValue) in audioFeatures) {
                intent.putExtra(featureName, featureValue)
            }
            return intent
        }
    }

    override fun getLogTag() = LOG_TAG

    override fun createFragment() = TrackCharacteristicsFragment()
}