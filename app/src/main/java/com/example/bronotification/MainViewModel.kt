package com.example.bronotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class MainViewModel(): ViewModel() {
    val groupDao = MainApp.database.getGroupDao()
    val notificationDao = MainApp.database.getNotificationDao()


    val groupList : Flow<List<Group>> = groupDao.getAllGroups()
    val notificationList : Flow<List<Notification>> = notificationDao.getAllNotifications()
    val groupWithNotifications: Flow<List<GroupWithNotifications>> = groupDao.getAllGroupsWithNotifications()


    fun addGroup(title : String){
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.addGroup(Group(name = title, isEnable = true))
            //alarmScheduler.rescheduleAll(groupDao)
        }
    }



    fun editGroup(group: Group) {
        viewModelScope.launch {
            groupDao.updateGroup(group)
        }
    }

    fun deleteGroup(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.deleteGroup(id)
        }
    }

    fun getEnableState(id: Int): Flow<Boolean?> {
        return groupDao.getEnableStateGroup(id)
    }

    fun setEnableState(id: Int, isEnabled: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            groupDao.setEnableState(id, isEnabled)
        }
    }

    fun addNotification(notification: Notification){
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.addNotification(notification)
        }
    }

    fun editNotification(notification: Notification){
        viewModelScope.launch() {
            notificationDao.updateNotification(notification)
        }
    }

    fun deleteNotification(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.deleteNotification(id)
        }
    }

    fun getSingleState(id: Int): Flow<Boolean?> {
        return notificationDao.getSingleStateGroup(id)
    }




}