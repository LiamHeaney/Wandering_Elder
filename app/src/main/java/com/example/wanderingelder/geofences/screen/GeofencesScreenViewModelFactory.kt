package com.example.wanderingelder.geofences.screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GeofencesScreenViewModelFactory(private val application:Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GeofencesScreenViewModel(application) as T
    }


}