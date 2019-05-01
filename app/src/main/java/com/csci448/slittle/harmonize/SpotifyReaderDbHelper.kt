package com.csci448.slittle.harmonize

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SpotifyReaderDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // If you change the database schema, you must increment the database version
        const val DATABASE_VERSION = 5
        const val DATABASE_NAME = "harmonize.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SpotifyReaderContract.SQL_CREATE_USER_ENTRIES)
        db.execSQL(SpotifyReaderContract.SQL_CREATE_PLAYLIST_ENTRIES)
        db.execSQL(SpotifyReaderContract.SQL_CREATE_TRACK_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SpotifyReaderContract.SQL_DELETE_USER_ENTRIES)
        db.execSQL(SpotifyReaderContract.SQL_DELETE_PLAYLIST_ENTRIES)
        db.execSQL(SpotifyReaderContract.SQL_DELETE_TRACK_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}