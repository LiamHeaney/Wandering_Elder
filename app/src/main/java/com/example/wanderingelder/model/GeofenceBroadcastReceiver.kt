package com.example.wanderingelder.model

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.telephony.SmsManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.wanderingelder.R
import com.example.wanderingelder.model.GeofenceRepo.dataSource
import com.example.wanderingelder.model.GeofenceRepo.notificationManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

//This handles sending text messages when a geofence is broken
class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    var lastActivation:LocalDateTime= LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {

        sharedPreferences =p0!!.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        //This ensures a max of 1 message per 5 minutes
        if(LocalDateTime.now() < lastActivation.plusMinutes(5))
            return
        else
            lastActivation = LocalDateTime.now()


        val geoFencingEvent = p1?.let { GeofencingEvent.fromIntent(it) }
        if(geoFencingEvent?.hasError() == true)
        {
            val errorMsg = GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)
            println(errorMsg)
            return
        }
        //This is not currently functional,
        //but is for later versions of the code that will allow for individual geofences to have their own times
//        CoroutineScope(Dispatchers.Default).launch {
//            geoFencingEvent?.triggeringGeofences?.get(0)?.requestId?.let {
//                var marker = dataSource.dao.get(it)
//
//                if(marker!=null)
//                {
//                    var startTime = marker.startTime.substring(0, 2).toInt()
//                    var endTime = marker.endTime.substring(0, 2).toInt()
//                }
//
//
//
//
//            }
//        }

        println("Time is: "+LocalDateTime.now().hour)
        var startTime = sharedPreferences.getString("startHour", "0:00")?.substring(0, 1)?.toInt()?:0
        var endTime = sharedPreferences.getString("endHour", "0:00")?.substring(0, 1)?.toInt()?:0
        startTime+= if(sharedPreferences.getString("startTimeTOD", "A.M.")=="A.M.")12 else 0
        endTime+= if(sharedPreferences.getString("endTimeTOD", "A.M.")=="A.M.")12 else 0

        val hour = LocalDateTime.now().hour
        if(hour in (startTime + 1) until endTime
            ||
            (endTime<startTime && (hour>startTime || hour < endTime) )
        )
        if(geoFencingEvent?.geofenceTransition?.equals(Geofence.GEOFENCE_TRANSITION_EXIT) == true)
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

//                when(geoFencingEvent?.geofenceTransition){
////                    Geofence.GEOFENCE_TRANSITION_DWELL-> "Dwell Event"
//                    Geofence.GEOFENCE_TRANSITION_EXIT-> "Exit Event"
////                    Geofence.GEOFENCE_TRANSITION_ENTER-> "Enter Event"
//
//                    else -> {" Unknown Event"}
//                }



        val smsManager = SmsManager.getDefault()
        val destinationAddress:String = sharedPreferences.getString("target_phone_number", "+10000000000")!!
        if(destinationAddress != "+10000000000")
            smsManager.sendTextMessage(destinationAddress, null,
                "Geofence "+ geoFencingEvent?.triggeringGeofences?.get(0)?.requestId+" has been broken",
                null, null)
        else
        {
            var builder = NotificationCompat.Builder(p0, "3")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alert")
                .setContentText("Cannot send Text Message: Number Invalid")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(
                    PendingIntent.getActivity(p0, 0,
                        Intent(p0, GeofenceBroadcastReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT), true)
            notificationManager.notify(3, builder.build())
        }
//        var tempFence : Geofence? = null
//        for(x in GeofenceRepo.geoFenceList)
//        {
//            if(x.requestId == geoFencingEvent?.triggeringGeofences?.get(0)?.requestId) {
//                tempFence = x
//                break
//            }
//        }

    }



}