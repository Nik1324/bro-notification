package com.example.bronotification

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Group(
    @PrimaryKey(autoGenerate = true)
    val group_id: Int = 0,
    val name: String = "",
    val isEnable: Boolean
)

data class GroupWithNotifications(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "group_id"
    )
    val notifications: List<Notification>
)