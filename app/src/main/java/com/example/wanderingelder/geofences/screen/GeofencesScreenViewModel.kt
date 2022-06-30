package com.example.wanderingelder.geofences.screen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderingelder.database.Dao
import com.example.wanderingelder.database.DatabaseRepo
import com.example.wanderingelder.database.Marker
import com.example.wanderingelder.database.MarkersDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.example.wanderingelder.model.GeofenceRepo.dataSource

class GeofencesScreenViewModel(application: Application) : ViewModel() {

    lateinit var allMarkers:LiveData<List<Marker>>
    var repo:DatabaseRepo = (DatabaseRepo(dataSource.dao))
    lateinit var displayedMarkers:MutableLiveData<List<Marker>>

    init{
        allMarkers = repo.markers
        displayedMarkers = repo.displayedMarkers

    }

}