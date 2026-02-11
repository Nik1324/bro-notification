package com.example.bronotification.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bronotification.Notification
import com.example.bronotification.Group
import com.example.bronotification.GroupWithNotifications
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group` ORDER BY group_id DESC")
    fun getAllGroups() : Flow<List<Group>>

    @Insert
    fun addGroup(group: Group)

    @Query("Delete FROM `Group` where group_id = :id")
    fun deleteGroup(id: Int)

    @Update
    suspend fun updateGroup(group: Group)

    @Transaction
    @Query("SELECT * FROM `Group`")
    fun getAllGroupsWithNotifications(): Flow<List<GroupWithNotifications>>

    @Transaction
    @Query("SELECT * FROM `Group`")
    suspend fun getAllGroupsWithNotificationsForAlarm(): List<GroupWithNotifications>

    @Query("SELECT isEnable FROM `Group` where group_id = :id")
    fun getEnableStateGroup(id: Int): Flow<Boolean?>

    @Query("UPDATE `Group` SET isEnable = :isEnabled WHERE group_id = :id")
    suspend fun setEnableState(id: Int, isEnabled: Boolean)

    @Query("SELECT * FROM `Group` WHERE group_id = :id")
    suspend fun getGroupById(id: Int): Group?
}