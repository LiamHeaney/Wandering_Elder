package com.example.wanderingelder

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.time.LocalDateTime

class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    lateinit var sharedPreferences: SharedPreferences
    init{
        println("Broadcast Receiver online")

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {
        sharedPreferences =p0!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val geoFencingEvent = p1?.let { GeofencingEvent.fromIntent(it) }
        if(geoFencingEvent?.hasError() == true)
        {
            val errorMsg = GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)
            println(errorMsg)
            return
        }
//        println("Time is: "+LocalDateTime.now().hour)
//        if(LocalDateTime.now().hour<8 || LocalDateTime.now().hour>20)
        if(geoFencingEvent?.geofenceTransition?.equals(Geofence.GEOFENCE_TRANSITION_ENTER) == true)
        {


        println(geoFencingEvent?.triggeringGeofences?.get(0)?.requestId)
        val geofenceTransition = geoFencingEvent?.geofenceTransition

//        println(geoFencingEvent?.geofenceTransition)
//        val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this, geofenceTransition, triggeringGeofences)

        val notificationManager = ContextCompat.getSystemService(
            p0!!, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createNotificationChannel(NotificationChannel(
            "2", "GeoFenceAlerts",NotificationManager.IMPORTANCE_DEFAULT)
        )
//        println(p0.getString(R.string.app_name))
//        println(PendingIntent.getActivity(p0, 0, p1, PendingIntent.FLAG_UPDATE_CURRENT))

        var builder = NotificationCompat.Builder(p0, "1")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alert")
            .setContentText("Geofence "+ geoFencingEvent?.triggeringGeofences?.get(0)?.requestId+" has been broken")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(
                PendingIntent.getActivity(p0, 0,
                    Intent(p0, GeofenceBroadcastReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT), true)
        notificationManager.notify(2, builder.build())
    }


        //sendNotification(geofenceTransitionDetails)
//        Toast.makeText(p0, "You have activate my message", Toast.LENGTH_LONG)
        println("Intent Fired\n"+
                when(geoFencingEvent?.geofenceTransition){
                    Geofence.GEOFENCE_TRANSITION_DWELL-> "Dwell Event"
                    Geofence.GEOFENCE_TRANSITION_EXIT-> "Exit Event"
                    Geofence.GEOFENCE_TRANSITION_ENTER-> "Enter Event"

                    else -> {" Unknown Event"}
                }


                )
        val smsManager = SmsManager.getDefault()
        val destinationAddress:String = sharedPreferences.getString("target_phone_number", "+17038530779")?:"+17038530779"
        smsManager.sendTextMessage(destinationAddress, null, "text Message for YOU", null, null)
    }



}