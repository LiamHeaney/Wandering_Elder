package com.example.wanderingelder.model

object NameGen {
    var count = 0
    fun getGeofenceName():String
    {
        count++
        return "Geofence "+ count
    }
}