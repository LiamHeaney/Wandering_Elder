package com.example.wanderingelder.addGeofenceScreen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wanderingelder.geofences.screen.GeofencesScreenViewModel

class AddGeofenceScreenViewModelFactory(private val application: Application): ViewModelProvider.Factory{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddGeofenceScreenViewModel(application) as T
    }


}