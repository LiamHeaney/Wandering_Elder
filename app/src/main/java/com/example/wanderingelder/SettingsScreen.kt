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
import android.util.Log
import android.widget.EditText
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
import com.example.wanderingelder.SettingsScreen.phone_number
import com.example.wanderingelder.SettingsScreen.sharedPreferences
import com.example.wanderingelder.ui.theme.WanderingElderTheme
import com.google.accompanist.pager.*
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


object SettingsScreen {
    lateinit var sharedPreferences: SharedPreferences
    var phone_number:String = ""

}

@Composable
fun launchSettingsScreen(context:Context)
{
    sharedPreferences =context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    phone_number = sharedPreferences.getString("target_phone_number", "")?:""

    var text by remember {
        mutableStateOf(phone_number)
    }
Column(){
    TextField(value = text,
        onValueChange = {
            text = it

        },
        label = { Text("Enter phone number \"xxxxxxxxxx\n (no spaces or dashes) " +
                "of the person you would like to receive text messages", color = Color.Black) },
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
    )
    Row(horizontalArrangement = Arrangement.Center)
    {
        Button(content = {
            Text(text = "Save")
        },
            onClick = {
                sharedPreferences.edit().putString("target_phone_number", "+1$text")
            })
    }

}

}