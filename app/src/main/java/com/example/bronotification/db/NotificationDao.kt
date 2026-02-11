package com.example.bronotification.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bronotification.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM Notification ORDER BY notification_id DESC")
    fun getAllNotifications() : Flow<List<Notification>>

    @Insert
    fun addNotification(notification: Notification)

    @Query("Delete FROM Notification where notification_id = :id")
    fun deleteNotification(id : Int)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Query("SELECT isSingle FROM Notification where notification_id = :id")
    fun getSingleStateGroup(id: Int): Flow<Boolean?>

    // 🔴 НУЖНО ДЛЯ BOOT и ALARM
    @Query("SELECT * FROM Notification")
    suspend fun getAllOnce(): List<Notification>

    @Query("SELECT * FROM Notification WHERE notification_id = :id")
    suspend fun getNotificationById(id: Int): Notification?
}