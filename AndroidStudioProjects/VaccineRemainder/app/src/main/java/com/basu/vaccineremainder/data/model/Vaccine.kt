package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines")
data class Vaccine(
    @PrimaryKey(autoGenerate = true)
    val vaccineId: Int = 0,
    val childId: Long = 0,
    val vaccineName: String,
    val recommendedAgeText: String,
    val recommendedAgeMonths: Int,
    val description: String,
    val givenDate: String?,
    val dueDate: String?,
    val isCompleted: Boolean
)

