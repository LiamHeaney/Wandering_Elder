package com.example.wanderingelder

import android.Manifest
import android.Manifest.permission.*
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


class MainActivity : ComponentActivity() {

    lateinit var  geofencingClient:GeofencingClient
    var geoFenceList : MutableList<Geofence> = ArrayList<Geofence>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)



        geoFenceList.add(
            Geofence.Builder()
                .setRequestId("Home1")
                .setCircularRegion(38.9072, -77.0369, 1000F)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build(),
        )
        geoFenceList.add(
            Geofence.Builder()
                .setRequestId("Home2")
                .setCircularRegion(37.9072, -77.0369, 1000F)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build(),
        )
        geoFenceList.add(
            Geofence.Builder()
                .setRequestId("Home3")
                .setCircularRegion(38.9072, -78.0369, 1000F)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build(),
        )
        val geoFencePendingIntent:PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
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

        }
        geofencingClient.addGeofences(getGeofencingRequest(), geoFencePendingIntent).run {
            addOnSuccessListener {
                println("Location Added")
            }
            addOnFailureListener{
                println("Error. Location failed to add")
            }
        }



        setContent {
            WanderingElderTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize()

                ) {
                    Greeting("Android")

                    Text("Blank spot")
                }
            }
        }

        startLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION),
                0)
        }
//        fusedLocationProviderClient.requestLocationUpdates(LocationRequest(),
//        LocationCallback(),
//        Looper.getMainLooper())
    }
    private fun getGeofencingRequest():GeofencingRequest
    {
        return GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geoFenceList)
        } .build()
    }
}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WanderingElderTheme {
        Greeting("Android")
    }
}