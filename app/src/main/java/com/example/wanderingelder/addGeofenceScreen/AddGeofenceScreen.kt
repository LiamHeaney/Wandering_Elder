package com.example.wanderingelder.addGeofenceScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.Geofence

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.app.ActivityCompat
import com.example.wanderingelder.SettingScreen.launchSettingsScreen
import com.example.wanderingelder.geofences.screen.launchGeofencesScreen
import com.example.wanderingelder.model.GeofenceBroadcastReceiver
import com.example.wanderingelder.model.GeofenceRepo
import com.example.wanderingelder.model.NameGen
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AddGeofenceScreen {

}
    var addressText:String = ""

    lateinit var geocoder : Geocoder

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Composable
    fun addGeofenceScreen(viewModel: AddGeofenceScreenViewModel)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            horizontalAlignment= Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            var markerName by remember {
                mutableStateOf("Home")
            }
            TextField(
                value = markerName, onValueChange = {markerName = it
                },
                label = { Text("Name Your Marker", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
            )
            Button(modifier = Modifier.size(150.dp),
                shape = CircleShape,
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Red,
                    contentColor = Color.White),
                onClick = { println("Adding Current Location")
                    Log.e("Button", "Button Clicked")

                    GeofenceRepo.addGeofenceAtCurrentLocation(markerName)
                },content = {
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
                        modifier = Modifier.absoluteOffset(x = -55.dp, y = -20.dp),
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
                    Canvas(modifier = Modifier.absoluteOffset(x = (-60).dp, y = 20.dp),
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
            var text by remember {
                mutableStateOf("")
            }
            Row()
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){
                    Spacer(modifier = Modifier.size(40.dp))
                    Text("Address:", textAlign = TextAlign.Center)
                }

                TextField(
                    value = text, onValueChange = { text = it
                        addressText = text},
                    label = { Text("Enter the address you would like to monitor", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),

                    )
            }

            Button(onClick = {
                println("Attempting to add geofence at: $addressText")
                try{
                    var addressList = geocoder.getFromLocationName(addressText, 1)
//                    lat = addressList[0].latitude
//                    long = addressList[0].longitude
                    GeofenceRepo.addGeofence(addressList[0].latitude, addressList[0].longitude, Geofence.GEOFENCE_TRANSITION_EXIT,markerName)
//                    addGeofence(geoFencePendingIntent, notificationManager, lat, long, 1
//                    )
//                    showMsg("Geofence added at: $addressText")
                }catch(e:Exception)
                {
//                    showMsg("Failed to translate input into address")
                    Log.e("Error", "Failed to translate input into address")
                }

            },
                content ={
                    Text("Add geofence at address location")
                })

            makeSliderWithLabels()
//           displayLatLong(GeofenceRepo.lastLat, GeofenceRepo.lastLong)
//            Text(text = (msg))

        }
    }

    @Composable
    fun makeSliderWithLabels()
    {
        var distance:Float by remember{ mutableStateOf(100f) }
        Column() {
            Row() {
                Text("50m")
                Spacer(modifier = Modifier.size(maxOf(0f,((distance-125)*.50f)).dp, 10.dp))
                Text(""+(distance-distance%25).toInt()+"m")
            }

            Slider(value = distance,
                onValueChange = {distance = it},
                steps = 9,
                colors = SliderDefaults.colors(),
                modifier = Modifier.fillMaxWidth(.60f),
                valueRange = 50f..500f,
                onValueChangeFinished = {
                    GeofenceRepo.geofenceDistance = distance
                    Log.e("Geofence", "Geofence Distance set to ${GeofenceRepo.geofenceDistance}")
                }
            )
        }
}