package com.csci448.slittle.harmonize

class Track(
    val _id       : String,
    val _name     : String,
    val _artists  : List<String>,
    val _album    : String,
    var _metadata : Map<String, String> = mutableMapOf()
)