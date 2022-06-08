package com.example.wanderingelder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*

object GeofenceRepo
{
    lateinit var  geofencingClient: GeofencingClient
    var geoFenceList : MutableList<Geofence> = ArrayList<Geofence>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    init{
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient()
//        geofencingClient = LocationServices.getGeofencingClient(this)
//
//        var builder = NotificationCompat.Builder(this, "1")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Notification")
//            .setContentText("You are Notified")
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setCategory(NotificationCompat.CATEGORY_ALARM)
//            .setFullScreenIntent(
//                PendingIntent.getActivity(this, 0,
//                    Intent(this, GeofenceBroadcastReceiver::class.java),
//                    PendingIntent.FLAG_UPDATE_CURRENT), true)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(
//            NotificationChannel("1",
//                "Notification Channel",
//                NotificationManager.IMPORTANCE_HIGH).apply { description = "Notification Description" })
//
//
//        val geoFencePendingIntent: PendingIntent by lazy {
//            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED
//            ||
//            (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            )
//                    != PackageManager.PERMISSION_GRANTED)
//            ||
//            (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//                    != PackageManager.PERMISSION_GRANTED)
//        ) {
//            println("Permissions Error")
//
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ),
//                0)
//
//            var coarse = ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )== PackageManager.PERMISSION_GRANTED
//            var fine= ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )== PackageManager.PERMISSION_GRANTED
//            var background = ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            )== PackageManager.PERMISSION_GRANTED
//            println("Permissions:\nCoarse Location:"
//                    +coarse+"\nFine Location: "+fine+"\nBackground: "+background)
//        }
//        else
//        {
//            println("Permissions already given")
//        }
//        fusedLocationProviderClient.setMockMode(true)
//        try {
//            geofencingClient.addGeofences(getGeofencingRequest(), geoFencePendingIntent).run {
//                addOnSuccessListener {
//                    println("Location Added")
//                }
//                addOnFailureListener {
//                    println("Error. Location failed to add")
//                }
//            }
//        }catch (e:Exception)
//        {
//            println("No Geofences to add")
//        }
//
//        val locationRequest = LocationRequest.create()
//            .setInterval(3000L)
//            .setFastestInterval(3000)
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//        fusedLocationProviderClient.removeLocationUpdates(geoFencePendingIntent)
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, geoFencePendingIntent)
//        fusedLocationProviderClient.setMockLocation(Location("mockStart").apply{
//            latitude=0.0
//            longitude=0.0
//        })
    }







    private fun getGeofencingRequest(): GeofencingRequest
    {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(geoFenceList)
        } .build()
    }
}