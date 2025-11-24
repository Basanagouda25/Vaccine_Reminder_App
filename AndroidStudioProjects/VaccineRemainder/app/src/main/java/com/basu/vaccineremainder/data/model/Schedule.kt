package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val scheduleId: Int = 0,
    val childId: Int,                 // Foreign key to Child.childId
    val vaccineId: Int,               // Foreign key to Vaccine.vaccineId
    val dueDate: String,              // Scheduled vaccination date (format: yyyy-MM-dd)
    val status: String                // "Pending", "Completed", "Missed"
)
