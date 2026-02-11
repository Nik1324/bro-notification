package com.example.bronotification.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bronotification.Group
import com.example.bronotification.Notification


@Database(entities = [Group::class, Notification::class], version = 1)
abstract class Database : RoomDatabase(){

    companion object {
        const val NAME = "Notif_DB"
    }

    abstract fun getNotificationDao() : NotificationDao
    abstract fun getGroupDao(): GroupDao


}
