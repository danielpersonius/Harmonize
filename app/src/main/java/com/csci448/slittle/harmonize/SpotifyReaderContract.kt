package com.csci448.slittle.harmonize

import android.provider.BaseColumns

object SpotifyReaderContract {
    // Table contents are grouped together in an anonymous object.
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "user_account"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val PLATFORM = "platform"
        const val ACCESS_TOKEN = "access_token"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${UserEntry.TABLE_NAME} (" +
                     "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                     "${UserEntry.USER_ID} TEXT," +
                     "${UserEntry.USER_NAME} TEXT," +
                     "${UserEntry.PLATFORM} TEXT," +
                     "${UserEntry.ACCESS_TOKEN} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}"
}