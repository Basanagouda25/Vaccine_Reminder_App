package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.basu.vaccineremainder.data.model.Schedule

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSchedules(scheduleList: List<Schedule>)

    @Query("SELECT * FROM schedule WHERE childId = :childId")
    suspend fun getSchedulesForChild(childId: Int): List<Schedule>

    @Query("SELECT * FROM schedule WHERE scheduleId = :scheduleId LIMIT 1")
    suspend fun getScheduleById(scheduleId: Int): Schedule?

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Query("UPDATE schedule SET status = :newStatus WHERE scheduleId = :scheduleId")
    suspend fun updateStatus(scheduleId: Int, newStatus: String)
}
