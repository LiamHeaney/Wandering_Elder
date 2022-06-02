package com.example.wanderingelder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(p0: Context?, p1: Intent?) {
        val geoFencingEvent = GeofencingEvent.fromIntent(p1)
        if(geoFencingEvent.hasError())
        {
            val errorMsg = GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)
            println(errorMsg)
            return
        }

        val geofenceTransition = geoFencingEvent.geofenceTransition

        val triggeringGeofences = geoFencingEvent.triggeringGeofences
//        val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this, geofenceTransition, triggeringGeofences)

        //sendNotification(geofenceTransitionDetails)
        Toast.makeText(p0, "You have activate my message", Toast.LENGTH_LONG)
        println("Intent Fired")
    }

}