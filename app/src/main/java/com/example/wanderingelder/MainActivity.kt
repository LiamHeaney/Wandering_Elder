package com.example.wanderingelder

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_PERMISSIONS
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wanderingelder.SettingScreen.launchSettingsScreen
import com.example.wanderingelder.addGeofenceScreen.LaunchAddGeofencesScreen
import com.example.wanderingelder.geofences.screen.LaunchGeofencesScreen
import com.example.wanderingelder.model.GeofenceBroadcastReceiver
import com.example.wanderingelder.model.GeofenceRepo
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.Permissions
import java.util.*
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    lateinit var  geofencingClient:GeofencingClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var addressText:String = ""
    lateinit var geocoder : Geocoder
    lateinit var notificationManager:NotificationManager
    var state : SnackbarHostState = SnackbarHostState()
    lateinit var myGeoFencePendingIntent: PendingIntent
    lateinit var sharedPreferences: SharedPreferences
    val requestPermissionLauncher =registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if(isGranted){}
            else showMsg("Error! permissions not found. System cannot function")}

    @SuppressLint("NewApi", "MissingPermission", "UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("ControlFlowWithEmptyBody")
        askForPermissions()

        GeofenceRepo.initialize(this, this@MainActivity)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        geocoder = Geocoder(this)
        Locale.getDefault()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        geofencingClient = LocationServices.getGeofencingClient(this)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel("1",
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH).apply { description = "Geofence added alert" })

        myGeoFencePendingIntent = PendingIntent.getBroadcast(this,
        0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)


        setContent {

            WanderingElderTheme {
                TabLayout()
            }
        }

    }


    fun showMsg(s:String){
        CoroutineScope(Dispatchers.Default).launch {
            state.showSnackbar(s)
        }
    }




    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalUnitApi::class, ExperimentalPagerApi::class)
    @Composable
    fun TabLayout() {

        val pagerState = rememberPagerState(pageCount = 3)

        Column(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            TopAppBar(backgroundColor = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Wandering Elder Application",
                        style = TextStyle(MaterialTheme.colors.onBackground),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(
                            18F,
                            TextUnitType.Sp
                        ),
                        modifier = Modifier.padding(all = Dp(5F)),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Tabs(pagerState = pagerState)
            SpaceForSnackBar(state = state)
            TabsContent(pagerState = pagerState)
        }
    }
    
    @Composable
    fun SpaceForSnackBar(state: SnackbarHostState)
    {
        if(state.currentSnackbarData?.message != null)
        {
            SnackbarHost(hostState = state)
        }
        else
        {
            Spacer(modifier = Modifier
                .size(1.dp, 72.dp)
                .fillMaxWidth())
        }
       
                
    }
    
    
    @ExperimentalPagerApi
    @Composable
    fun Tabs(pagerState: PagerState) {

        val tabsList = listOf(
            "MainScreen" to Icons.Default.Home,
            "Create a Marker" to Icons.Default.Add,
            "Settings" to Icons.Default.Settings
        )
        val scope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage,
            indicator = {
                tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color =MaterialTheme.colors.onBackground
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        ) {
            tabsList.forEachIndexed { index, _ ->
                Tab(
                    icon = {
                        Icon(imageVector = tabsList[index].second, contentDescription = null)
                    },
                    text = {
                        Text(
                            tabsList[index].first,
                            color = if (pagerState.currentPage == index)
                                MaterialTheme.colors.primary
                            else
                                MaterialTheme.colors.primaryVariant
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                            Log.e("Pager", "Moving to new tab")
                        }
                    }
                )
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalPagerApi
    @Composable
    fun TabsContent(pagerState: PagerState) {
        HorizontalPager(state = pagerState) {
                page ->
            when (page) {
                0 -> LaunchGeofencesScreen()
                1 -> LaunchAddGeofencesScreen()
                2 -> launchSettingsScreen(this@MainActivity,this@MainActivity)
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForSMSPermission():Boolean
    {
        Log.e("Permissions", "Checking SMS permissions")
//        ActivityCompat.requestPermissions(this, arrayOf(SEND_SMS), 43)
        when{
            ContextCompat.checkSelfPermission(this, SEND_SMS)== PERMISSION_GRANTED->{
                //fantastic
            }
            shouldShowRequestPermissionRationale(SEND_SMS)->{
                requestPermissionLauncher.launch(SEND_SMS)
            }
            else -> requestPermissionLauncher.launch(SEND_SMS)
        }
        return true
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForCoarseLocationPermission():Boolean
    {
        Log.e("Permissions", "Checking SMS permissions")
        when{
            ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)== PERMISSION_GRANTED->{
                //fantastic
            }
            shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }
            else -> requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
        }
        return true
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForBackgroundLocationPermission():Boolean
    {
        Log.e("Permissions", "Checking SMS permissions")
        when{
            ContextCompat.checkSelfPermission(this, ACCESS_BACKGROUND_LOCATION)== PERMISSION_GRANTED->{
                //fantastic
            }
            shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
            }
            else -> requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
        }
        return true
    }
    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForFineLocationPermissions():Boolean
    {
        Log.e("Permissions", "Checking permissions")
        when{
            ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)== PERMISSION_GRANTED->{
                //fantastic
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
            else -> requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
        return true
    }


    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForPermissions():Boolean
    {
        //This needs to be modified.
        //Possibly split into a coroutine with consecutive calls after each completes
        //User should be prompted for each permission, but asking for them too quickly is rejected
        askForFineLocationPermissions()
        askForBackgroundLocationPermission()
        askForSMSPermission()
        askForCoarseLocationPermission()
        return true
    }
}