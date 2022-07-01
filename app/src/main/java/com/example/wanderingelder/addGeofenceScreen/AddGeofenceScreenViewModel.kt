package com.example.wanderingelder.addGeofenceScreen

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.compose.material.SnackbarHostState
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderingelder.model.GeofenceBroadcastReceiver
import com.example.wanderingelder.model.GeofenceRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class AddGeofenceScreenViewModel (application: Application) : ViewModel(){

    lateinit var  geofencingClient: GeofencingClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var addressText:String = ""
    lateinit var geocoder : Geocoder
    lateinit var notificationManager: NotificationManager

    var state : SnackbarHostState = SnackbarHostState()
    lateinit var myGeoFencePendingIntent: PendingIntent
    lateinit var sharedPreferences: SharedPreferences
    var markerName:String = ""
    var text : String = ""

    init{
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        geocoder = Geocoder(application.applicationContext)
        Locale.getDefault()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)

        geofencingClient = LocationServices.getGeofencingClient(application.applicationContext)

        notificationManager = application.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel("1",
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH).apply { description = "Geofence added alert" })

        myGeoFencePendingIntent = PendingIntent.getBroadcast(application.applicationContext,
            0,
            Intent(application.applicationContext, GeofenceBroadcastReceiver::class.java,),
            PendingIntent.FLAG_UPDATE_CURRENT)
    }
}