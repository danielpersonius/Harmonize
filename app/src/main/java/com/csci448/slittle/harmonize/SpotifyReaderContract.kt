package com.csci448.slittle.harmonize

import android.provider.BaseColumns

object SpotifyReaderContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME    = "user_account"
        const val USER_ID       = "user_id"
        const val USER_NAME     = "user_name"
        const val PLATFORM      = "platform"
        const val REFRESH_TOKEN = "refresh_token"
        const val HAS_PREMIUM   = "has_premium"
    }

    object PlaylistEntry : BaseColumns {
        const val TABLE_NAME             = "spotify_playlist"
        const val PLAYLIST_CREATED       = "playlist_created"
        const val PLAYLIST_HREF          = "playlist_href"
        const val PLAYLIST_ID            = "playlist_id"
        const val PLAYLIST_NAME          = "playlist_name"
        const val PLAYLIST_COLLABORATIVE = "playlist_collaborative"
        const val PLAYLIST_OWNER         = "playlist_owner"
        const val PLAYLIST_PUBLIC        = "playlist_public"
        const val PLAYLIST_TYPE          = "playlist_type"
        const val PLAYLIST_URI           = "playlist_uri"
    }

    // suggested tracks
    object TrackEntry : BaseColumns {
        const val TABLE_NAME = "spotify_track"
        const val TRACK_ID = "track_id"
        const val PLAYLIST_ID = "playlist_id"
        const val TRACK_NAME = "track_name"
        // breaks normal form, but not too badly
        // don't want to bother with an xref table
        const val TRACK_ARTISTS = "track_artists"
        const val TRACK_ARTISTS_IDS = "track_artists_ids"
        const val TRACK_ALBUM = "track_album"
    }

    const val SQL_CREATE_USER_ENTRIES =
        "CREATE TABLE ${UserEntry.TABLE_NAME} (" +
                     "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                     "${UserEntry.USER_ID} TEXT UNIQUE," +
                     "${UserEntry.USER_NAME} TEXT," +
                     "${UserEntry.PLATFORM} TEXT," +
                     "${UserEntry.REFRESH_TOKEN} TEXT," +
                     "${UserEntry.HAS_PREMIUM} INTEGER)"

    const val SQL_CREATE_PLAYLIST_ENTRIES =
        "CREATE TABLE ${PlaylistEntry.TABLE_NAME} (" +
                     "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                     "${PlaylistEntry.PLAYLIST_CREATED} DATETIME," +
                     "${PlaylistEntry.PLAYLIST_HREF} TEXT," +
                     "${PlaylistEntry.PLAYLIST_ID} TEXT," +
                     "${PlaylistEntry.PLAYLIST_NAME} TEXT," +
                     "${PlaylistEntry.PLAYLIST_COLLABORATIVE} INTEGER," +
                     "${PlaylistEntry.PLAYLIST_OWNER} TEXT," +
                     "${PlaylistEntry.PLAYLIST_PUBLIC} INTEGER," +
                     "${PlaylistEntry.PLAYLIST_TYPE} TEXT," +
                     "${PlaylistEntry.PLAYLIST_URI} TEXT)"

    const val SQL_CREATE_TRACK_ENTRIES =
        "CREATE TABLE ${TrackEntry.TABLE_NAME} (" +
                     "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                     "${TrackEntry.TRACK_ID} TEXT," +
                     "${TrackEntry.PLAYLIST_ID} TEXT," +
                     "${TrackEntry.TRACK_NAME} TEXT, " +
                     "${TrackEntry.TRACK_ARTISTS} TEXT, " +
                     "${TrackEntry.TRACK_ARTISTS_IDS} TEXT, " +
                     "${TrackEntry.TRACK_ALBUM} TEXT," +
                     "FOREIGN KEY(playlist_id) REFERENCES spotify_playlist(playlist_id))"

    const val SQL_DELETE_USER_ENTRIES     = "DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}"
    const val SQL_DELETE_PLAYLIST_ENTRIES = "DROP TABLE IF EXISTS ${PlaylistEntry.TABLE_NAME}"
    const val SQL_DELETE_TRACK_ENTRIES    = "DROP TABLE IF EXISTS ${TrackEntry.TABLE_NAME}"
}