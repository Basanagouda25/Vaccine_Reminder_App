package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val childId: Int = 0,
    val parentId: Int,
    val name: String,
    val dateOfBirth: String,
    val gender: String,
    val parentEmail: String,
    val providerId: Int?
)
