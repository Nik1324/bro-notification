package com.example.bronotification

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bronotification.alarm.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(): ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val groupDao = MainApp.database.getGroupDao()
    val notificationDao = MainApp.database.getNotificationDao()


    val groupList: Flow<List<Group>> = groupDao.getAllGroups()
    val notificationList: Flow<List<Notification>> = notificationDao.getAllNotifications()
    val groupWithNotifications: Flow<List<GroupWithNotifications>> =
        groupDao.getAllGroupsWithNotifications()



    fun addGroup(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.addGroup(Group(name = title, isEnable = true))
            //alarmScheduler.rescheduleAll(groupDao)
            Log.d(TAG, "addGroup: title=$title")
            AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
        }
    }


    fun editGroup(group: Group) {
        viewModelScope.launch {
            viewModelScope.launch(Dispatchers.IO) {
                groupDao.updateGroup(group)
                Log.d(TAG, "editGroup: groupId=${group.group_id}, isEnable=${group.isEnable}")
                AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
            }
        }
    }

    fun deleteGroup(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.deleteGroup(id)
            Log.d(TAG, "deleteGroup: groupId=$id")
            AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
        }
    }

    fun getEnableState(id: Int): Flow<Boolean?> {
        return groupDao.getEnableStateGroup(id)
    }

    fun setEnableState(id: Int, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.setEnableState(id, isEnabled)
            Log.d(TAG, "setEnableState: groupId=$id, isEnabled=$isEnabled")
            AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.addNotification(notification)
            Log.d(
                TAG,
                "addNotification: groupId=${notification.group_id}, title=${notification.title}"
            )
            AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
        }
    }

    fun editNotification(notification: Notification) {
        viewModelScope.launch() {
            viewModelScope.launch(Dispatchers.IO) {
                notificationDao.updateNotification(notification)
                Log.d(TAG, "editNotification: notificationId=${notification.notification_id}")
                AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
            }
        }
    }
    fun deleteNotification(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.deleteNotification(id)
            AlarmScheduler.cancelNotification(MainApp.instance.applicationContext, id)
            Log.d(TAG, "deleteNotification: notificationId=$id")
            AlarmScheduler.rescheduleAll(MainApp.instance.applicationContext)
        }
    }

    fun getSingleState(id: Int): Flow<Boolean?> {
        return notificationDao.getSingleStateGroup(id)
    }


}