package com.example.wanderingelder

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
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
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var  geofencingClient:GeofencingClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var geocoder : Geocoder
    var state : SnackbarHostState = SnackbarHostState()
    lateinit var myGeoFencePendingIntent: PendingIntent
    lateinit var sharedPreferences: SharedPreferences
    private val requestPermissionLauncher =registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if(isGranted){}
            else showMsg("Error! permissions not found. System cannot function")}

    @SuppressLint("NewApi", "MissingPermission", "UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Self explanatory, but we ask for permissions here.
        //This ensures that permissions are requested as soon as the app starts
        @Suppress("ControlFlowWithEmptyBody")
        askForPermissions()

        //This initializes the Geofence Repo, setting up necessary preparations for using Geofences
        GeofenceRepo.initialize(this, this@MainActivity)
        //Initialization of sharedPreferences, which allows us access to small-scale persistent
        //storage. This is primarily used to store settings
        // in this case we use it to store and retrieve phone numbers and time settings
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //This sets up the geocoder, so we can reverse-engineer longitude/latitude coordinates
        geocoder = Geocoder(this)
        //This sets the Locale, which is important for the geocoder to understand the addresses we give it
        Locale.getDefault()
        //Initialization of fusedLocationProvider, which we use to access location data
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //Initialization of The Geofence client, which is used to make/manage geofences
        geofencingClient = LocationServices.getGeofencingClient(this)
        //The pending intent is used to create geofences that can activate in the background of the application
        myGeoFencePendingIntent = PendingIntent.getBroadcast(this,
        0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)

        //This starts the compose-UI section, after initialization of all the previous parts
        setContent {
            WanderingElderTheme {
                TabLayout()
            }
        }

    }

    //This is a small function that allows us to send messages to the user that appear in the application
    //They will show one at a time, in the order they were created
    //This is not always desireable, if many messages are sent in a short time it can clog up the message display snackbar
    fun showMsg(s:String){
        CoroutineScope(Dispatchers.Default).launch {
            state.showSnackbar(s)
        }
    }



    //This is a tabbed layout framework. It provides the Application name at the top,
    //The has 3 tabs the user can click on, each with differing UIs
    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalUnitApi::class, ExperimentalPagerApi::class)
    @Composable
    fun TabLayout() {

        val pagerState = rememberPagerState(pageCount = 3)

        Column(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            //This creates the application name at the top
            TopAppBar(backgroundColor = MaterialTheme.colors.secondary) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Wandering Elder Application",
                        style = TextStyle(MaterialTheme.colors.onSecondary),
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
            //This calls another function to makes tabs
            Tabs(pagerState = pagerState)
            //This the the space where we display messages to the user
            //If we have no messages, we create a spacer, so there is blank space instead
            SpaceForSnackBar(state = state)
            //Finally, we call the function that creates the actual content of the tabs
            //We pass into this function the pagerstate, which is just the tab the user is on
            //That way, we know which tab's content to display
            TabsContent(pagerState = pagerState)
        }
    }

    //This ensures that we either show a message, or the area is blank
    //Otherwise, each message pushes the other UI elements down when it shows up
    //This was deemed to be aggravating to the user, when UI elements keep shifting so they cannot click
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
    
    //This creates the tabs and sets their icons
    @ExperimentalPagerApi
    @Composable
    fun Tabs(pagerState: PagerState) {

        //Here we set the names of the tabs and link them to default icons
        val tabsList = listOf(
            "MainScreen" to Icons.Default.Home,
            "Create a Marker" to Icons.Default.Add,
            "Settings" to Icons.Default.Settings
        )
        //This is used later to launch coroutines
        //Primarily to animate scrolling between different tabs
        val scope = rememberCoroutineScope()
        //This creates the actual tabs to click on
        TabRow(selectedTabIndex = pagerState.currentPage,
            indicator = {
                tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color =MaterialTheme.colors.onSecondary
                )
            },
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
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
                                MaterialTheme.colors.onSecondary
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
    //This simply maps the tabs to the Compose functions for their content
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalPagerApi
    @Composable
    fun TabsContent(pagerState: PagerState) {
        HorizontalPager(state = pagerState) {
                page ->
            when (page) {
                0 -> LaunchGeofencesScreen()
                1 -> LaunchAddGeofencesScreen(this@MainActivity)
                2 -> launchSettingsScreen(this@MainActivity,this@MainActivity)
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun askForSMSPermission():Boolean
    {
        Log.e("Permissions", "Checking SMS permissions")
        when{
            ContextCompat.checkSelfPermission(this, SEND_SMS)== PERMISSION_GRANTED->{
                //fantastic
            }
            //If we should request it, do so.
            shouldShowRequestPermissionRationale(SEND_SMS)->{
                requestPermissionLauncher.launch(SEND_SMS)
            }
            //Request it anyways. Requesting Android Permissions is really unreliable
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
            //If we should request it, do so.
            shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }
            //Request it anyways. Requesting Android Permissions is really unreliable
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
            //If we should request it, do so.
            shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
            }
            //Request it anyways. Requesting Android Permissions is really unreliable
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
            //If we should request it, do so.
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)->{
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
            //Request it anyways. Requesting Android Permissions is really unreliable
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
        //Currently, we bypass this by requesting permissions on app startup,
        //then also when the users try to add a geofence or save a phone number
        //This lets us have the minimum need two calls to get necessary permissions
        //But this is certainly not best practice.
        //In addition, the requests should be better explained to the user
        askForFineLocationPermissions()
        askForBackgroundLocationPermission()
        askForCoarseLocationPermission()
        return true
    }


}