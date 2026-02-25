package com.example.bronotification

import android.app.Application
import androidx.compose.runtime.LaunchedEffect
import android.util.Log
import androidx.room.Room
import com.example.bronotification.alarm.AlarmScheduler
import com.example.bronotification.db.Database
import com.example.bronotification.db.GroupDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainApp : Application() {

    companion object {
        lateinit var instance: MainApp
        lateinit var database: Database
        private const val TAG = "MainApp"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            Database.NAME
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "onCreate: database initialized, sync alarms from DB")
            AlarmScheduler.rescheduleAll(applicationContext)
        }
    }

}
