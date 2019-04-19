package com.csci448.slittle.harmonize

data class Playlist(
    val href : String,
    val _id : String,
    val _name : String,
    val _collaborative : Boolean,
    val _owner: String,
    val _public: Boolean,
    val _type: String,
    val _uri: String,
    val _tracks : List<Track>?
)