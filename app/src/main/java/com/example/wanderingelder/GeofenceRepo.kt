package com.example.wanderingelder


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("StaticFieldLeak")
object GeofenceRepo
{

    var geofenceDistance:Float = 100f
    var lastLat:Double = 0.0
    var lastLong:Double = 0.0
    lateinit var  geofencingClient:GeofencingClient
    var geoFenceList : MutableList<Geofence> = ArrayList<Geofence>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var addressText:String = ""
    lateinit var geocoder : Geocoder
    lateinit var notificationManager:NotificationManager

    lateinit var myContext: Context
    lateinit var myGeoFencePendingIntent: PendingIntent
    private var geofenceList = ArrayList<Geofence>()
    private lateinit var mActivity:MainActivity

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(context: Context, activity: MainActivity)
    {
        mActivity=activity
        myContext=context
        geocoder = Geocoder(context)
        Locale.getDefault()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        geofencingClient = LocationServices.getGeofencingClient(context)

        notificationManager = context.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(
            NotificationChannel("1",
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH).apply { description = "Geofence added alert" })

        myGeoFencePendingIntent = PendingIntent.getBroadcast(context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java,),
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @SuppressLint("MissingPermission")
    fun addGeofenceAtCurrentLocation() {
        fusedLocationProviderClient.lastLocation.apply {
            addOnSuccessListener { location ->
                if (location != null) {
                   addGeofence(location.latitude, location.longitude, 1)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(
        lat: Double,
        long: Double,
        type: Int
    ) {

        println("Attempting to add geofence from REPO")
        lastLat = lat
        lastLong = long
        var name = NameGen.getGeofenceName()
        fusedLocationProviderClient.lastLocation.apply {
            geoFenceList.add(
                Geofence.Builder()
                    .setRequestId(name)
                    .setCircularRegion(
                        lat,
                        long,
                        geofenceDistance
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(type)
                    .setLoiteringDelay(1000)
                    .build()
            )
            geofencingClient.addGeofences(
                getGeofencingRequest(),
                myGeoFencePendingIntent
            ).run {
                addOnSuccessListener {

                    println("Location(s) Added")
                    mActivity.showMsg("Location Added")
                    var builder = NotificationCompat.Builder(myContext, "1")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("GeoFence Added")
                    .setContentText("$name  has been added at this location")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(
                        PendingIntent.getActivity(
                            myContext, 0,
                            Intent(myContext, GeofenceBroadcastReceiver::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT), true)

                    notificationManager.notify(1, builder.build())
                }
                addOnFailureListener {
                    println("Error. Location failed to add")
                    mActivity.showMsg("Error. Please Contact Support")
                }
            }

        }

    }

    private fun getGeofencingRequest(): GeofencingRequest
    {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(geoFenceList)
        } .build()
    }
}