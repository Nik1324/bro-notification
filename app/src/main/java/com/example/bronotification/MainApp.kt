package com.example.bronotification

import android.app.Application
import androidx.compose.runtime.LaunchedEffect
import androidx.room.Room
import com.example.bronotification.db.Database
import com.example.bronotification.db.GroupDao

class MainApp : Application() {

    companion object {
        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            Database.NAME
        ).build()
    }

}