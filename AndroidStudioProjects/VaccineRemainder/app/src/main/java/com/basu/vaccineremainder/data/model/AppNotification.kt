package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val notificationId: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long,
    val parentId: Int
)
