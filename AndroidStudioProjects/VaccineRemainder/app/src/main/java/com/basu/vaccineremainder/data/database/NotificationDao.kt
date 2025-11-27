package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.basu.vaccineremainder.data.model.AppNotification

@Dao
interface NotificationDao {

    @Insert
    suspend fun insertNotification(notification: AppNotification)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotifications(): List<AppNotification>
}
