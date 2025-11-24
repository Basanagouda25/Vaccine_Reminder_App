package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val childId: Int = 0,
    val parentId: Int,            // Foreign key linking to User.userId
    val name: String,
    val dateOfBirth: String,      // later it is convert to LocalDate or long
    val gender: String            // "Male", "Female", "Other"
)
