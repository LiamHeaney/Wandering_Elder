package com.example.wanderingelder.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import java.time.LocalDateTime

@Dao
interface Dao {

    @Insert
    suspend fun insert(marker: Marker)

    @Update
    suspend fun update(marker: Marker)

    @Query("SELECT * FROM MARKERS_TABLE WHERE name = :markerName")
    suspend fun get(markerName:String):Marker?

    @Query("DELETE FROM MARKERS_TABLE")
    suspend fun clearDatabase()

    @Query("SELECT * FROM MARKERS_TABLE ORDER BY id DESC")
    fun getAllMarkers():List<Marker>

    @Query("SELECT * FROM MARKERS_TABLE ORDER BY id DESC")
    fun getLiveDataMarkers():LiveData<List<Marker>>

    @Query("SELECT " +
            "CASE WHEN EXISTS(" +
                "SELECT * FROM MARKERS_TABLE " +
                "WHERE " +
                "ABS(-1 <:latitude - latitude) = 1 " +
                "AND " +
                "ABS(-1<:longitude - longitude < 1)) " +
            "THEN 'TRUE' " +
            "ELSE 'FALSE' " +
            "END")
    fun locationConflict(latitude:Double, longitude:Double):Boolean

    @Query("SELECT COUNT(*) FROM MARKERS_TABLE")
    fun getSize():Int

    @Query("DELETE FROM MARKERS_TABLE WHERE :name=name")
    fun delete(name:String)

    @Query("SELECT " +
            "CASE WHEN EXISTS(" +
                "SELECT * FROM MARKERS_TABLE " +
                "WHERE " +
                "name = :name " +
            ") "+
            "THEN 'TRUE' " +
            "ELSE 'FALSE' " +
            "END")
    fun checkIfNameAlreadyPresent(name:String):Boolean
}