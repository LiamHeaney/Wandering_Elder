package com.example.wanderingelder

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import org.intellij.lang.annotations.JdkConstants
import java.util.jar.Attributes


class MainActivity : ComponentActivity() {

    lateinit var  geofencingClient:GeofencingClient
    var geoFenceList : MutableList<Geofence> = ArrayList<Geofence>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var lastLocation:Location =  Location("network")

    lateinit var myGeoFencePendingIntent: PendingIntent
    var gAPI = GoogleApi.Settings.Builder().build()
    //var latLong :LiveData<String> = LiveData<String>
    @SuppressLint("NewApi", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        geofencingClient = LocationServices.getGeofencingClient(this)

//        fusedLocationProviderClient.lastLocation.apply {
//            addOnSuccessListener { location ->
//                if (location != null) {
//                    println("location found")
//                    println("Location: "+location.longitude +" "+ location.latitude)
//                    geoFenceList.add(Geofence.Builder()
//                        .setRequestId("entry.key")
//                        .setCircularRegion(location.latitude, location.longitude, 50f)
//                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                        .setTransitionTypes(
//                            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLoiteringDelay(1000)
//                        .build())
//                    }
//                }
//            addOnFailureListener { println("Location Unavailable") }
//        }
        geoFenceList.add(Geofence.Builder()
            .setRequestId(getNextLocation())
            .setCircularRegion(0.0,0.0, 50f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                or Geofence.GEOFENCE_TRANSITION_EXIT
                    or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(1000)
            .build())

        var locResponses = LocationServices.getSettingsClient(this).checkLocationSettings(LocationSettingsRequest.Builder()
            .addLocationRequest(
                LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
            ).build())
        locResponses.addOnCompleteListener{
            println("responses")

        }

//        var builder = NotificationCompat.Builder(this, "1")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("GeoFence Added")
//            .setContentText("A GeoFence has been added at this location")
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setCategory(NotificationCompat.CATEGORY_ALARM)
//            .setFullScreenIntent(
//                PendingIntent.getActivity(this, 0,
//                Intent(this, GeofenceBroadcastReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT), true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel("1",
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH).apply { description = "Geofence added alert" })

        myGeoFencePendingIntent = PendingIntent.getBroadcast(this,
        0,
        Intent(this, GeofenceBroadcastReceiver::class.java,),
        PendingIntent.FLAG_UPDATE_CURRENT)
        this.sendBroadcast(Intent(this, GeofenceBroadcastReceiver::class.java))
        val geoFencePendingIntent:PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//            intent.action = ACTION_GEOFENCE_EVENT
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        geofencingClient.addGeofences(
            getGeofencingRequest(),
            geoFencePendingIntent
        ).run {
            addOnSuccessListener {
                println("Location(s) Added")
            }
            addOnFailureListener {
                println("Error. Location failed to add")
            }
        }

        setContent {
            var msg by remember{
                mutableStateOf("no Message")
            }
            var lat by remember {
            mutableStateOf(0.0)
            }
            var long by remember {
                mutableStateOf(0.0)
            }
            WanderingElderTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    horizontalAlignment=Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {
//                    Spacer(modifier = Modifier.absolutePadding(top=150.dp))
//                    Greeting("Android")
//                    Text("Blank spot")
//
                    Button(modifier = Modifier.size(250.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Red,
                            contentColor = Color.White),
                        onClick = { println("Adding Current Location")
                        Log.e("Button", "Button Clicked")
                        addGeofence(geoFencePendingIntent, notificationManager
//                            , myPendingIntent
                        )
                        msg = "Geofence added"
                        fusedLocationProviderClient.lastLocation.apply {
                            addOnSuccessListener { location ->
                                if (location != null) {
                                    lat = location.latitude
                                    long = location.longitude
                                }
                            }
                        }

                    }, content = {
                            val textPaintStroke = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                                textSize = 30f
                                color = android.graphics.Color.BLACK
                                strokeWidth = 12f
                                strokeMiter= 10f
                                strokeJoin = android.graphics.Paint.Join.ROUND
                            }

                            val textPaint = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.FILL
                                textSize = 30f
                                color = android.graphics.Color.WHITE
                            }

                            Canvas(
                                modifier = Modifier.absoluteOffset(x = -70.dp, y = -20.dp),
                                onDraw = {
                                    drawIntoCanvas {
                                        it.nativeCanvas.drawText(
                                            "Click here to add a",
                                            0f,
                                            0.dp.toPx(),
                                            textPaintStroke
                                        )

                                        it.nativeCanvas.drawText(
                                            "Click here to add a",
                                            0f,
                                            0.dp.toPx(),
                                            textPaint
                                        )

                                    }
                                }
                            )
                            Canvas(modifier = Modifier.absoluteOffset(x = (-100).dp, y = 20.dp),
                                onDraw = {
                                    drawIntoCanvas {
                                        it.nativeCanvas.drawText(
                                            "geofence at this location",
                                            0f,
                                            0.dp.toPx(),
                                            textPaintStroke
                                        )
                                        it.nativeCanvas.drawText(
                                            "geofence at this location",
                                            0f,
                                            0.dp.toPx(),
                                            textPaint
                                        )
                                    }
                                }
                            )

                    })
                    Button(onClick = { println("Location1")
                                     },
                        content ={
                        Text("Move me (Unavailable)")
                    })


                   displayLatLong(lat, long)
                    Text(text = (msg))

                }
            }
        }
    }

@Composable
fun displayLatLong(lat:Double, long:Double)
{
    Text(text = "Latitude: "+lat)
    Text(text = "Longitude: "+long)
}


    @SuppressLint("MissingPermission")
    private fun addGeofence(geoFencePendingIntent:PendingIntent,
                            notificationManager:NotificationManager
//                            , myPendingIntent:PendingIntent
    )
    {
       var name = getNextLocation()
        fusedLocationProviderClient.lastLocation.apply {
            addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                    geoFenceList.add(
                        Geofence.Builder()
                            .setRequestId(name)
                            .setCircularRegion(
                                location.latitude,
                                location.longitude,
                                100f
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(
                                Geofence.GEOFENCE_TRANSITION_ENTER
                                        or Geofence.GEOFENCE_TRANSITION_DWELL
                                        or Geofence.GEOFENCE_TRANSITION_EXIT
                            )
                            .setLoiteringDelay(1000)
                            .build()
                    )
//                    geofencingClient.removeGeofences(geoFencePendingIntent)
                    geofencingClient.addGeofences(
                        getGeofencingRequest(),
                        geoFencePendingIntent
                    ).run {
                        addOnSuccessListener {
                            println("Location(s) Added")
                        }
                        addOnFailureListener {
                            println("Error. Location failed to add")
                        }

                    }
//                    geoFenceList.add(
//                        Geofence.Builder()
//                            .setRequestId("Current_Loc2")
//                            .setCircularRegion(
//                                location.latitude,
//                                location.longitude,
//                                100f
//                            )
//                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                            .setTransitionTypes(
//                                Geofence.GEOFENCE_TRANSITION_ENTER
////                                        or Geofence.GEOFENCE_TRANSITION_DWELL
////                                        or Geofence.GEOFENCE_TRANSITION_EXIT
//                            )
//                            .setLoiteringDelay(1000)
//                            .build()
//                    )
//                    geofencingClient.addGeofences(
//                        getGeofencingRequest(),
//                        myGeoFencePendingIntent
//                    ).run {
//                        addOnSuccessListener {
//                            println("Location(s) Added")
//                        }
//                        addOnFailureListener {
//                            println("Error. Location failed to add")
//                        }
//
//                    }

                }
            }
        }
        var builder = NotificationCompat.Builder(this, "1")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("GeoFence Added")
        .setContentText(name+"  has been added at this location")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setFullScreenIntent(
            PendingIntent.getActivity(this, 0,
                Intent(this, GeofenceBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT), true)

        notificationManager.notify(1, builder.build())
    }
    private fun getGeofencingRequest():GeofencingRequest
    {
        return GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geoFenceList)
        } .build()
    }
    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000
        mLocationRequest.fastestInterval = 3000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION
            )
            != PERMISSION_GRANTED
            ||
            (ActivityCompat.checkSelfPermission(this,
                ACCESS_BACKGROUND_LOCATION
            )
                    != PERMISSION_GRANTED  )
            ||
            (ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION
            )
                    != PERMISSION_GRANTED  )
        ) {
            println("Permissions Error")

            requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION),
                0)

            var coarse = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)== PERMISSION_GRANTED
            var fine= ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)== PERMISSION_GRANTED
            var background = ActivityCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION)== PERMISSION_GRANTED
            println("Permissions:\nCoarse Location:"
                    +coarse+"\nFine Location: "+fine+"\nBackground: "+background)
        }
        else
        {
            println("Permissions already given")
        }
    }
    fun getNextLocation():String
    {
        return   NameGen.getGeofenceName()
    }
}