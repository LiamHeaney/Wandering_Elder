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
import com.example.wanderingelder.GeofenceRepo.dataSource
import com.example.wanderingelder.database.MarkersDatabase
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


object MainScreen {

}
@Composable
fun launchMainScreen()
{

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

        Button(onClick = {
            GlobalScope.launch {dataSource.dao.clearDatabase()  }}, modifier = Modifier.background(Color.Transparent).fillMaxHeight(.8f))
        {
            Text("  Click here to Clear Database  ")

        }
    }
}