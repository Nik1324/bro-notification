package com.example.bronotification.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bronotification.MainApp
import com.example.bronotification.Notification
import com.example.bronotification.getActiveDays
import com.example.bronotification.isSingleMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.ceil

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"
    private const val REQUEST_CODE_BASE = 40_000
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun rescheduleAll(context: Context) {
        val groupDao = MainApp.database.getGroupDao()
        val groups = groupDao.getAllGroupsWithNotificationsForAlarm()

        Log.d(TAG, "rescheduleAll: groups=${groups.size}")

        groups.forEach { groupWithNotifications ->
            groupWithNotifications.notifications.forEach { notification ->
                cancelNotification(context, notification.notification_id)

                if (groupWithNotifications.group.isEnable) {
                    scheduleNotification(context, notification)
                } else {
                    Log.d(
                        TAG,
                        "rescheduleAll: skip notificationId=${notification.notification_id}, groupId=${groupWithNotifications.group.group_id} disabled"
                    )
                }
            }
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, notificationId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d(TAG, "cancelNotification: notificationId=$notificationId")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, notification: Notification) {
        val nextTriggerAt = calculateNextTrigger(notification) ?: run {
            Log.d(TAG, "scheduleNotification: no next trigger, notificationId=${notification.notification_id}")
            cancelNotification(context, notification.notification_id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, notification.notification_id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerAt, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerAt, pendingIntent)
        }

        Log.d(TAG, "scheduleNotification: notificationId=${notification.notification_id}, triggerAt=$nextTriggerAt")
    }

    private fun buildPendingIntent(context: Context, notificationId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
            .putExtra(EXTRA_NOTIFICATION_ID, notificationId)

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BASE + notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateNextTrigger(notification: Notification, now: LocalDateTime = LocalDateTime.now()): Long? {
        val activeDays = notification.getActiveDays()
        if (activeDays.isEmpty()) {
            return null
        }

        val today = now.toLocalDate()

        var best: LocalDateTime? = null
        for (dayOffset in 0..7) {
            val candidateDate = today.plusDays(dayOffset.toLong())
            val calendarDay = candidateDate.toCalendarDayInt()
            if (!activeDays.contains(calendarDay)) continue

            val candidate = if (notification.isSingleMode) {
                candidateForSingle(notification, candidateDate, now)
            } else {
                candidateForPeriod(notification, candidateDate, now)
            }

            if (candidate != null && (best == null || candidate.isBefore(best))) {
                best = candidate
            }
        }

        return best?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun candidateForSingle(
        notification: Notification,
        date: LocalDate,
        now: LocalDateTime
    ): LocalDateTime? {
        val time = minutesToLocalTime(notification.start_time)
        val dateTime = LocalDateTime.of(date, time)
        return if (dateTime.isAfter(now)) dateTime else null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun candidateForPeriod(
        notification: Notification,
        date: LocalDate,
        now: LocalDateTime
    ): LocalDateTime? {
        val startMinutes = notification.start_time
        val endMinutes = notification.end_time
        val periodMinutes = notification.period.coerceAtLeast(1)

        if (startMinutes > endMinutes) return null

        val startTime = minutesToLocalTime(startMinutes)
        val endTime = minutesToLocalTime(endMinutes)

        val startDateTime = LocalDateTime.of(date, startTime)
        val endDateTime = LocalDateTime.of(date, endTime)

        if (endDateTime.isBefore(now)) return null

        val diffSeconds = if (startDateTime.isBefore(now)) {
            java.time.Duration.between(startDateTime, now).seconds
        } else {
            0L
        }

        val periodSeconds = periodMinutes * 60.0
        val steps = if (diffSeconds <= 0L) {
            0L
        } else {
            ceil(diffSeconds / periodSeconds).toLong()
        }

        var candidate = startDateTime.plusMinutes(steps * periodMinutes)
        if (candidate.isBefore(now)) {
            candidate = candidate.plusMinutes(periodMinutes.toLong())
        }

        return if (!candidate.isBefore(now) && !candidate.isAfter(endDateTime)) {
            candidate
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun LocalDate.toCalendarDayInt(): Int {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> android.icu.util.Calendar.MONDAY
            DayOfWeek.TUESDAY -> android.icu.util.Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> android.icu.util.Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> android.icu.util.Calendar.THURSDAY
            DayOfWeek.FRIDAY -> android.icu.util.Calendar.FRIDAY
            DayOfWeek.SATURDAY -> android.icu.util.Calendar.SATURDAY
            DayOfWeek.SUNDAY -> android.icu.util.Calendar.SUNDAY
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun minutesToLocalTime(minutes: Int): LocalTime {
        val safeMinutes = minutes.coerceIn(0, 24 * 60 - 1)
        return LocalTime.of(safeMinutes / 60, safeMinutes % 60)
    }
}
