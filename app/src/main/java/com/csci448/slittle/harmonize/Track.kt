package com.csci448.slittle.harmonize

import java.io.Serializable

class Track(val _id          : String,
            val _name        : String,
            val _artistNames : List<String>,
            val _artistIds   : List<String>,
            val _album       : String,
            var _metadata    : Map<String, String> = mutableMapOf()
           ) : Serializable {
    override fun toString(): String = "($_id, $_name, $_artistNames, $_artistIds, $_album, $_metadata)"
}