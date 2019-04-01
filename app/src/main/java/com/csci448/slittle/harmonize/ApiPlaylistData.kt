package com.csci448.slittle.harmonize

import com.beust.klaxon.Json

data class ApiPlaylistData(
    @Json(name = "href")
    val _href : String,
    @Json(name = "items")
    val _playlists : List<ApiPlaylist>?,
    @Json(name = "limit")
    val _limit : Int,
    @Json(name = "next")
    val _next : String,
    @Json(name = "offset")
    val _offset : Int,
    @Json(name = "previous")
    val _previous : String,
    @Json(name = "total")
    val _total : Int
)