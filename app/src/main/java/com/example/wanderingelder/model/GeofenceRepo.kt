package com.example.wanderingelder.model


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.wanderingelder.MainActivity
import com.example.wanderingelder.database.Marker
import com.example.wanderingelder.database.MarkersDatabase
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


@SuppressLint("StaticFieldLeak")
object GeofenceRepo
{

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    var geofenceDistance:Float = 100f
    private var lastLat:Double = 0.0
    private var lastLong:Double = 0.0
    private lateinit var  geofencingClient:GeofencingClient
    private var geoFenceList : MutableList<Geofence> = ArrayList()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    var addressText:String = ""
    private lateinit var geocoder : Geocoder
    lateinit var notificationManager:NotificationManager

    private var loading = true
    private lateinit var myContext: Context
    private lateinit var myGeoFencePendingIntent: PendingIntent
//    private var geofenceList = ArrayList<Geofence>()
    private lateinit var mActivity: MainActivity
    lateinit var dataSource:MarkersDatabase
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(context: Context, activity: MainActivity)
    {
        sharedPreferences =context.getSharedPreferences("prefs", MODE_PRIVATE)
        dataSource = MarkersDatabase.getInstance(context)
        mActivity =activity
        myContext =context
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
            Intent(context, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)

        coroutineScope.launch {
//            dataSource.dao.clearDatabase()
            loadGeofencesFromDB()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun addGeofenceAtCurrentLocation(name:String="Home") {
        fusedLocationProviderClient.lastLocation.apply {
            addOnSuccessListener { location ->
                if (location != null) {
                   addGeofence(location.latitude, location.longitude, Geofence.GEOFENCE_TRANSITION_EXIT, name)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun addGeofence(
        lat: Double,
        long: Double,
        type: Int = Geofence.GEOFENCE_TRANSITION_EXIT,
        name:String,
        silent:Boolean = false,
//        distance:Float = geofenceDistance
    ) {

        println("Attempting to add geofence from REPO")
        lastLat = lat
        lastLong = long
        CoroutineScope(Dispatchers.Default).launch {
//            if(true){
            if(locConflict(lat, long)){
                if(!silent)
                {
                    println("Location Failed to Add")
                    mActivity.showMsg("Failure! Location Too Close to another Location!")
                }

            }
            else if(nameConflict(name)){
                if(!silent)
                {
                    println("Location Failed to Add")
                    mActivity.showMsg("Failure! Identifier already in use. Please rename the Marker")
                }

            }
            else{
                fusedLocationProviderClient.lastLocation.apply {
                    val geofence = Geofence.Builder()
                        .setRequestId(name)
                        .setCircularRegion(
                            lat,
                            long,
                            geofenceDistance
                        ).setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(type)
                        .setLoiteringDelay(1000)
                        .build()
                    geoFenceList.add( geofence)
                    geofencingClient.addGeofences(
                        getGeofencingRequest(),
                        myGeoFencePendingIntent
                    ).run {
                        addOnSuccessListener {
                            coroutineScope.launch {
                                addToDatabase(lat, long, name)
                            }

                            if(!silent)
                            {
                                println("Location(s) Added")
                                mActivity.showMsg("$name Added")
                            }

                        }
                        addOnFailureListener {
                            println("Error. Location failed to add")
                            mActivity.showMsg("Error. Please Contact Support")
                        }
                    }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun addToDatabase(lat:Double, long:Double, name:String)
    {
        val conflict = locConflict(lat,long) || nameConflict(name)
        println("Conflict?: $conflict")
        println("Database Size: "+ dataSource.dao.getSize()+1)
        if(!conflict && dataSource.dao.getSize()<=100)
            dataSource.dao.insert(
                Marker(
                    startTime = sharedPreferences.getString("startHour", "00:00")!!,
                    endTime =  sharedPreferences.getString("endHour", "00:00")!!,
                    latitude=lat,
                    longitude=long,
                    creationDate = LocalDateTime.now().toString(),
                    name = name,
                    type = Geofence.GEOFENCE_TRANSITION_EXIT,
                    size = geofenceDistance
                )
            )
        println("databaseStuff")
        val markers = dataSource.dao.getAllMarkers()
        for (x in markers)
            println(x)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadGeofencesFromDB()
    {
        for((count, x) in dataSource.dao.getAllMarkers().withIndex())
        {
            if(count<100){
                addGeofence(
                    lat = x.latitude,
                    long =x.longitude,
                    type = Geofence.GEOFENCE_TRANSITION_EXIT,
                    name = x.name,
                loading
                )
                println("Added to Fences: $x")
            }
            else
                println("TOO MANY ENTRIES IN DATABASE")

        }
        loading = false
    }
    private fun locConflict(lat:Double, long: Double):Boolean
    {
//        var conflict = false
        for(x in dataSource.dao.getAllMarkers())
            if(abs((x.longitude-long)) <1 && abs((x.latitude-lat)) <1)
                return true
        return false
    }
    private fun nameConflict(name:String):Boolean
    {
//        var conflict = false
        for(x in dataSource.dao.getAllMarkers())
            if(name == x.name)
                return true
        return false
    }
}