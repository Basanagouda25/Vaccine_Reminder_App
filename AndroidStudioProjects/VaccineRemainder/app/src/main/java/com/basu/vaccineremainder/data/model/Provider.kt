package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "provider")
data class Provider(
    @PrimaryKey
    @DocumentId
    val providerId: String = "",

    val name: String = "",
    val email: String = "",
    val clinicName: String = "",
    val phone: String = "",

    val password: String = ""
)
