package com.csci448.slittle.harmonize

data class Track(val _name     : String,
                 val _artist   : String,
                 val _album    : String,
                 val _metadata : Map<String, String>)