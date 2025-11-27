package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provider")
data class Provider(
    @PrimaryKey(autoGenerate = true)
    val providerId: Int = 0,

    val name: String,
    val email: String,
    val password: String,
    val clinicName: String,
    val phone: String
)
