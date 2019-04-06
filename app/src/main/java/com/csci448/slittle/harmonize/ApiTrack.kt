package com.csci448.slittle.harmonize

import com.beust.klaxon.Json

data class ApiTrack(
    @Json(name = "items")
    val _tracks : List<T>
)
data class T(
    @Json(name = "track")
    val _track : T2
)

data class T2(
    @Json(name = "id")
    val _id : String,
    @Json(name = "name")
    val _name : String,
    @Json(name = "type")
    val _type : String
)

data class Artist(
    @Json(name = "id")
    val _id : Int,
    @Json(name = "name")
    val _name : String,
    @Json(name = "type")
    val _type : String
)