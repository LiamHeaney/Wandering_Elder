package com.example.wanderingelder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)



        geoFenceList.add(
            Geofence.Builder()
                .setRequestId("Home1")
                .setCircularRegion(38.9072, -77.0369, 1000F)
                .setExpirationDuration(5000000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build(),
        )
        val geoFencePendingIntent:PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        else
            println("Permissions Error")

//        geofencingClient.removeGeofences(geoFencePendingIntent).run{
//            addOnSuccessListener {
//                println("Location successfully removed")
//            }
//            addOnFailureListener{
//                println("Error. Location failed to remove")
//            }
//        }
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