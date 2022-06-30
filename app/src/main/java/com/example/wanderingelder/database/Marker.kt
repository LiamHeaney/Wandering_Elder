package com.example.wanderingelder.database

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.Geofence
import java.sql.Time
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "Markers_Table")
data class Marker @RequiresApi(26) constructor(
    @ColumnInfo(name = "Latitude")
    var latitude:Double = 0.0,
    @ColumnInfo(name = "Longitude")
    var longitude:Double = 0.0,
    @ColumnInfo(name = "Creation_Date")
    var creationDate:String = LocalDateTime.now().toString(),
    @ColumnInfo(name = "Start_Time")
    var startTime:String = "00:00",
    @ColumnInfo(name = "End_Time")
    var endTime:String = "00:00",
    @ColumnInfo(name = "Name")
    var name:String = "DFLT",
    @ColumnInfo(name = "Type")
    var type:Int = Geofence.GEOFENCE_TRANSITION_EXIT,
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
)