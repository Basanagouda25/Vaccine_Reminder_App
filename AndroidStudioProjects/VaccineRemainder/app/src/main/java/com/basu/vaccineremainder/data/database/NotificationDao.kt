package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basu.vaccineremainder.data.model.AppNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    // FIX: Add this new function to get notifications for a specific parent
    @Query("SELECT * FROM notifications WHERE parentId = :parentId ORDER BY timestamp DESC")
    fun getNotificationsForParent(parentId: Int): Flow<List<AppNotification>>

    // This function can be kept for potential admin use, or removed if not needed.
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>
}
