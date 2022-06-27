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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.wanderingelder.SettingsScreen.mActivity
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
    var phone_number:String = "0000000000"
    lateinit var mActivity:MainActivity

    var hours:List<String> = listOf("0:00","1:00","2:00","3:00",
        "4:00","5:00","6:00","7:00",
        "8:00","9:00","10:00","11:00")
    val time_of_day:List<String> = listOf("A.M.", "P.M.")

}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun launchSettingsScreen(context:Context, activity: MainActivity)
{
    mActivity=activity
    sharedPreferences =context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    phone_number = sharedPreferences.getString("target_phone_number", "0000000000")?:"0000000000"

    var text by remember {
        mutableStateOf(phone_number)
    }
    var phoneText by remember{
    mutableStateOf(sharedPreferences.getString("target_phone_number", "0000000000")?:"0000000000")
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
        )
    {
        Row() {
            Text(text = "Current phone number:")


            Text(text = "  +1-"+phoneText.substring(0, 3)+"-"+
                    phoneText.substring(3, 6)+"-"+
                    phoneText.substring(6))



        }

        TextField(value = text,
            onValueChange = { it ->
                if(text.length<11)
                    text = it.filter { it.isDigit() }

            },
            label = { Text("Enter phone number \"xxxxxxxxxx\n (no spaces or dashes) " +
                    "of the person you would like to receive text messages", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(10.dp, 10.dp, 10.dp, 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = MaskTransform()
        )
        Column( modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment=Alignment.CenterHorizontally)
        {
            Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly)
            {
                Spacer(modifier = Modifier.fillMaxSize(.10f))
                Button(content = {
                    Text(text = "Save")
                },
                    onClick = {
                        if (!text.isNullOrEmpty() && text.length>=10 && text!="0000000000")
                        {
                            if(text.length>10) text = text.substring(0, 10)
                            mActivity.showMsg("Saved phone number : +1-${text.substring(0,3)+"-"+text.subSequence(3,6)+"-"+text.subSequence(6, text.length)}")
                            sharedPreferences.edit().putString("target_phone_number", "$text").apply()
                            phone_number = text
                            phoneText = text
                        }
                        else
                            mActivity.showMsg("Invalid Phone Number")
                    }
                )
                Spacer(modifier = Modifier.fillMaxSize(.10f))
                Button(content = {
                    Text(text = "Clear")
                },
                    onClick = {
                        text = ""
                        phoneText = "0000000000"
                        sharedPreferences.edit().putString("target_phone_number", "0000000000").apply()
                        mActivity.showMsg("Stored Phone Number Cleared")
                    }
                )
                Spacer(modifier = Modifier.fillMaxSize(.10f))
                Spacer(modifier = Modifier.fillMaxSize(.10f))

            }
            Spacer(modifier = Modifier.fillMaxSize(.10f))
            var expandedStart by remember {
                mutableStateOf(false)
            }
            val startIcon = if (expandedStart)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            var selectedStart by remember { mutableStateOf(
                sharedPreferences.getString("startHour", SettingsScreen.hours[0])!!
                )
            }
            var startTextSize by remember { mutableStateOf(Size.Zero) }
            var expandedStartTOD by remember{
            mutableStateOf(false)}
            var selectedStartTOD by remember{ mutableStateOf(
                sharedPreferences.getString("startTimeOfDay", SettingsScreen.time_of_day[0])!!
                )
            }
            val startIconTOD = if (expandedStartTOD)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            var startTextSizeTOD by remember{ mutableStateOf(Size.Zero)}
            Row( horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically)
            {


                Text("Start Time:\t")

                OutlinedTextField(value = selectedStart,
                    onValueChange = { selectedStart = it },
                    modifier = Modifier.fillMaxWidth(0.4f)
                        .onGloballyPositioned { coordinates ->
                            startTextSize = coordinates.size.toSize()
                        },
//                    label = { Text("0:00") },
                    trailingIcon = {
                        Icon(startIcon, "contentDescription",
                            Modifier.clickable { expandedStart = !expandedStart })
                    },
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedStart,
                    onDismissRequest = { expandedStart = false }

                ) {
                    SettingsScreen.hours.forEach { label ->
                        DropdownMenuItem(onClick = {
                            selectedStart = label
                            expandedStart = false
                        }) {
                            Text(text = label)
                        }
                    }
                }

                OutlinedTextField(value = selectedStartTOD,
                    onValueChange ={selectedStartTOD = it},
                    modifier = Modifier.fillMaxWidth(0.5f)
                        .onGloballyPositioned { coordinates ->
                            startTextSizeTOD = coordinates.size.toSize()},
//                    label = {Text("A.M.", fontSize = 3.em)},
                    trailingIcon = {
                        Icon(startIconTOD,"contentDescription",
                            Modifier.clickable { expandedStartTOD = !expandedStartTOD })
                    },
                    textStyle = TextStyle(fontSize = 3.em),
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedStartTOD,
                    onDismissRequest = { expandedStartTOD = false }

                ) {
                    SettingsScreen.time_of_day.forEach { label ->
                        DropdownMenuItem(onClick = {
                            selectedStartTOD = label
                            expandedStartTOD = false
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }
            var expandedEnd by remember{
                mutableStateOf(false)}
            var selectedEnd by remember{ mutableStateOf(
                sharedPreferences.getString("endHour", SettingsScreen.hours[0])!!
                )
            }
            val endIcon = if (expandedEnd)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            var endTextSize by remember{ mutableStateOf(Size.Zero)}
            var expandedEndTOD by remember{
                mutableStateOf(false)}
            var selectedEndTOD by remember{ mutableStateOf(
                sharedPreferences.getString("endTimeOfDay", SettingsScreen.time_of_day[0])!!
                )
            }
            val endIconTOD = if (expandedEndTOD)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            var endTextSizeTOD by remember{ mutableStateOf(Size.Zero)}
            Row( horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically)
            {
                Text("End Time:  \t")

                OutlinedTextField(value = selectedEnd,
                    onValueChange ={selectedEnd = it},
                    modifier = Modifier.fillMaxWidth(0.4f)
                        .onGloballyPositioned { coordinates ->
                            endTextSize = coordinates.size.toSize()},
//                    label = {Text("0:00")},
                    trailingIcon = {
                        Icon(endIcon,"contentDescription",
                            Modifier.clickable { expandedEnd = !expandedEnd })
                    },
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedEnd,
                    onDismissRequest = { expandedEnd = false }

                ) {
                    SettingsScreen.hours.forEach { label ->
                        DropdownMenuItem(onClick = {
                            selectedEnd = label
                            expandedEnd = false
                        }) {
                            Text(text = label)
                        }
                    }
                }


                OutlinedTextField(value = selectedEndTOD,
                    onValueChange ={selectedEndTOD = it},
                    modifier = Modifier.fillMaxWidth(0.5f)
                        .onGloballyPositioned { coordinates ->
                            endTextSizeTOD = coordinates.size.toSize()},
//                    label = {Text("A.M.", fontSize = 3.em)},
                    trailingIcon = {
                        Icon(endIconTOD,"contentDescription",
                            Modifier.clickable { expandedEndTOD = !expandedEndTOD })
                    },
                    textStyle = TextStyle(fontSize = 3.em),
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedEndTOD,
                    onDismissRequest = { expandedEndTOD = false }

                ) {
                    SettingsScreen.time_of_day.forEach { label ->
                        DropdownMenuItem(onClick = {
                            selectedEndTOD = label
                            expandedEndTOD = false
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }
            Button(content = {
                Text(text = "Save Times")
            },
                onClick = {
                    sharedPreferences.edit().putString("startHour", selectedStart).apply()
                    sharedPreferences.edit().putString("startTimeOfDay", selectedStartTOD).apply()
                    sharedPreferences.edit().putString("endHour", selectedEnd).apply()
                    sharedPreferences.edit().putString("endTimeOfDay", selectedEndTOD).apply()
                    mActivity.showMsg("Marker active from $selectedStart $selectedStartTOD until $selectedEnd $selectedEndTOD")

                }
            )

        }

    }
}