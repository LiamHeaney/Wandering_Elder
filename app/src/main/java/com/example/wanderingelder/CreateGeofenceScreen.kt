package com.example.wanderingelder


import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


object CreateGeofenceScreen {
//
//    fun getNextLocation():String
//    {
//        return   NameGen.getGeofenceName()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun askForPermissions():Boolean
//    {
//        if (ActivityCompat.checkSelfPermission(this,
//                ACCESS_FINE_LOCATION
//            )
//            != PERMISSION_GRANTED
//            ||
//            (ActivityCompat.checkSelfPermission(this,
//                ACCESS_BACKGROUND_LOCATION
//            )
//                    != PERMISSION_GRANTED  )
//            ||
//            (ActivityCompat.checkSelfPermission(this,
//                ACCESS_COARSE_LOCATION
//            )
//                    != PERMISSION_GRANTED  )
//            ||
//            (ActivityCompat.checkSelfPermission(this,
//                SEND_SMS
//            )
//                    != PERMISSION_GRANTED  )
//        ) {
//            println("Permissions Error")
//
//            requestPermissions(
//                arrayOf(ACCESS_FINE_LOCATION,
//                    ACCESS_COARSE_LOCATION,
//                    ACCESS_BACKGROUND_LOCATION,
//                    SEND_SMS),
//                0)
//
//            var coarse = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)== PERMISSION_GRANTED
//            var fine= ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)== PERMISSION_GRANTED
//            var background = ActivityCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION)== PERMISSION_GRANTED
//            println("Permissions:\nCoarse Location:"
//                    +coarse+"\nFine Location: "+fine+"\nBackground: "+background)
//        }
//        else
//        {
//            println("Permissions already given")
//        }
//        return (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
//                )
//    }
//
//    private fun createLocationRequest(): LocationRequest {
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.interval = 3000
//        mLocationRequest.fastestInterval = 3000
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        return mLocationRequest
//    }
}
//@Composable
//fun launchCreateGeofenceScreen()
//{
//    var geofenceScreen =CreateGeofenceScreen
//
//}
//@SuppressLint("MissingPermission")
//@Composable
//fun addGeofenceScreen()
//{
//    var msg by remember{mutableStateOf("no Message")}
//    var lat by remember {mutableStateOf(0.0)}
//    var long by remember {mutableStateOf(0.0)}
//    val geoFencePendingIntent:PendingIntent by lazy {
//        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Gray),
//        horizontalAlignment=Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//
//    ) {
//        Button(modifier = Modifier.size(250.dp),
//            shape = CircleShape,
//            colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Red,
//                contentColor = Color.White),
//            onClick = { println("Adding Current Location")
//                Log.e("Button", "Button Clicked")
//                fusedLocationProviderClient.lastLocation.apply {
//                    addOnSuccessListener { location ->
//                        if (location != null) {
//                            lat = location.latitude
//                            long = location.longitude
//                            addGeofence(geoFencePendingIntent, notificationManager,
//                                lat, long, 1
//                            )
//                        }
//                    }
//                }
//
//                msg = "Geofence added"
//
//                Log.e("Navigation", "Attempting navigation to MainScreen")
////                    navController.navigate("MainScreen")
//
//            },content = {
//                val textPaintStroke = Paint().asFrameworkPaint().apply {
//                    isAntiAlias = true
//                    style = android.graphics.Paint.Style.STROKE
//                    textSize = 30f
//                    color = android.graphics.Color.BLACK
//                    strokeWidth = 12f
//                    strokeMiter= 10f
//                    strokeJoin = android.graphics.Paint.Join.ROUND
//                }
//
//                val textPaint = Paint().asFrameworkPaint().apply {
//                    isAntiAlias = true
//                    style = android.graphics.Paint.Style.FILL
//                    textSize = 30f
//                    color = android.graphics.Color.WHITE
//                }
//
//                Canvas(
//                    modifier = Modifier.absoluteOffset(x = -35.dp, y = -20.dp),
//                    onDraw = {
//                        drawIntoCanvas {
//                            it.nativeCanvas.drawText(
//                                "Click here to add a",
//                                0f,
//                                0.dp.toPx(),
//                                textPaintStroke
//                            )
//
//                            it.nativeCanvas.drawText(
//                                "Click here to add a",
//                                0f,
//                                0.dp.toPx(),
//                                textPaint
//                            )
//
//                        }
//                    }
//                )
//                Canvas(modifier = Modifier.absoluteOffset(x = (-50).dp, y = 20.dp),
//                    onDraw = {
//                        drawIntoCanvas {
//                            it.nativeCanvas.drawText(
//                                "geofence at this location",
//                                0f,
//                                0.dp.toPx(),
//                                textPaintStroke
//                            )
//                            it.nativeCanvas.drawText(
//                                "geofence at this location",
//                                0f,
//                                0.dp.toPx(),
//                                textPaint
//                            )
//                        }
//                    }
//                )
//
//            })
//        var text by remember {
//            mutableStateOf("")
//        }
//        Row()
//        {
//            Text("Address:", textAlign = TextAlign.Center)
//            TextField(
//                value = text, onValueChange = { text = it
//                    addressText = text},
//                label = { Text("Enter the address you would like to monitor", color = Color.Black) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
//
//                )
//        }
//
//        Button(onClick = {
//            println("Attempting to add geofence at: $addressText")
//            try{
//                var addressList = geocoder.getFromLocationName(addressText, 1)
//                lat = addressList[0].latitude
//                long = addressList[0].longitude
//                addGeofence(geoFencePendingIntent, notificationManager, lat, long, 1
//                )
//                msg = "Geofence added at: $addressText"
//            }catch(e:Exception)
//            {
//                Log.e("Error", "Failed to translate input into address")
//            }
//
//        },
//            content ={
//                Text("Add geofence at address location")
//            })
//
//
//        displayLatLong(lat, long)
//        Text(text = (msg))
//
//    }
//}
//
//
//
//
//@SuppressLint("MissingPermission")
//private fun addGeofence(geoFencePendingIntent:PendingIntent,
//                        notificationManager:NotificationManager, lat:Double, long:Double, type:Int
//)
//{
//    var name = getNextLocation()
//    fusedLocationProviderClient.lastLocation.apply {
////            addOnSuccessListener { location ->
////                if (location != null) {
////                    lastLocation = location
//        geoFenceList.add(
//            Geofence.Builder()
//                .setRequestId(name)
//                .setCircularRegion(
//                    lat,
//                    long,
//                    100f
//                )
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setTransitionTypes(type
//                )
//                .setLoiteringDelay(1000)
//                .build()
//        )
////                    geofencingClient.removeGeofences(geoFencePendingIntent)
//        geofencingClient.addGeofences(
//            getGeofencingRequest(),
//            geoFencePendingIntent
//        ).run {
//            addOnSuccessListener {
//                println("Location(s) Added")
//            }
//            addOnFailureListener {
//                println("Error. Location failed to add")
//            }
//
//        }
//
//    }
////            }
////        }
//    var builder = NotificationCompat.Builder(this, "1")
//        .setSmallIcon(R.drawable.ic_launcher_foreground)
//        .setContentTitle("GeoFence Added")
//        .setContentText(name+"  has been added at this location")
//        .setPriority(NotificationCompat.PRIORITY_MAX)
//        .setCategory(NotificationCompat.CATEGORY_ALARM)
//        .setFullScreenIntent(
//            PendingIntent.getActivity(this, 0,
//                Intent(this, GeofenceBroadcastReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT), true)
//
//    notificationManager.notify(1, builder.build())
//}
//private fun getGeofencingRequest():GeofencingRequest
//{
//    return GeofencingRequest.Builder().apply {
//        setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            .addGeofences(geoFenceList)
//    } .build()
//}

