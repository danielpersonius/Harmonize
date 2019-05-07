package com.csci448.slittle.harmonize

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DbInstance {
    companion object {
        var dbHelper: SpotifyReaderDbHelper? = null
        lateinit var writableDb : SQLiteDatabase
        lateinit var readableDb : SQLiteDatabase

        fun createDbInstance(pContext: Context) {
            if (dbHelper == null) {
                dbHelper = SpotifyReaderDbHelper(pContext)
                writableDb = (dbHelper as SpotifyReaderDbHelper).writableDatabase
                readableDb = (dbHelper as SpotifyReaderDbHelper).readableDatabase
            }
        }
    }
}