package com.example.bronotification.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.bronotification.Group
import com.example.bronotification.MainApp
import com.example.bronotification.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationId = intent.getIntExtra(AlarmScheduler.EXTRA_NOTIFICATION_ID, -1)
                if (notificationId == -1) {
                    Log.w(TAG, "onReceive: missing notification id")
                    return@launch
                }

                val notificationDao = MainApp.database.getNotificationDao()
                val groupDao = MainApp.database.getGroupDao()

                val notification = notificationDao.getNotificationById(notificationId)

                if (notification == null) {
                    Log.d(TAG, "onReceive: notificationId=$notificationId already removed")
                    AlarmScheduler.cancelNotification(context, notificationId)
                    return@launch
                }

                val group = groupDao.getGroupById(notification.group_id)
                if (group == null || !group.isEnable) {
                    Log.d(TAG, "onReceive: skip notificationId=$notificationId, group disabled or missing")
                    AlarmScheduler.cancelNotification(context, notificationId)
                    return@launch
                }

                showSystemNotification(context, notification, group)
                AlarmScheduler.scheduleNotification(context, notification)
            } catch (e: Exception) {
                Log.e(TAG, "onReceive error", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showSystemNotification(context: Context, notification: Notification, group: Group) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Log.w(TAG, "showSystemNotification: POST_NOTIFICATIONS not granted")
                return
            }
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val systemNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(group.name)
            .setContentText(notification.title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(notification.notification_id, systemNotification)
        Log.d(TAG, "showSystemNotification: notificationId=${notification.notification_id}")
    }

    companion object {
        private const val TAG = "AlarmReceiver"
        private const val CHANNEL_ID = "alarm_channel"
        private const val CHANNEL_NAME = "Alarm reminders"
    }
}
