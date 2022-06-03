package com.example.wanderingelder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {
        val geoFencingEvent = p1?.let { GeofencingEvent.fromIntent(it) }
        if(geoFencingEvent?.hasError() == true)
        {
            val errorMsg = GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)
            println(errorMsg)
            return
        }

        val geofenceTransition = geoFencingEvent?.geofenceTransition

        val triggeringGeofences = geoFencingEvent?.triggeringGeofences
//        val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this, geofenceTransition, triggeringGeofences)

        val notificationManager = ContextCompat.getSystemService(
            p0!!, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createNotificationChannel(NotificationChannel(
            "1", "GeoFenceAlerts",NotificationManager.IMPORTANCE_DEFAULT)
        )


        //sendNotification(geofenceTransitionDetails)
        Toast.makeText(p0, "You have activate my message", Toast.LENGTH_LONG)
        println("Intent Fired")
    }

}