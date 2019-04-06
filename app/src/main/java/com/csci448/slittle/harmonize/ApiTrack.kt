package com.csci448.slittle.harmonize

import com.beust.klaxon.Json

data class ApiTrack(
    @Json(name = "id")
    val _id : Int,
    @Json(name = "name")
    val _name : String,
    @Json(name = "type")
    val _type : String
)