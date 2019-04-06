package com.csci448.slittle.harmonize

import com.beust.klaxon.Json

data class ApiPlaylist(
    @Json(name = "collaborative")
    val _collaborative : Boolean,
    @Json(name = "external_urls")
    val _external_urls : String,
    @Json(name = "href")
    val href : String,
    @Json(name = "id")
    val _id: String,
    @Json(name = "name")
    val _name: String,
    @Json(name = "owner")
    val _owner: String,
    @Json(name = "primary_color")
    val _primary_color: String?,
    @Json(name = "public")
    val _public: Boolean,
    @Json(name = "snapshot_id")
    val _snapshot_id: String,
    @Json(name = "type")
    val _type: String,
    @Json(name = "uri")
    val _uri: String
//    @Json(name = "tracks")
//    val _tracks: List<ApiTrack>?
)