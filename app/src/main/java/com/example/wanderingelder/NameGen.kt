package com.example.wanderingelder

object NameGen {
    var count = 0
    fun getGeofenceName():String
    {
        count++
        return "Geofence "+count
    }
}