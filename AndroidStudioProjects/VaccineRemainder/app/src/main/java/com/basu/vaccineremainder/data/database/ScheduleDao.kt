package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.basu.vaccineremainder.data.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSchedules(scheduleList: List<Schedule>)

    // The CORRECTED version for ScheduleDao.kt
    @Query("SELECT * FROM schedule WHERE childId = :childId")
    fun getSchedulesForChild(childId: Long): Flow<List<Schedule>> // <-- Remove suspend, add Flow


    @Query("SELECT * FROM schedule WHERE scheduleId = :scheduleId LIMIT 1")
    suspend fun getScheduleById(scheduleId: Int): Schedule?

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Query("UPDATE schedule SET status = :newStatus WHERE scheduleId = :scheduleId")
    suspend fun updateStatus(scheduleId: Int, newStatus: String)

    @Query("UPDATE schedule SET dueDate = :newDate WHERE scheduleId = :scheduleId")
    suspend fun updateDueDate(scheduleId: Int, newDate: String)


    @Query("SELECT * FROM schedule")
    suspend fun getAllSchedules(): List<Schedule>

}
