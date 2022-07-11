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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderingelder.MainActivity
import com.example.wanderingelder.geofences.screen.GeofencesList
import com.example.wanderingelder.geofences.screen.GeofencesScreenViewModel
import com.example.wanderingelder.geofences.screen.GeofencesScreenViewModelFactory
import com.example.wanderingelder.model.GeofenceRepo

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LaunchAddGeofencesScreen()
{

    LocalViewModelStoreOwner.current?.let { val viewModel: AddGeofenceScreenViewModel =
        viewModel(
            it,
            "AddGeofenceScreenViewModel",
            AddGeofenceScreenViewModelFactory(
                LocalContext.current.applicationContext as Application
            )
        )
        AddGeofenceScreen(viewModel = viewModel, activity = MainActivity())
    }



}
var addressText:String = ""

    lateinit var geocoder : Geocoder

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
@Composable
fun AddGeofenceScreen(viewModel: AddGeofenceScreenViewModel, activity: MainActivity)
{
    Column(
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight()
            .background(MaterialTheme.colors.background),
        horizontalAlignment=Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center

    ) {
        val focusManager = LocalFocusManager.current
        var markerName by remember {
            mutableStateOf("Home")
        }
        TextField(
            value = markerName, onValueChange = { markerName = it
            },
            label = { Text("Name Your Marker", color = MaterialTheme.colors.onBackground) },
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone ={
                    focusManager.clearFocus()
                }
            ), textStyle = TextStyle(color = MaterialTheme.colors.onBackground)
        )

        Button(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.textButtonColors(backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onBackground),
            onClick = { println("Adding Current Location")
                Log.e("Button", "Button Clicked")

                GeofenceRepo.addGeofenceAtCurrentLocation(markerName)
            },content = {
                val txtSize = 50f
                val textPaintStroke = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    textSize = txtSize
                    color = android.graphics.Color.BLACK
                    strokeWidth = 6f
                    strokeMiter= 5f
                    strokeJoin = android.graphics.Paint.Join.ROUND
                }

                val textPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.FILL
                    textSize = txtSize
                    color = android.graphics.Color.WHITE
                }

                Canvas(
                    modifier = Modifier.fillMaxSize()
                        .offset(x= LocalDensity.current.density*txtSize.dp/16,
                            y= LocalDensity.current.density*txtSize.dp/4
                        ),
                    onDraw = {
                        drawIntoCanvas {
                            it.nativeCanvas.drawText(
                                "Click here to add a geofence at this location",
                                0f,
                                0.dp.toPx(),
                                textPaintStroke,
                            )

                            it.nativeCanvas.drawText(
                                "Click here to add a geofence at this location",
                                0f,
                                0.dp.toPx(),
                                textPaint
                            )

                        }
                    },
                )
            })
        var text by remember {
            mutableStateOf("")
        }
        Row()
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center){
//                    Spacer(modifier = Modifier.size(40.dp))
//                    Text("Address:", textAlign = TextAlign.Center,
//                    color = MaterialTheme.colors.onBackground)
            }
            @Suppress("NAME_SHADOWING") val focusManager = LocalFocusManager.current
            TextField(
                value = text, onValueChange = { text = it
                    addressText = text},
                label = { Text("Enter the address you would like to monitor", color = MaterialTheme.colors.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone ={
                        focusManager.clearFocus()
                    }
                )


            )
        }

        Button(onClick = {
            println("Attempting to add geofence at: $addressText")
            try{
                val addressList = geocoder.getFromLocationName(addressText, 1)

                GeofenceRepo.addGeofence(addressList[0].latitude, addressList[0].longitude, Geofence.GEOFENCE_TRANSITION_EXIT,markerName)

            }catch(e:Exception)
            {
                activity.showMsg("Failed to translate input into address")
                Log.e("Error", "Failed to translate input into address")
            }

        },
            content ={
                Text("Add geofence at address location")
            })

        MakeSliderWithLabels()
    }
}

    @Composable
    fun MakeSliderWithLabels()
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