package com.example.bronotification

import android.icu.util.Calendar
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Time


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["group_id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE //  удалять уведомления при удалении группы
        )
    ],
    indices = [Index("group_id")]
)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    var notification_id: Int = 0,
    var group_id: Int,
    var title: String = "",
    var mo: Boolean,
    var tu: Boolean,
    var we: Boolean,
    var th: Boolean,
    var fr: Boolean,
    var sa: Boolean,
    var su: Boolean,
    var start_time: Int,
    var end_time: Int,
    var period: Int,
    var isSingle: Int,
)
fun Notification.getActiveDays(): List<Int> {
    val days = mutableListOf<Int>()
    if (mo) days.add(Calendar.MONDAY)     // 2
    if (tu) days.add(Calendar.TUESDAY)    // 3
    if (we) days.add(Calendar.WEDNESDAY)  // 4
    if (th) days.add(Calendar.THURSDAY)   // 5
    if (fr) days.add(Calendar.FRIDAY)     // 6
    if (sa) days.add(Calendar.SATURDAY)   // 7
    if (su) days.add(Calendar.SUNDAY)     // 1
    return days
}

val Notification.isSingleMode: Boolean
    get() = isSingle == 1