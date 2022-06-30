package com.example.wanderingelder.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseRepo(private val dao: Dao) {
    val markers: LiveData<List<Marker>> = dao.getLiveDataMarkers()
    val displayedMarkers=MutableLiveData<List<Marker>>()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun insertMarker(marker: Marker)
    {
        coroutineScope.launch{
            dao.insert(marker)
        }
    }
    fun deleteMarker(name:String)
    {
        coroutineScope.launch{
            dao.delete(name)
        }
    }
}