package com.example.wanderingelder

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
import androidx.compose.foundation.selection.toggleable
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
import androidx.core.app.NotificationCompat
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


class MainActivity : ComponentActivity() {


    lateinit var  geofencingClient:GeofencingClient
//    var geoFenceList : MutableList<Geofence> = ArrayList<Geofence>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var addressText:String = ""
    lateinit var geocoder : Geocoder
    lateinit var notificationManager:NotificationManager

    var state : SnackbarHostState = SnackbarHostState()
    lateinit var myGeoFencePendingIntent: PendingIntent
    lateinit var sharedPreferences: SharedPreferences

//    var geofenceDistance:Float = 100f

    @SuppressLint("NewApi", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        while(!askForPermissions()){}
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
            Intent(this, GeofenceBroadcastReceiver::class.java,),
            PendingIntent.FLAG_UPDATE_CURRENT)

        setContent {
            WanderingElderTheme {
                TabLayout()

            }
        }
    }
    @Preview
    @Composable
    fun preview()
    {
        setContent {
            WanderingElderTheme {
                TabLayout()

            }
        }
    }


    fun showMsg(s:String){
        GlobalScope.launch{
            state.showSnackbar(s)
        }
    }




//    @Composable
//    fun displayLatLong(lat:Double, long:Double)
//    {
//        Text(text = "Latitude: "+GeofenceRepo.lastLat)
//        Text(text = "Longitude: "+GeofenceRepo.lastLong)
//    }
    @OptIn(ExperimentalUnitApi::class, ExperimentalPagerApi::class)
    @Composable
    fun TabLayout() {

        val pagerState = rememberPagerState(pageCount = 3)

        Column(
            modifier = Modifier.background(Color.Gray)
        ) {
            TopAppBar(backgroundColor = Color.LightGray) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Wandering Elder Application",
                        style = TextStyle(color = Color.Black),
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
            spaceForSnackBar(state = state)
            TabsContent(pagerState = pagerState)
        }
    }
    
    @Composable
    fun spaceForSnackBar(state: SnackbarHostState)
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
                    color = Color.White
                )
            },
            backgroundColor = Color.LightGray,
            contentColor = Color.Black
        ) {
            tabsList.forEachIndexed { index, value ->
                Tab(
                    icon = {
                        Icon(imageVector = tabsList[index].second, contentDescription = null)
                    },
                    text = {
                        Text(
                            tabsList[index].first,
                            color = if (pagerState.currentPage == index) Color.Black else Color.DarkGray
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
    @ExperimentalPagerApi
    @Composable
    fun TabsContent(pagerState: PagerState) {
        val tabsList = listOf(
            "MainScreen" to Icons.Default.Home,
            "Create a Marker" to Icons.Default.Add,
            "Settings" to Icons.Default.Settings
        )
        HorizontalPager(state = pagerState) {
                page ->
            when (page) {
                0 -> launchMainScreen()
                1 -> addGeofenceScreen()
                2 -> launchSettingsScreen(this@MainActivity,this@MainActivity)
            // TabContentScreen(content = "Welcome to "+tabsList[2].first)
            }
        }
    }
    @Composable
    fun TabContentScreen(content: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = content,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun addGeofenceScreen()
    {
//        var msg by remember{mutableStateOf("no Message")}
//        var lat by remember {mutableStateOf(0.0)}
//        var long by remember {mutableStateOf(0.0)}
//        val geoFencePendingIntent:PendingIntent by lazy {
//            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        }


//        Row()
//        {
//            Column(horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center){
//                Spacer(modifier = Modifier.size(40.dp))
//                Text("Address:", textAlign = TextAlign.Center)
//            }


//        }
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

        ) {
            var markerName by remember {
            mutableStateOf("Home")
            }
            TextField(
                    value = markerName, onValueChange = { markerName = it
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
//                    fusedLocationProviderClient.lastLocation.apply {
//                        addOnSuccessListener { location ->
//                            if (location != null) {
//                                lat = location.latitude
//                                long = location.longitude
//                                addGeofence(geoFencePendingIntent, notificationManager,
//                                        lat, long, 1
//                                )
//                                addGeofence(GeofenceRepo.myGeoFencePendingIntent,
//                                GeofenceRepo.notificationManager, lat, long, 1)
//                            }
//                        }
//                    }

//                    msg = "Geofence added"

                    Log.e("Navigation", "Attempting navigation to MainScreen")
//                    navController.navigate("MainScreen")

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
                    showMsg("Failed to translate input into address")
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
        var distance:Float by remember{ mutableStateOf(100f)}
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




//    @SuppressLint("MissingPermission")
//    private fun addGeofence(geoFencePendingIntent:PendingIntent,
//                            notificationManager:NotificationManager, lat:Double, long:Double, type:Int
//    )
//    {
//       var name = getNextLocation()

//        fusedLocationProviderClient.lastLocation.apply {
//                    geoFenceList.add(
//                        Geofence.Builder()
//                            .setRequestId(name)
//                            .setCircularRegion(
//                                lat,
//                                long,
//                                100f
//                            )
//                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                            .setTransitionTypes(type
//                            )
//                            .setLoiteringDelay(1000)
//                            .build()
//                    )
////                    geofencingClient.removeGeofences(geoFencePendingIntent)
//                    geofencingClient.addGeofences(
//                        getGeofencingRequest(),
//                        geoFencePendingIntent
//                    ).run {
//                        addOnSuccessListener {
//                            println("Location(s) Added")
//                        }
//                        addOnFailureListener {
//                            println("Error. Location failed to add")
//                        }
//
//                    }
//
//                }
////            }
////        }
//        var builder = NotificationCompat.Builder(this, "1")
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
//        notificationManager.notify(1, builder.build())
//    }

//    private fun getGeofencingRequest():GeofencingRequest
//    {
//        return GeofencingRequest.Builder().apply {
//                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//                .addGeofences(geoFenceList)
//        } .build()
//    }
//    private fun createLocationRequest(): LocationRequest {
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.interval = 3000
//        mLocationRequest.fastestInterval = 3000
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        return mLocationRequest
//    }







    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForPermissions():Boolean
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
            ||
            (ActivityCompat.checkSelfPermission(this,
                SEND_SMS
            )
                    != PERMISSION_GRANTED  )
        ) {
            println("Permissions Error")

            requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION,
                SEND_SMS),
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
        return (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
                &&
        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
                &&
        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
                &&
        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)==PERMISSION_GRANTED
        )
    }
    fun getNextLocation():String
    {
        return   NameGen.getGeofenceName()
    }
}