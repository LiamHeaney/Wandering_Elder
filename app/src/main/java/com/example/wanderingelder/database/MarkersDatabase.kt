package com.example.wanderingelder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Marker::class], version =4, exportSchema = false)
abstract class MarkersDatabase : RoomDatabase() {
    abstract val dao:Dao
    companion object{
        @Volatile
        private var INSTANCE: MarkersDatabase? = null
        fun getInstance(context: Context):MarkersDatabase
        {
            synchronized(this)
            {
                var instance = INSTANCE
                if(instance == null)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MarkersDatabase::class.java,
                        "Markers_Table")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}